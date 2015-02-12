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

import com.hmiard.blackwater.screens.MainScreen.MainScreenPresenter;
import javafx.scene.layout.StackPane;

public class BlackwaterApp {

    public StackPane wrapper;
    public Boolean closed = true;
    public static Boolean busy = false;

    protected MainScreenPresenter parent;

    public void injectPresenter(MainScreenPresenter p){
        this.parent = p;
    }

    public void load(){
        if (!closed || busy) return;
        this.wrapper.setDisable(false);
        this.wrapper.setVisible(true);
        this.closed = false;
    }

    public void close(){
        if (closed || busy) return;
        this.wrapper.setDisable(true);
        this.wrapper.setVisible(false);
        this.closed = true;
    }

    public void block(){
        busy = true;
    }

    public void release(){
        busy = false;
    }
}
