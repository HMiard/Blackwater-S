package com.hmiard.blackwater.projects;

import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.screens.MainScreen.MainScreenPresenter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;


public class ProjectBlock extends StackPane {

    public Project project;
    public int index;
    public Text projectName;
    public Text projectPath;
    public Button openDirectory;
    public Button loadProject;

    protected MainScreenPresenter parent;

    public ProjectBlock(Project project, int index, MainScreenPresenter p){

        this.parent = p;
        this.setId(project.getName() + "ProjectBlock");
        this.project = project;
        this.index = index;
        this.setPrefHeight(50);
        this.setPrefWidth(180);


        this.projectName = new Text(project.getName().toUpperCase());
        this.projectName.setFont(Font.font(Start.fonts.get(0)));
        this.projectName.setWrappingWidth(100);
        setAlignment(projectName, Pos.CENTER_LEFT);
        setMargin(projectName, new Insets(-15,0,0,10));
        this.getChildren().add(projectName);

        String path = project.getPath();
        if (path.length() > 30)
            path = "..." + path.substring(path.length() - 30);

        this.projectPath = new Text(path);
        this.projectPath.setFont(Font.font(Start.fonts.get(0), 10));
        this.projectPath.setWrappingWidth(160);
        setAlignment(projectPath, Pos.CENTER_LEFT);
        setMargin(projectPath, new Insets(30,0,0,10));
        this.getChildren().add(projectPath);

        this.openDirectory = new Button("", new ImageView(new Image(Start.class.getResource("content/icons/folder-open-mini.png").toString())));
        setAlignment(openDirectory, Pos.CENTER_LEFT);
        setMargin(openDirectory, new Insets(-15,0,0,120));
        this.getChildren().add(openDirectory);

        this.loadProject = new Button("", new ImageView(new Image(Start.class.getResource("content/icons/settings-mini.png").toString())));
        setAlignment(loadProject, Pos.CENTER_LEFT);
        setMargin(loadProject, new Insets(-15,0,0,150));
        this.getChildren().add(loadProject);

        setCss();
        setInteractions();
    }


    public void setInteractions(){

        this.openDirectory.setOnMouseClicked(e -> {
            try {
                Builder.openPath(this.project.getPath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        this.loadProject.setOnMouseClicked(e -> parent.loadProjectApp("project", this.project));
    }


    public void setCss(){

        this.projectName.setStyle(
                "-fx-fill: #4892CE;"
        );
        this.projectPath.setStyle(
                "-fx-fill: #666;"
        );
        this.openDirectory.setStyle(
                "-fx-opacity: 0.7;" +
                "-fx-background-color: transparent;"
        );
        this.loadProject.setStyle(
                "-fx-opacity: 0.7;" +
                        "-fx-background-color: transparent;"
        );
    }
}
