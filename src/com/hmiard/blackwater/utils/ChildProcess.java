package com.hmiard.blackwater.utils;


import com.developpez.adiguba.shell.ProcessConsumer;
import com.developpez.adiguba.shell.Shell;
import com.hmiard.blackwater.Start;

import java.io.*;

public class ChildProcess implements Runnable {

    public ProcessConsumer consumer;
    public int process;
    public File dir;

    private Shell shell;
    private String cmd;
    private ConsoleEmulator listener;
    private Runnable callback;

    public ChildProcess(String cmd, File dir, ConsoleEmulator listener, Runnable callback) {

        this.shell = new Shell();
        this.shell.setDirectory(dir);
        this.cmd = cmd;
        this.dir = dir;
        this.listener = listener;
        this.callback = callback;
    }

    public ChildProcess(String cmd, File dir, ConsoleEmulator listener) {

        this.shell = new Shell();
        this.shell.setDirectory(dir);
        this.cmd = cmd;
        this.dir = dir;
        this.listener = listener;
    }

    public ChildProcess(String cmd, ConsoleEmulator listener, Runnable callback) {

        this.shell = new Shell();
        this.cmd = cmd;
        this.listener = listener;
        this.callback = callback;
    }

    public ChildProcess(String cmd, ConsoleEmulator listener) {

        this.shell = new Shell();
        this.cmd = cmd;
        this.listener = listener;
    }

    @Override
    public void run(){

        this.consumer =  shell.command(cmd);

        try {
            process = this.consumer.output(listener).consume();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (callback != null)
            callback.run();
    }


    /**
     * Shortcut method to halt a program with simulated ctrl+C
     */
    public void halt(int pid){

        try {
            if (Start.env.equals("WINDOWS"))
                shell.command("taskkill /IM "+pid+" /F").consume();
            else
                shell.command("kill -9 "+pid).consume();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}