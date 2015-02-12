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