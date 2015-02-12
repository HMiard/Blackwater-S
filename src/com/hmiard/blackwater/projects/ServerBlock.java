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

package com.hmiard.blackwater.projects;

import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.utils.ConsoleEmulator;
import com.hmiard.blackwater.utils.ProjectApp;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

public class ServerBlock extends StackPane {

    public Project project;
    public String name;
    public Text serverName;
    public Button launchButton;
    public Button stopButton;
    public ConsoleEmulator console;
    public Boolean isRunning = false;
    public Boolean canBeLaunched = true;

    protected ProjectApp parent;

    public ServerBlock(Project project, String name, ProjectApp parent){

        this.project = project;
        this.parent = parent;
        this.name = name;
        this.setPrefHeight(210);
        this.setPrefWidth(500);

        this.console = new ConsoleEmulator(new TextArea(), parent);
        this.console.push("@ " + name + "Server > Ready.\n");
        this.console.console.setMaxHeight(170);
        this.console.console.setEditable(false);
        this.console.console.setWrapText(true);
        setAlignment(console.console, Pos.BOTTOM_LEFT);
        setMargin(console.console, new Insets(0,-1,-1,-1));
        this.getChildren().add(console.console);

        this.serverName = new Text((name + " Server").toUpperCase());
        this.serverName.setFont(Font.font(Start.fonts.get(0)));
        this.serverName.setWrappingWidth(100);
        setAlignment(serverName, Pos.TOP_LEFT);
        setMargin(serverName, new Insets(10,0,0,10));
        this.getChildren().add(serverName);


        this.launchButton = new Button("", new ImageView(new Image(Start.class.getResource("content/icons/airplane.png").toString())));
        this.launchButton.setOpacity(0.7);
        setAlignment(launchButton, Pos.TOP_RIGHT);
        setMargin(launchButton, new Insets(0,10,0,0));
        this.launchButton.setOnMouseClicked(e -> {
            try {
                project.launchServer(this);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.getChildren().add(launchButton);


        this.stopButton = new Button("", new ImageView(new Image(Start.class.getResource("content/icons/circle-remove.png").toString())));
        this.stopButton.setOpacity(0.3);
        this.stopButton.setDisable(true);
        setAlignment(stopButton, Pos.TOP_RIGHT);
        setMargin(stopButton, new Insets(0, 50, 0, 0));
        this.stopButton.setOnMouseClicked(e -> {
            try {
                project.killServer(name);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        this.getChildren().add(stopButton);


        setCss();
    }

    /**
     * Called if the server successfully started.
     */
    public void started(){

        Platform.runLater(() -> {
            launchButton.setDisable(true);
            launchButton.setOpacity(0.3);
            stopButton.setDisable(false);
            stopButton.setOpacity(0.7);

            parent.block();
            isRunning = true;
        });
    }


    /**
     * Called in the server ChildProcess callback.
     */
    public void stoppedRunning(){

        Platform.runLater(() -> {
            stopButton.setDisable(true);
            stopButton.setOpacity(0.3);
            launchButton.setDisable(false);
            launchButton.setOpacity(0.7);

            console.push("\nServer stopped.\n");
            isRunning = false;
            project.runningServers.remove(name);
            if (project.runningServers.isEmpty())
                parent.release();
        });
    }


    public void setCss(){

        this.setStyle(
                "-fx-border-style: solid;" +
                "-fx-border-color: #555;" +
                        "-fx-background-color: rgba(50, 50, 50, 0.30);"
        );

        this.serverName.setStyle("-fx-fill: #4892CE;");
        this.console.console.setStyle("-fx-border-color: #555;-fx-border-style: solid;-fx-background-color: transparent");

        this.launchButton.setStyle(
            "-fx-background-color: transparent;"
        );
        this.stopButton.setStyle(
                "-fx-background-color: transparent;"
        );
    }
}
