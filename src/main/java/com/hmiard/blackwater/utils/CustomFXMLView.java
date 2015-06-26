/*
 * See Airhacks afterburner FXMLView
 */

package com.hmiard.blackwater.utils;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.ResourceBundle.getBundle;

public class CustomFXMLView {

    public final static String DEFAULT_ENDING = "View";
    protected ObjectProperty<Object> presenterProperty;
    protected FXMLLoader fxmlLoader;
    protected String bundleName;
    protected ResourceBundle bundle;
    protected final Function<String, Object> injectionContext;
    protected URL resource;
    protected final static Executor PARENT_CREATION_POOL = Executors.newCachedThreadPool(runnable -> {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    });
    /**
     * Constructs the view lazily (fxml is not loaded) with empty injection
     * context.
     */
    public CustomFXMLView() {
        this(f -> null);
    }
    /**
     *
     * @param injectionContext the function is used as a injection source.
     * Values matching for the keys are going to be used for injection into the
     * corresponding presenter.
     */
    public CustomFXMLView(Function<String, Object> injectionContext) {
        this.injectionContext = injectionContext;
        this.init(getClass(), getFXMLName());
    }
    private void init(Class clazz, final String conventionalName) {
        this.presenterProperty = new SimpleObjectProperty<>();
        this.resource = clazz.getClassLoader().getResource("screens/"+conventionalName);
        this.bundleName = getBundleName();
        this.bundle = getResourceBundle(bundleName);
    }
    FXMLLoader loadSynchronously(final URL resource, ResourceBundle bundle, final String conventionalName) throws IllegalStateException {
        final FXMLLoader loader = new FXMLLoader(resource, bundle);
        loader.setControllerFactory((Class<?> p) -> Injector.instantiatePresenter(p, this.injectionContext));

        try {
            loader.load();
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot load " + conventionalName, ex);
        }
        return loader;
    }
    void initializeFXMLLoader() {
        if (this.fxmlLoader == null) {
            this.fxmlLoader = this.loadSynchronously(resource, bundle, bundleName);
            this.presenterProperty.set(this.fxmlLoader.getController());
        }
    }
    /**
     * Initializes the view by loading the FXML (if not happened yet) and
     * returns the top Node (parent) specified in
     *
     * @return Parent
     */
    public Parent getView() {
        this.initializeFXMLLoader();
        Parent parent = fxmlLoader.getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }
    /**
     * Initializes the view synchronously and invokes and passes the created
     * parent Node to the consumer within the FX UI thread.
     *
     * @param consumer - an object interested in received the Parent as callback
     */
    public void getView(Consumer<Parent> consumer) {
        Supplier<Parent> supplier = this::getView;
        Executor fxExecutor = Platform::runLater;
        CompletableFuture.supplyAsync(supplier, fxExecutor).thenAccept(consumer);
    }
    /**
     * Creates the view asynchronously using an internal thread pool and passes
     * the parent node withing the UI Thread.
     *
     *
     * @param consumer - an object interested in received the Parent as callback
     */
    public void getViewAsync(Consumer<Parent> consumer) {
        PARENT_CREATION_POOL.execute(() -> getView(consumer));
    }
    /**
     * Scene Builder creates for each FXML document a root container. This
     * method omits the root container (e.g. AnchorPane) and gives you the
     * access to its first child.
     *
     * @return the first child of the AnchorPane
     */
    public Node getViewWithoutRootContainer() {
        final ObservableList<Node> children = getView().getChildrenUnmodifiable();
        if (children.isEmpty()) {
            return null;
        }
        return children.listIterator().next();
    }
    void addCSSIfAvailable(Parent parent) {
        URL uri = getClass().getClassLoader().getResource("screens/"+getStyleSheetName());
        if (uri == null) {
            return;
        }
        String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }
    String getStyleSheetName() {
        return getConventionalName(".css");
    }
    /**
     * In case the view was not initialized yet, the conventional fxml
     * (airhacks.fxml for the AirhacksView and AirhacksPresenter) are loaded and
     * the specified presenter / controller is going to be constructed and
     * returned.
     *
     * @return the corresponding controller / presenter (usually for a
     * AirhacksView the AirhacksPresenter)
     */
    public Object getPresenter() {
        this.initializeFXMLLoader();
        return this.presenterProperty.get();
    }
    /**
     * Does not initialize the view. Only registers the Consumer and waits until
     * the the view is going to be created / the method FXMLView#getView or
     * FXMLView#getViewAsync invoked.
     *
     * @param presenterConsumer listener for the presenter construction
     */
    public void getPresenter(Consumer<Object> presenterConsumer) {
        this.presenterProperty.addListener((ObservableValue<? extends Object> o, Object oldValue, Object newValue) -> {
            presenterConsumer.accept(newValue);
        });
    }
    String getConventionalName(String ending) {
        return getConventionalName() + ending;
    }
    /**
     *
     * @return the name of the view without the "View" prefix in lowerCase. For
     * AirhacksView just airhacks is going to be returned.
     */
    String getConventionalName() {
        String clazz = this.getClass().getSimpleName();
        return stripEnding(clazz);
    }
    String getBundleName() {
        String conventionalName = getConventionalName();
        return this.getClass().getPackage().getName() + "." + conventionalName;
    }
    static String stripEnding(String clazz) {
        if (!clazz.endsWith(DEFAULT_ENDING)) {
            return clazz;
        }
        int viewIndex = clazz.lastIndexOf(DEFAULT_ENDING);
        return clazz.substring(0, viewIndex);
    }
    /**
     *
     * @return the name of the fxml file derived from the FXML view. e.g. The
     * name for the AirhacksView is going to be airhacks.fxml.
     */
    final String getFXMLName() {
        return getConventionalName(".fxml");
    }
    public static ResourceBundle getResourceBundle(String name) {
        try {
            return getBundle(name);
        } catch (MissingResourceException ex) {
            return null;
        }
    }
    /**
     *
     * @return an existing resource bundle, or null
     */
    public ResourceBundle getResourceBundle() {
        return this.bundle;
    }

}
