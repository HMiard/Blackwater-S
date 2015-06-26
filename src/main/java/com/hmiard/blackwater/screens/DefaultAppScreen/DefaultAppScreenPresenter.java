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