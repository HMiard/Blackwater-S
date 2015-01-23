package com.hmiard.blackwater.utils;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;

/**
 * Generic wrapper class for file parsing tasks.
 */
public class Parser {

    public static Properties conf;

    /**
     * Getting the configuration.
     *
     * @return Properties
     * @throws IOException
     */
    public Properties getConfiguration() throws IOException {

        Properties prop = new Properties();
        prop.load(new FileInputStream("resources/conf/conf.properties"));

        return prop;
    }

    /**
     * Shorthand updating method.
     */
    public void updateConfiguration(){

        try {
            Parser.conf = getConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the current configuration in the conf file.
     */
    public static void flushConfiguration(){

        try {
            FileOutputStream conf = new FileOutputStream("resources/conf/conf.properties");
            Parser.conf.store(conf, "");
            conf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parsing a project, checking all the servers.
     *
     * @param path String
     * @return ArrayList
     */
    public static ArrayList<String> parseProjectRoot(String path){

        ArrayList<String> servers = new ArrayList<>();

        File src = new File(path+"\\src");
        File mainClass;
        File bootstrap;
        if (!src.exists()) return null;

        File[] files = src.listFiles();
        if (files == null) return null;

        for (File f : files)
            if (f.isDirectory() && f.getName().matches("^[a-z|A-Z]*Server$")){
                mainClass = new File(f.getAbsolutePath() + "\\" + f.getName()+".php");
                bootstrap = new File(f.getAbsolutePath() + "\\bin\\init.php");
                if (mainClass.exists() && bootstrap.exists())
                    servers.add(f.getName().replace("Server", ""));
            }

        return servers;
    }


    /**
     * Checking if a directory looks like a Blackwater project.
     *
     * @param directory File
     * @return Boolean
     */
    public static Boolean isABlackwaterDirectory(File directory){

        if (directory.exists() && directory.isDirectory()){
            File tokens = new File(directory.getAbsolutePath() + "\\.blackwater\\tokens.properties");
            File composerArchive = new File(directory.getAbsolutePath() + "\\bin\\composer.phar");

            if (tokens.exists() && composerArchive.exists())
                return true;
        }

        return false;
    }


    /**
     * Creating a formatted string from a list of objects.
     *
     * @param token String
     * @param list ArrayList
     * @return String
     */
    public static String parseArrayToStringList(String token, ArrayList<?> list){

        StringBuilder builder = new StringBuilder();
        for (Object o : list){
            builder.append(o.toString());
            if (list.indexOf(o) != list.size()-1)
                builder.append(token);
        }
        return builder.toString();
    }

    /**
     * Creating an array list of string from a formatted string.
     *
     * @param token String
     * @param subject String
     * @return ArrayList
     */
    public static ArrayList<String> parseStringToArrayList(String token, String subject){

        ArrayList<String> ret = new ArrayList<>();
        Collections.addAll(ret, subject.split(token));
        return ret;
    }
}
