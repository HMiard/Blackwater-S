package com.hmiard.blackwater.screens.DefaultAppScreen;


import com.hmiard.blackwater.utils.BlackwaterApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class DefaultAppScreenPresenter extends BlackwaterApp implements Initializable {

    @FXML
    public StackPane defaultApp;
    @FXML
    public Button defaultAppCloseButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.wrapper = defaultApp;
        defaultAppCloseButton.setOnMouseClicked(e -> close());
    }
}
