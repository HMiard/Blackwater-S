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
