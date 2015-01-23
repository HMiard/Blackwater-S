package com.hmiard.blackwater.projects;

import com.hmiard.blackwater.Start;
import com.hmiard.blackwater.utils.ChildProcess;
import com.hmiard.blackwater.utils.Parser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class Project {

    private String path;
    private String name;
    private ArrayList<String> servers = new ArrayList<>();
    private Date lastUpdate;

    public Properties tokens;
    public Boolean needUpdate = true;

    public HashMap<String, ChildProcess> runningServers = new HashMap<>();

    public Project(String path){

        updateFromPath(path);
    }

    /**
     * Starting a new thread running a Blackwater server.
     * The output is displayed in the console from the server block.
     *
     * @param serverBlock ServerBlock
     */
    public void launchServer(ServerBlock serverBlock) throws IOException {

        if (serverBlock.isRunning || !serverBlock.canBeLaunched) return;
        File directory = new File(path + "\\src\\"+ serverBlock.name + "Server");
        if (!directory.exists() || !directory.isDirectory())
            throw new IOException(directory.getAbsolutePath() + " is not a valid Blackwater server !");

        serverBlock.console.push("\n");
        ChildProcess server = new ChildProcess(
                "php bin/init.php",
                directory,
                serverBlock.console,
                serverBlock::stoppedRunning
        );
        Thread t = new Thread(server);
        ChildProcess e = runningServers.get(serverBlock.name);

        if (e != null){
            killServer(e);
            runningServers.remove(serverBlock.name);
        }
        runningServers.put(serverBlock.name, server);
        t.start();
        serverBlock.started();
    }

    /**
     * Self-explanatory...
     */
    public void killAllRunningServers() throws IOException {

        for (String name : runningServers.keySet())
            killServer(runningServers.get(name));
    }


    /**
     * Killing a running server according to its name.
     *
     * @param serverName String
     */
    public void killServer(String serverName) throws IOException {

        ChildProcess server = runningServers.get(serverName);
        if (server == null) return;

        killServer(server);
    }


    /**
     * Killing a running server.
     *
     * @param server ChildProcess
     */
    public void killServer(ChildProcess server) throws IOException {

        File watcher = new File(server.dir.getAbsolutePath() + "\\bin\\watcher");
        if (!watcher.exists())
            throw new FileNotFoundException("file process not found in " + server.dir.getAbsolutePath() + "\\bin");

        int pid = Integer.parseInt(Builder.readFile(watcher.getAbsolutePath()));
        server.halt(pid);
        watcher.delete();
    }


    /**
     * Updating the project from a new path ?
     *
     * @param path String
     */
    public void updateFromPath(String path) {

        try {
            this.path = path;
            this.tokens = new Properties();
            this.tokens.load(new FileInputStream(path + "\\.blackwater\\tokens.properties"));

            updateFields();
            flushTokens();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
            Start.critical = true;
            Start.displayWarning("Composer file in "+path+" does not seem to be valid, please close the application and fix it.");
        }
    }

    /**
     * Updating the fields from the project configuration file and parser.
     */
    public void updateFields() throws ParseException {

        if (this.tokens == null) return;
        File root = new File(this.path);

        this.name = root.getName();
        this.tokens.setProperty("NAME", this.name);
        this.tokens.setProperty("PATH", this.path);
        this.lastUpdate = new Date();
        this.tokens.setProperty("LAST_UPDATE", this.lastUpdate.toString());

        this.servers.clear();
        ArrayList<String> actualServers = Parser.parseProjectRoot(this.path);
        if (actualServers == null) Collections.addAll(this.servers, tokens.getProperty("SERVERS").split(","));
        else{
            this.servers = actualServers;
            this.tokens.setProperty("SERVERS", Parser.parseArrayToStringList(",", actualServers));
        }
    }


    public void flushTokens() throws IOException, JSONException {

        if (this.tokens == null) return;
        FileOutputStream tokensFile = new FileOutputStream(path + "\\.blackwater\\tokens.properties");
        tokens.store(tokensFile, "Blackwater build version : "+ Start.VERSION);
        tokensFile.close();

        // Updating composer
        File composer = new File(path+"\\composer.json");
        JSONObject composerJSON = new JSONObject();
        JSONObject autoload = new JSONObject();
        JSONObject psr0 = new JSONObject();
        JSONObject require = new JSONObject();

        if (!composer.exists()){

            if (!composer.createNewFile()) return;
            // Re-creating the file if necessary
            composerJSON.put("autoload", autoload);
            composerJSON.put("require", require);

            require.put("blackwater/blackwaterp", Start.blackwaterpVersion);
        }
        else{
            composerJSON = new JSONObject(Builder.readFile(composer.getAbsolutePath()));
            autoload = composerJSON.getJSONObject("autoload");
            psr0 = autoload.getJSONObject("psr-0");
        }

        for (String name : servers)
            try{
                psr0.get(name+"Server");
            }catch (JSONException e){
                psr0.put(name+"Server", "src");
            }

        autoload.put("psr-0", psr0);

        BufferedWriter cw = new BufferedWriter(new FileWriter(composer.getAbsoluteFile()));
        String content = composerJSON.toString(4).replaceAll("\\\\", "");
        cw.write(content);
        cw.close();

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getServers() {
        return servers;
    }

    public void setServers(ArrayList<String> servers) {
        this.servers = servers;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
