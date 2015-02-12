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

package com.hmiard.blackwater.utils;


import java.io.IOException;

/**
 * Wrapper class executing some tests.
 */
public class Checker {


    public static void checkEverything(){

        //checkGit();
        checkPHP();
        Parser.flushConfiguration();
    }

    /**
     * Octo-check !
     */
    public static void checkGit(){

        try{
            Runtime.getRuntime().exec("git --version");
        }catch (IOException e){
            Parser.conf.setProperty("GIT_AVAILABLE", "FALSE");
            return;
        }
        Parser.conf.setProperty("GIT_AVAILABLE", "TRUE");
    }

    /**
     * Oliphant check !
     */
    public static void checkPHP(){

        try{
            Runtime.getRuntime().exec("php -v");
        }catch (IOException e){
            Parser.conf.setProperty("PHP_AVAILABLE", "FALSE");
            return;
        }
        Parser.conf.setProperty("PHP_AVAILABLE", "TRUE");
    }

}
