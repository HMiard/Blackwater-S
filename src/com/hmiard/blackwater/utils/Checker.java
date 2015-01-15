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
