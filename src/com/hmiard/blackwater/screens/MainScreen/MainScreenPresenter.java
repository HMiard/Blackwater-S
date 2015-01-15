package com.hmiard.blackwater.screens.MainScreen;


import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.projects.Builder;
import com.hmiard.blackwater.projects.Loader;
import com.hmiard.blackwater.projects.Project;
import com.hmiard.blackwater.projects.ProjectBlock;
import com.hmiard.blackwater.screens.DefaultAppScreen.DefaultAppScreenView;
import com.hmiard.blackwater.screens.NewProjectAppScreen.NewProjectAppScreenView;
import com.hmiard.blackwater.screens.ProjectAppScreen.ProjectAppScreenView;
import com.hmiard.blackwater.utils.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class MainScreenPresenter implements Initializable {

    @FXML
    public StackPane appsWrapper;

    @FXML
    public StackPane container;
    @FXML
    public Text footerText;
    @FXML
    public Button closeButton;
    @FXML
    public Button miniButton;
    @FXML
    public StackPane wrapper;
    @FXML
    public GridPane topPanel;
    @FXML
    public StackPane msgDisplay;
    @FXML
    public Text warningMsg;
    @FXML
    public GridPane leftPanel;
    //@FXML
    //public Text gitState;
    @FXML
    public Text phpState;
    @FXML
    public Button phpSyncButton;
    //@FXML
   // public Button gitSyncButton;
    @FXML
    public Button newProjectButton;
    @FXML
    public Button openProjectButton;
    @FXML
    public FlowPane projectsPanel;


    private HashMap<String, CustomFXMLView> apps = new HashMap<>();
    private String currentApp = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        footerText.setText(Start.footer);

        /* CLOSING THE WINDOW */
        closeButton.setOnMouseClicked(e -> Start.exit());

        /* MINIMIZING THE WINDOW */
        miniButton.setOnMouseClicked(e -> {
            Stage s = (Stage)((Node)e.getSource()).getScene().getWindow();
            s.setIconified(true);
        });

        /* UPDATING DEPENDENCIES */
        phpSyncButton.setOnMouseClicked(e -> updateDependenciesDisplay());
        //gitSyncButton.setOnMouseClicked(e -> updateDependenciesDisplay());

        /* FUNCTIONALITIES */
        newProjectButton.setOnMouseClicked(e -> {
            try {
                loadApp("newProject");
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        });
        openProjectButton.setOnMouseClicked(e -> openProject());


        /* CONFIGURATION */
        Start.parser.updateConfiguration();
        if (Parser.conf.getProperty("PROJECTS_ROOT").isEmpty())
            Parser.conf.setProperty("PROJECTS_ROOT", System.getProperty("user.home")+"\\Blackwater");
        Builder.buildFolder(Parser.conf.getProperty("PROJECTS_ROOT"));
        Platform.runLater(this::updateDependenciesDisplay);
        updateProjectsPanel();


        /* INITIALIZATING APPS */
        loadAllApps();
        try {
            loadApp("default");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loading all the apps.
     */
    private void loadAllApps(){

        this.apps.put("default", new DefaultAppScreenView());
        this.apps.put("newProject", new NewProjectAppScreenView());
        this.apps.put("project", new ProjectAppScreenView());
    }


    /**
     * Loading an app according to its name.
     *
     * @param appName String
     */
    public void loadApp(String appName) throws NoSuchFieldException {

        if (BlackwaterApp.busy || !Start.dependenciesOK) return;
        closeApp();
        CustomFXMLView v = apps.get(appName);
        if (v != null){
            if (appsWrapper.lookup("#"+appName+"App") == null)
                appsWrapper.getChildren().add(v.getView());
            BlackwaterApp app = (BlackwaterApp) v.getPresenter();
            app.injectPresenter(this);
            app.load();
            currentApp = appName;
        }
        else
            throw new IndexOutOfBoundsException("App "+appName+" does not exist !");
    }

    /**
     * Loading a project app according to its name.
     *
     * @param appName String
     * @param project Project
     */
    public void loadProjectApp(String appName, Project project){

        if (BlackwaterApp.busy || !Start.dependenciesOK) return;
        closeApp();
        CustomFXMLView v = apps.get(appName);
        if (v != null){
            if (appsWrapper.lookup("#"+appName+"App") == null)
                appsWrapper.getChildren().add(v.getView());
            ProjectApp app = (ProjectApp) v.getPresenter();
            app.injectPresenter(this);
            app.load(project);
            currentApp = appName;
        }
        else
            throw new IndexOutOfBoundsException("App "+appName+" does not exist !");
    }

    /**
     * Closing the currently loaded app.
     */
    public void closeApp(){

        if (BlackwaterApp.busy) return;
        CustomFXMLView v = apps.get(currentApp);
        if (v == null) return;
        BlackwaterApp app = (BlackwaterApp) v.getPresenter();
        app.close();
        currentApp = "";
    }

    /**
     * Updating the texts and buttons in the leftPanel if dependencies are found.
     */
    public void updateDependenciesDisplay(){

        Checker.checkEverything();
        /*if (Parser.conf.getProperty("GIT_AVAILABLE").equals("FALSE")){
            gitSyncButton.setDisable(false);
            gitState.setText("MISSING");
            gitState.setStyle("-fx-fill: #999");
        }else{
            gitSyncButton.setDisable(true);
            gitState.setText("OK");
            gitState.setStyle("-fx-fill: rgba(46, 165, 196, 0.76)");
        }*/
        if (Parser.conf.getProperty("PHP_AVAILABLE").equals("FALSE")){
            phpSyncButton.setDisable(false);
            phpState.setText("MISSING");
            phpState.setStyle("-fx-fill: #999");
        }else{
            phpSyncButton.setDisable(true);
            phpState.setText("OK");
            phpState.setStyle("-fx-fill: rgba(46, 165, 196, 0.76)");
        }

        if (Parser.conf.getProperty("PHP_AVAILABLE").equals("FALSE")
            ){
            Start.displayWarning("Please install and/or update the required modules, and make sure that they can be accessed from the CLI.");
            Start.dependenciesOK = false;
        }
        else{
            Start.dependenciesOK = true;
            Start.discardWarning();
        }
    }


    /**
     * Opening a directory, and loading the corresponding project.
     */
    public void openProject(){

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Parser.conf.getProperty("PROJECTS_ROOT")));
        final File selectedDirectory = directoryChooser.showDialog(Start.wrapper);

        if (selectedDirectory != null){
            if (Parser.isABlackwaterDirectory(selectedDirectory)){

                Project project = new Project(selectedDirectory.getAbsolutePath());
                if (!isKnownProject(project.getName()))
                    Start.projects.add(project);

                loadProjectApp("project", project);
                Loader.updateConfProjects();
                Start.discardWarning();
                updateProjectsPanel();

            }else{
                Start.displayWarning("This is not a valid Blackwater directory.");
            }
        }
    }


    public Boolean isKnownProject(String name){

        for (Project p : Start.projects)
            if (p.getName().equals(name))
                return true;
        return false;
    }

    /**
     * Updating Projects displayed in the left panel.
     */
    public void updateProjectsPanel(){

        if (Start.projects.isEmpty())
            Loader.parseConfProjects();
        projectsPanel.getChildren().clear();

        int i = 0;
        for (Project p : Start.projects)
            projectsPanel.getChildren().add(new ProjectBlock(p, i++, this));
    }
}
