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

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.OutputStream;

public class ConsoleEmulator extends OutputStream {

    public TextArea console;
    public String shell;
    public BlackwaterApp app;

    public ConsoleEmulator(TextArea console, String shell, BlackwaterApp app){

        this.shell = shell;
        this.app = app;

        console.textProperty().addListener((observable, oldValue, newValue) -> {
            console.setScrollTop(Double.MAX_VALUE);
        });
        this.console = console;
    }

    public ConsoleEmulator(TextArea console, BlackwaterApp app){
        this(console, "", app);
    }

    public void push(String text){

        Platform.runLater(() -> {
            String content = console.getText();
            console.setText(shell+content+text);
            console.appendText("");
        });
    }

    @Override
    public void write(int b) throws IOException {
        if (b <= 0) return;
        int[] bytes = {b};
        write(bytes, 0, bytes.length);
    }

    public void write(int[] bytes, int offset, int length){
        String out = new String(bytes, offset, length);
        push(out);
    }

    public void clean() {
        Platform.runLater(() -> console.setText(""));
    }
}
