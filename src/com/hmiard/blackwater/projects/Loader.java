package com.hmiard.blackwater.projects;


import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.utils.Parser;

import java.io.File;
import java.util.ArrayList;

public class Loader {

    /**
     * At load time, parsing the configuration to check for current projects.
     */
    public static void parseConfProjects(){

        String projectsPaths = Parser.conf.getProperty("PROJECTS");
        Start.projects.clear();

        for (String path : projectsPaths.split(","))
            if (!path.isEmpty()){
                if (Parser.isABlackwaterDirectory(new File(path)))
                    Start.projects.add(new Project(path));
            }
        updateConfProjects();
    }

    /**
     * Updating the configuration.
     */
    public static void updateConfProjects(){

        ArrayList<String> projects = new ArrayList<>();
        for (Project p : Start.projects)
            projects.add(p.getPath());
        Parser.conf.setProperty("PROJECTS", Parser.parseArrayToStringList(",", projects));
        Parser.flushConfiguration();
    }
}
