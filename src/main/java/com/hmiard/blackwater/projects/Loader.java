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
