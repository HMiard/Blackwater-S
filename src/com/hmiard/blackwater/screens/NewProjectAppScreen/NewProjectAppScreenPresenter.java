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

package com.hmiard.blackwater.screens.NewProjectAppScreen;


import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.projects.Project;
import com.hmiard.blackwater.utils.BlackwaterApp;
import com.hmiard.blackwater.projects.Builder;
import com.hmiard.blackwater.utils.ConsoleEmulator;
import com.hmiard.blackwater.utils.Parser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NewProjectAppScreenPresenter extends BlackwaterApp implements Initializable {

    @FXML
    public StackPane newProjectApp;
    @FXML
    public Button newProjectAppCloseButton;
    @FXML
    public TextField newProjectName;
    @FXML
    public TextField newProjectServerNames;
    @FXML
    public TextField newProjectLocation;
    @FXML
    public Button newProjectBrowseButton;
    @FXML
    public Button newProjectGenerateButton;
    @FXML
    public GridPane newProjectForm;
    @FXML
    public TextArea newProjectWizardConsole;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.wrapper = newProjectApp;
        newProjectAppCloseButton.setOnMouseClicked(e -> close());
        newProjectBrowseButton.setOnMouseClicked(e -> browse());


        reset();
    }

    /**
     * Browsing a directory and updating conf.
     */
    public void browse(){

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Parser.conf.getProperty("PROJECTS_ROOT")));
        final File selectedDirectory = directoryChooser.showDialog(Start.wrapper);

        if (selectedDirectory != null){
            Parser.conf.setProperty("PROJECTS_ROOT", selectedDirectory.getAbsolutePath());
            Parser.flushConfiguration();
            newProjectLocation.setText(Parser.conf.getProperty("PROJECTS_ROOT"));
        }
    }

    /**
     * Validating the fields and starting project generation if valid.
     */
    public void validateFieldsAndGenerate(){

        Start.discardWarning();

        if (newProjectName.getText().isEmpty()){
            Start.displayWarning("Please enter a name for your project.");
            newProjectName.setStyle("-fx-border-color: darkred");
            return;
        }else
            newProjectName.setStyle("-fx-border-color: #777");

        if (newProjectServerNames.getText().isEmpty()){
            Start.displayWarning("Please provide at least one server name.");
            newProjectServerNames.setStyle("-fx-border-color: darkred");
            return;
        }else
            newProjectServerNames.setStyle("-fx-border-color: #777");


        String[] serverNames = newProjectServerNames.getText().split(",");
        ArrayList<String> correctedServerNames = new ArrayList<>();

        if (!newProjectName.getText().matches("[A-Za-z0-9 _]+")){
            Start.displayWarning("Please only enter only alphanumerical characters (spaces and '_' are allowed).");
            newProjectName.setStyle("-fx-border-color: darkred");
            return;
        }else
            newProjectName.setStyle("-fx-border-color: #777");


        for (String s : serverNames){
            s = s.replaceAll("\\s", "");
            if (!s.matches("!*[A-Za-z]+")){
                Start.displayWarning("Server names can only contain letters !");
                newProjectServerNames.setStyle("-fx-border-color: darkred");
                return;
            }
            correctedServerNames.add(s.toLowerCase().replace("server", ""));
        }
        newProjectServerNames.setStyle("-fx-border-color: #777");

        File urlCheck = new File(newProjectLocation.getText());
        if (!urlCheck.exists() || !urlCheck.isDirectory() || !urlCheck.isAbsolute()){
            Start.displayWarning("Please enter a valid and absolute path to a directory.");
            newProjectLocation.setStyle("-fx-border-color: darkred");
            return;
        }
        else
            newProjectLocation.setStyle("-fx-border-color: #777");

        File projectChecker = new File(newProjectLocation.getText()+"\\"+newProjectName.getText());
        if (projectChecker.exists() && projectChecker.isDirectory()){
            Start.displayWarning("The directory "+projectChecker.getAbsolutePath()+" already exists !");
            newProjectName.setStyle("-fx-border-color: darkred");
            return;
        }
        else
            newProjectName.setStyle("-fx-border-color: #777");

        // Generating the project !
        this.block();
        newProjectForm.setVisible(false);
        newProjectForm.setDisable(true);
        newProjectGenerateButton.setVisible(false);
        newProjectGenerateButton.setDisable(true);
        newProjectWizardConsole.setVisible(true);
        newProjectAppCloseButton.setDisable(true);

        ConsoleEmulator wizardConsole = new ConsoleEmulator(newProjectWizardConsole, this);

        Builder.buildNewProject(
                newProjectName.getText(),
                correctedServerNames,
                newProjectLocation.getText(),
                wizardConsole
        );
    }

    /**
     * Called once the project is built.
     */
    public void projectCreatedCallback(String projectURL, ConsoleEmulator consoleEmulator){

        // Adding the path to configuration
        String currentProjects = Parser.conf.getProperty("PROJECTS");
        if (currentProjects.isEmpty())
            Parser.conf.setProperty("PROJECTS", projectURL);
        else
            Parser.conf.setProperty("PROJECTS", currentProjects + "," + projectURL);
        Parser.flushConfiguration();

        Platform.runLater(() -> {

            consoleEmulator.push("\nProject created successfully !");
            newProjectAppCloseButton.setDisable(false);

            this.newProjectGenerateButton.setText("Go to project root");
            this.newProjectGenerateButton.setDisable(false);
            this.newProjectGenerateButton.setVisible(true);
            this.newProjectGenerateButton.setOnMouseClicked(e -> {
                try {
                    Builder.openPath(projectURL);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
            Start.projects.add(new Project(projectURL));
            this.parent.updateProjectsPanel();
        });
        this.release();
    }


    /**
     * Cleaning all the fields.
     */
    public void reset(){

        newProjectName.setText("");
        newProjectServerNames.setText("");
        newProjectForm.setVisible(true);
        newProjectForm.setDisable(false);
        newProjectWizardConsole.setVisible(false);
        newProjectWizardConsole.setText("");
        newProjectGenerateButton.setVisible(true);
        newProjectGenerateButton.setDisable(false);
        newProjectGenerateButton.setText("Generate !");
        newProjectGenerateButton.setOnMouseClicked(e -> validateFieldsAndGenerate());
        newProjectAppCloseButton.setDisable(false);
        newProjectLocation.setText(Parser.conf.getProperty("PROJECTS_ROOT"));
    }

    @Override
    public void close() {
        reset();
        super.close();
    }
}
