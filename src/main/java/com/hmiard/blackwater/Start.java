/*
 * Blackwater-S - Server Interface
 * Copyright (C) 2014-2015 Hugo MIARD
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hmiard.blackwater;


import com.hmiard.blackwater.projects.Project;
import com.hmiard.blackwater.screens.MainScreen.MainScreenView;
import com.hmiard.blackwater.utils.Parser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Start extends Application {

    double initialX;
    double initialY;

    public static Stage wrapper;

    public static final int V_WIDTH = 1024;
    public static final int V_HEIGHT = 700;
    public static final String VERSION = "0.1.2";
    public static final String footer = "Blackwater - GNU General Public License v3.0 - HMiard Build "+VERSION;
    public static final String blackwaterpVersion = "v0.1.3";
    public static String env;
    public static Boolean critical = false;

    public MainScreenView mainScreen;
    public static Parser parser;
    public static ArrayList<Project> projects = new ArrayList<>();
    public static ArrayList<String> fonts = new ArrayList<>();
    public static Boolean dependenciesOK = true;

    @Override
    public void start(Stage mainW) throws Exception {

        /* FONTS */
        fonts.add("Gil-Sans-MT.ttf");
        fonts.add("Coda-Regular.ttf");

        setEnv();

        try {
            for (String f : fonts){
                InputStream font = Start.class.getClassLoader().getResourceAsStream("content/fonts/" +f);
                if (font != null)
                    Font.loadFont(font, -1);
                else
                    throw new FileNotFoundException(f+" font not found !");
            }
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }



        /* SETTING THE WINDOW */
        wrapper = mainW;
        this.mainScreen = new MainScreenView();
        Start.parser = new Parser();

        mainW.setTitle("Blackwater S");
        mainW.initStyle(StageStyle.UNDECORATED);
        mainW.getIcons().add(new Image(Start.class.getClassLoader().getResourceAsStream("content/img/ico.png")));

        /* SETTING THE SCENE */
        mainW.setScene(new Scene(mainScreen.getView(), V_WIDTH, V_HEIGHT));
        final Node root = mainW.getScene().getRoot();
        this.addDragListeners(root);
        root.setStyle("-fx-background-color: #333");

        mainW.show();
    }

    /**
     * Binding drag events.
     *
     * @param n Node
     */
    public void addDragListeners(final Node n){

        n.setOnMousePressed(me -> {
            initialX = me.getSceneX();
            initialY = me.getSceneY();
        });

        n.setOnMouseDragged(me -> {
            wrapper.setX( me.getScreenX() - initialX );
            wrapper.setY( me.getScreenY() - initialY);
        });
    }

    /**
     * Displaying a warning message.
     *
     * @param warning String
     */
    public static void displayWarning(String warning) {

        Platform.runLater(() -> {
            StackPane msgDisplay = (StackPane) wrapper.getScene().lookup("#msgDisplay");
            Text warningMsg = (Text) wrapper.getScene().lookup("#warningMsg");

            warningMsg.setText(warning);
            msgDisplay.setVisible(true);
        });
    }

    /**
     * Hiding the current warning message.
     */
    public static void discardWarning() {

        if (critical) return;
        Platform.runLater(() -> {
            StackPane msgDisplay = (StackPane) wrapper.getScene().lookup("#msgDisplay");
            Text warningMsg = (Text) wrapper.getScene().lookup("#warningMsg");

            warningMsg.setText("");
            msgDisplay.setVisible(false);
        });
    }

    /**
     * Setting the env static const
     */
    private void setEnv(){
        String osname = System.getProperty("os.name").toLowerCase();
        if (osname.contains("win"))
            env = "WINDOWS";
        else if (osname.contains("nux") || osname.contains("nix") || osname.contains("aix"))
            env = "UNIX";
        else
            env = "MAC";
    }

    @Override
    public void stop() throws Exception {
        exit();
        super.stop();
    }

    /**
     * Clean exit.
     */
    public static void exit(){
        try {
            for (Project p : projects)
                p.killAllRunningServers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Platform.exit();
        System.exit(0);
    }

    /**
     * Launching the app.
     *
     * @param args String[]
     */
    public static void main(String[] args) {

        launch(args);
    }
}
