package com.hmiard.blackwater.projects;

import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.screens.NewProjectAppScreen.NewProjectAppScreenPresenter;
import com.hmiard.blackwater.utils.ChildProcess;
import com.hmiard.blackwater.utils.ConsoleEmulator;
import com.hmiard.blackwater.utils.Parser;
import org.apache.commons.io.FileUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class Builder {

    /**
     * Building a new project.
     *
     * @param projectName String
     * @param serverNames ArrayList
     * @param location String
     */
    public static void buildNewProject(String projectName, ArrayList<String> serverNames, String location, ConsoleEmulator consoleListener){

        try {
            consoleListener.clean();
            consoleListener.push("Project creation started...\n\n");
            projectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
            String projectUrl = location + "\\" + projectName;

            buildFolder(projectUrl);
            buildFolder(projectUrl + "\\bin");
            buildFolder(projectUrl + "\\src");
            buildFolder(projectUrl + "\\.blackwater");

            ArrayList<String> realServerNames = new ArrayList<>();
            for (String name : serverNames)
                    realServerNames.add(name.replace("!", ""));

            // Creating the configuration
            Properties configuration = new Properties();
            File tokens = new File (projectUrl + "\\.blackwater\\tokens.properties");
            configuration.setProperty("PATH", projectUrl);
            configuration.setProperty("NAME", projectName);
            configuration.setProperty("SERVERS", Parser.parseArrayToStringList(",", realServerNames));
            configuration.setProperty("LAST_UPDATE", new Date().toString());
            configuration.store(new FileOutputStream(tokens), "Blackwater build version : "+Start.VERSION);
            consoleListener.push("Tokens generated.\n");


            // Creating composer.json...
            JSONObject composerJSON = new JSONObject();
            JSONObject autoload = new JSONObject();
            JSONObject psr0 = new JSONObject();
            JSONObject require = new JSONObject();
            autoload.put("psr-0", psr0);
            composerJSON.put("autoload", autoload);
            composerJSON.put("require", require);

            require.put("blackwater/blackwaterp", Start.blackwaterpVersion);
            File composer = new File(projectUrl+"\\composer.json");
            if(!composer.createNewFile()){
                consoleListener.push("\nWeird composer stuff happened... Aborting.\n");
                return;
            }


            BufferedWriter cw = new BufferedWriter(new FileWriter(composer.getAbsoluteFile()));
            String content = composerJSON.toString(4).replaceAll("\\\\", "");
            cw.write(content);
            cw.close();
            consoleListener.push("File created : composer.json\n");

            // Creating the servers...
            consoleListener.push("Server creation started...\n");
            for (String name : serverNames)
                if (name.charAt(0) == '!')
                    appendServer(projectUrl, name.replace("!", ""), consoleListener, false);
                else
                    appendServer(projectUrl, name, consoleListener, true);


            // Copying composer.phar
            consoleListener.push("Installing local composer wrapper...\n");
            copyFile(new File("resources/packages/composer.phar"), new File(composer.getParent()+"\\bin\\composer.phar"));

            // Building...
            consoleListener.push("Building dependencies...\n");
            new Thread(new ChildProcess(
                    "php bin/composer.phar install", composer.getParentFile(), consoleListener,
                    () -> {
                        NewProjectAppScreenPresenter presenter = (NewProjectAppScreenPresenter) consoleListener.app;
                        presenter.projectCreatedCallback(projectUrl, consoleListener);
                    }
            )).start();

        }catch (JSONException | IOException e){
            e.printStackTrace();
        }

    }


    /**
     * Appending a new server to a project.
     *
     * @param projectRoot String
     * @param serverName String
     * @param consoleListener ConsoleEmulator
     * @param needsDb Boolean
     */
    public static Boolean appendServer(String projectRoot, String serverName, ConsoleEmulator consoleListener, Boolean needsDb){

        try {
            serverName = serverName.substring(0, 1).toUpperCase() + serverName.substring(1).toLowerCase();
            String shortServerName = serverName;
            serverName += "Server";
            String src = projectRoot + "\\src\\" + serverName;
            File checker = new File(projectRoot+"\\src\\"+serverName);
            File composerJSON = new File(projectRoot+"\\composer.json");

            if (checker.exists() && checker.isDirectory()){
                consoleListener.push("This server already exists ! Operation aborted.");
                return false;
            }
            if (!composerJSON.exists()){
                consoleListener.push("File composer.json is missing ! Operation aborted.");
                return false;
            }

            if (needsDb)
                copyFolder(new File("resources/packages/DefaultApp"), new File(src));
            else
                copyFolder(new File("resources/packages/NoDbApp"), new File(src));


            FileOutputStream writer;
            File core = new File(src+"\\BlackwaterDefaultApp.php");
            File qf = new File(src+"\\DefaultAppQueryFactory.php");
            File bootstrap = new File(src+"\\bin\\init.php");

            String coreContent = readFile(core.getAbsolutePath());
            coreContent = coreContent.replace("BlackwaterDefaultApp", serverName);
            File newCore = new File(src+"\\"+serverName+".php");
            if (newCore.createNewFile() && core.delete()){
                writer = new FileOutputStream(newCore);
                writer.write(coreContent.getBytes());
                writer.flush();
                writer.close();
            }

            if (needsDb){
                String qfContent = readFile(qf.getAbsolutePath());
                qfContent = qfContent.replace("BlackwaterDefaultApp", serverName);
                qfContent = qfContent.replace("DefaultApp", shortServerName);
                File newQf = new File(src+"\\"+shortServerName+"QueryFactory.php");
                if (newQf.createNewFile() && qf.delete()){
                    writer = new FileOutputStream(newQf);
                    writer.write(qfContent.getBytes());
                    writer.flush();
                    writer.close();
                }
            }


            String bootsrapContent = readFile(bootstrap.getAbsolutePath());
            Random r = new Random();
            bootsrapContent = bootsrapContent.replace("Default", shortServerName);
            bootsrapContent = bootsrapContent.replace("8080", String.valueOf(r.nextInt(2000) + 7000));
            writer = new FileOutputStream(bootstrap);
            writer.write(bootsrapContent.getBytes());
            writer.flush();
            writer.close();


            JSONObject composer = new JSONObject(readFile(composerJSON.getAbsolutePath()));
            JSONObject autoload = composer.getJSONObject("autoload");
            JSONObject psr0 = autoload.getJSONObject("psr-0");
            psr0.put(serverName, "src");

            BufferedWriter cw = new BufferedWriter(new FileWriter(composerJSON.getAbsoluteFile()));
            String content = composer.toString(4).replaceAll("\\\\", "");
            cw.write(content);
            cw.close();

            consoleListener.push(serverName+" created !\n");

        }catch (JSONException | IOException e){
            e.printStackTrace();
        }

        return true;
    }


    /**
     * Deleting a server from a project.
     *
     * @param projectRoot String
     * @param serverName String
     * @param listener ConsoleEmulator
     * @return Boolean
     */
    public static Boolean deleteServer(String projectRoot, String serverName, ConsoleEmulator listener){

        serverName = serverName.substring(0, 1).toUpperCase() + serverName.substring(1).toLowerCase();
        serverName += "Server";
        File checker = new File(projectRoot+"\\src\\"+serverName);
        File composerJSON = new File(projectRoot+"\\composer.json");

        if (!checker.exists()){
            listener.push("This server is missing ! Operation aborted.");
            return false;
        }
        if (!composerJSON.exists()){
            listener.push("File composer.json is missing ! Operation aborted.");
            return false;
        }

        try {
            FileUtils.deleteDirectory(checker);

            if (checker.exists()){
                listener.push("Woops ! It seems impossible to delete "+checker.getAbsolutePath());
                return false;
            }


            JSONObject composer = new JSONObject(readFile(composerJSON.getAbsolutePath()));
            JSONObject autoload = composer.getJSONObject("autoload");
            JSONObject psr0 = autoload.getJSONObject("psr-0");

            psr0.remove(serverName);

            BufferedWriter cw = new BufferedWriter(new FileWriter(composerJSON.getAbsoluteFile()));
            String content = composer.toString(4).replaceAll("\\\\", "");
            cw.write(content);
            cw.close();

            listener.push("\n" + serverName + " was deleted successfully !\n");

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Simple directory creation ;
     * won't erase the existing directory if it already exists.
     *
     * @param url String
     */
    public static void buildFolder(String url) {

        File folder = new File(url);
        if (folder.exists() && folder.isDirectory()){
            Start.displayWarning("The directory "+url+" already exists !");
            return;
        }
        if (!folder.mkdir())
            System.out.println("Cannot create the following folder : " + url);
    }


    /**
     * Copying a file by 1/2 Mo blocks
     *
     * @param src File
     * @param dest File
     * @return Boolean
     */
    public static Boolean copyFile(File src, File dest){

        try{
            try (FileInputStream sourceFile = new FileInputStream(src)) {
                FileOutputStream destinationFile = null;

                try {
                    destinationFile = new FileOutputStream(dest);

                    byte buffer[] = new byte[512 * 1024];
                    int n;

                    while ((n = sourceFile.read(buffer)) != -1) {
                        destinationFile.write(buffer, 0, n);
                    }
                } finally {
                    if (destinationFile != null)
                        destinationFile.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Recursively copying a folder.
     *
     * @param src File
     * @param dest File
     * @throws IOException
     */
    public static void copyFolder(File src, File dest) throws IOException{

        if(src.isDirectory()){

            if(!dest.exists())
                dest.mkdir();

            String files[] = src.list();
            for (String file : files)
                copyFolder(new File(src, file), new File(dest, file));

        }else{
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);

            in.close();
            out.close();
        }
    }

    /**
     * Utility fonction. Return an UTF-8 encoded string from a file.
     *
     * @param path String
     * @return String
     * @throws IOException
     */
    public static String readFile(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, StandardCharsets.UTF_8);
    }

    /**
     * Asking the OS to open a directory or a file.
     *
     * @param path String
     * @throws IOException
     */
    public static void openPath(String path) throws IOException {

        if (Desktop.isDesktopSupported()){
            Desktop.getDesktop().open(new File(path));
        }
    }
}
