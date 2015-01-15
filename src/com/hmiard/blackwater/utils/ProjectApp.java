package com.hmiard.blackwater.utils;


import com.hmiard.blackwater.projects.Project;

public abstract class ProjectApp extends BlackwaterApp {

    public static Project loadedProject;

    public abstract void loadProject(Project project);

    public void load(Project project) {
        loadProject(project);
        super.load();
    }
}
