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

package com.hmiard.blackwater.screens.ProjectAppScreen;

import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.projects.Builder;
import com.hmiard.blackwater.projects.Project;
import com.hmiard.blackwater.projects.ServerBlock;
import com.hmiard.blackwater.utils.ChildProcess;
import com.hmiard.blackwater.utils.ConsoleEmulator;
import com.hmiard.blackwater.utils.ProjectApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ProjectAppScreenPresenter extends ProjectApp implements Initializable {

    @FXML
    public StackPane projectApp;
    @FXML
    public Text projectAppName;
    @FXML
    public Button projectAppCloseButton;
    @FXML
    public FlowPane serversPanel;
    @FXML
    public Button projectAppRunAll;
    @FXML
    public Button projectAppCreateNew;
    @FXML
    public Button projectAppServerCreateConfirm;
    @FXML
    public TextField projectAppServerAsker;
    @FXML
    public Button projectAppHaltAll;
    @FXML
    public Button projectAppServerCreateCancel;
    @FXML
    public Button projectAppGoToRoot;
    @FXML
    public Button projectAppDelete;
    @FXML
    public TextField projectAppServerDeleteAsker;
    @FXML
    public Button projectAppServerDeleteConfirm;
    @FXML
    public Button projectAppServerDeleteCancel;
    @FXML
    public ScrollPane projectAppScrollPane;
    @FXML
    public CheckBox projectAppServerCreateDB;

    ArrayList<ServerBlock> servers = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.wrapper = projectApp;
        projectAppServerCreateConfirm.setDisable(true);
        projectAppServerCreateConfirm.setVisible(false);
        projectAppServerCreateCancel.setDisable(true);
        projectAppServerCreateCancel.setVisible(false);
        projectAppServerAsker.setVisible(false);
        projectAppServerDeleteConfirm.setDisable(true);
        projectAppServerDeleteConfirm.setVisible(false);
        projectAppServerDeleteCancel.setDisable(true);
        projectAppServerDeleteCancel.setVisible(false);
        projectAppServerDeleteAsker.setVisible(false);
        projectAppServerCreateDB.setVisible(false);
        projectAppServerCreateDB.setDisable(true);

        projectAppCloseButton.setOnMouseClicked(e -> close());
        projectAppRunAll.setOnMouseClicked(e -> launchAllServers());

        projectAppCreateNew.setOnMouseClicked(e -> buildNewServer(true));
        projectAppServerCreateConfirm.setOnMouseClicked(e -> buildNewServer(false));
        projectAppServerCreateCancel.setOnMouseClicked(e -> displayCreateForm(false));

        projectAppDelete.setOnMouseClicked(e -> deleteServer(true));
        projectAppServerDeleteConfirm.setOnMouseClicked(e -> deleteServer(false));
        projectAppServerDeleteCancel.setOnMouseClicked(e -> displayDeleteForm(false));

        projectAppGoToRoot.setOnMouseClicked(e -> {
            try {
                Builder.openPath(loadedProject.getPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        projectAppHaltAll.setOnMouseClicked(e -> {
            try {
                loadedProject.killAllRunningServers();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }



    @Override
    public void loadProject(Project project) {

        loadedProject = project;
        projectAppName.setText(project.getName());
        reloadServers();

        /*
            Updates on first load only.
         */
        if (project.needUpdate){
            project.needUpdate = false;
            blockEverything(true);
            ConsoleEmulator emulator = servers.get(0).console;

            if (!servers.isEmpty())
                new Thread(new ChildProcess(
                        "php bin/composer.phar update",
                        new File(loadedProject.getPath()),
                        emulator,
                        () -> Platform.runLater(() -> {
                            blockEverything(false);
                            emulator.push("\nDependencies successfully updated !\n");

                        })
                )).start();
        }
    }

    /**
     * Launching all the currently loaded servers.
     */
    public void launchAllServers(){

        try {
            for (ServerBlock s : servers)
                loadedProject.launchServer(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updating the server panel and storing loaded servers.
     */
    public void reloadServers(){

        if (loadedProject == null) return;


        ArrayList<ServerBlock> runningServers = new ArrayList<>();
        for (ServerBlock s : servers)
            if (s.isRunning)
                runningServers.add(s);

        serversPanel.getChildren().clear();
        servers.clear();
        ServerBlock s;

        for (String name : loadedProject.getServers()){

            s = new ServerBlock(loadedProject, name, this);
;           for (ServerBlock e : runningServers)
                if (e.name.toLowerCase().equals(name.toLowerCase()))
                    s = e;
            servers.add(s);
            serversPanel.getChildren().add(s);
        }
    }

    /**
     * Displaying a form, validating the server name, building it.
     *
     * @param form Boolean
     */
    public void buildNewServer(Boolean form){

        if (form) {
            displayCreateForm(true);
            displayDeleteForm(false);
        }
        else {

            Start.discardWarning();
            String serverName = projectAppServerAsker.getText().replaceAll("\\s", "").toLowerCase().replace("server", "");

            if (!serverName.matches("[A-Za-z]+")){
                Start.displayWarning("Please only enter only letters.");
                projectAppServerAsker.setStyle("-fx-border-color: darkred");
                return;
            }
            if (serverExists(serverName)){
                Start.displayWarning("This server already exists.");
                projectAppServerAsker.setStyle("-fx-border-color: darkred");
                return;
            }
            projectAppServerAsker.setStyle("-fx-border-color: #777");

            ConsoleEmulator emulator = new ConsoleEmulator(new TextArea(), this);
            Builder.appendServer(
                    loadedProject.getPath(), serverName, emulator,
                    this.projectAppServerCreateDB.isSelected()
            );
            try {
                loadedProject.updateFields();
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            ServerBlock s = new ServerBlock(loadedProject, serverName, this);
            servers.add(s);
            serversPanel.getChildren().add(s);
            s.launchButton.setDisable(true);
            s.launchButton.setOpacity(0.3);
            s.canBeLaunched = false;
            new Thread(new ChildProcess("php bin/composer.phar update", new File(loadedProject.getPath()), s.console, () -> Platform.runLater(() -> {
                s.console.push("\nThe new server has been successfully created !\n");
                s.launchButton.setDisable(false);
                s.launchButton.setOpacity(0.7);
                s.canBeLaunched = true;
            }))).start();
            displayCreateForm(false);
        }
    }


    public void displayCreateForm(Boolean display){

        projectAppCreateNew.setDisable(display);
        projectAppServerCreateConfirm.setDisable(!display);
        projectAppServerCreateConfirm.setVisible(display);
        projectAppServerCreateCancel.setDisable(!display);
        projectAppServerCreateCancel.setVisible(display);
        projectAppServerCreateDB.setVisible(display);
        projectAppServerCreateDB.setDisable(!display);
        projectAppServerAsker.setVisible(display);
        projectAppServerAsker.setText("");
        projectAppServerAsker.setStyle("-fx-border-color: #777");
        if (display)
            projectAppServerAsker.requestFocus();
    }


    /**
     * Deleting a server from the current project.
     *
     * @param form Boolean
     */
    public void deleteServer(Boolean form){

        if (form) {
            displayDeleteForm(true);
            displayCreateForm(false);
        }
        else {

            Start.discardWarning();
            String serverName = projectAppServerDeleteAsker.getText().replaceAll("\\s", "").toLowerCase().replace("server", "");

            if (!serverExists(serverName)){
                Start.displayWarning("This server does not exist.");
                projectAppServerDeleteAsker.setStyle("-fx-border-color: darkred");
                return;
            }
            ServerBlock s = this.getServerBlockByName(serverName);
            if (s == null) return;
            if (s.isRunning){
                Start.displayWarning("Please shutdown the server before deleting it.");
                projectAppServerDeleteAsker.setStyle("-fx-border-color: darkred");
                return;
            }

            projectAppServerDeleteAsker.setStyle("-fx-border-color: #777");
            scrollToServerBlock(s);
            if (!Builder.deleteServer(loadedProject.getPath(), serverName, s.console))
                return;

            try {
                loadedProject.updateFields();
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }

            s.launchButton.setDisable(true);
            s.launchButton.setOpacity(0.3);
            s.canBeLaunched = false;
            new Thread(new ChildProcess("php bin/composer.phar update", new File(loadedProject.getPath()), s.console, () -> Platform.runLater(() -> {
                s.console.push("\nThe server has been successfully deleted !\n");
                s.launchButton.setDisable(false);
                s.launchButton.setOpacity(0.7);
                s.canBeLaunched = true;
                servers.remove(s);
                serversPanel.getChildren().remove(s);
                reloadServers();
            }))).start();

            displayDeleteForm(false);
        }
    }

    public void displayDeleteForm(Boolean display){

        projectAppDelete.setDisable(display);
        projectAppServerDeleteConfirm.setDisable(!display);
        projectAppServerDeleteConfirm.setVisible(display);
        projectAppServerDeleteCancel.setDisable(!display);
        projectAppServerDeleteCancel.setVisible(display);
        projectAppServerDeleteAsker.setVisible(display);
        projectAppServerDeleteAsker.setText("");
        projectAppServerDeleteAsker.setStyle("-fx-border-color: #777");
        if (display)
            projectAppServerDeleteAsker.requestFocus();
    }

    /**
     * Checking if the current project already has a server named...
     *
     * @param serverName String
     * @return Boolean
     */
    public Boolean serverExists(String serverName){

        for (String name : loadedProject.getServers())
            if (name.toLowerCase().equals(serverName.toLowerCase()))
                return true;
        return false;
    }

    /**
     * Finding the right server block.
     *
     * @param serverName String
     * @return ServerBlock
     */
    public ServerBlock getServerBlockByName(String serverName){

        for (ServerBlock s : this.servers)
            if (s.name.toLowerCase().equals(serverName.toLowerCase()))
                return s;

        return null;
    }


    /**
     * UI swag.
     *
     * @param serverBlock ServerBlock
     */
    public void scrollToServerBlock(ServerBlock serverBlock){

        /**
         * @todo That's not really working...
         */
        /*
        if (serverBlock == null) return;
        int index = servers.indexOf(serverBlock);
        if (index != -1)
            projectAppScrollPane.setVvalue((serverBlock.getHeight() + 30) * index);
        */
    }


    @Override
    public void block() {
        projectAppCloseButton.setDisable(true);
        super.block();
    }

    @Override
    public void release() {
        projectAppCloseButton.setDisable(false);
        super.release();
    }


    public void blockEverything(Boolean block){

        for (Node n : projectApp.getChildren())
            n.setDisable(block);
    }

    @Override
    public void close() {
        super.close();
    }
}
