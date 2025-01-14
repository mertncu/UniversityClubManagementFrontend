package com.mertncu.universityclubmanagementsystemfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class UserController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label roleLabel;

    private AuthReqResDTO userData;

    public void setUserData(AuthReqResDTO userData) {
        this.userData = userData;
    }

    @FXML
    private void initialize() {
        if (userData != null) {
            nameLabel.setText("Name: " + userData.getName());
            emailLabel.setText("Email: " + userData.getEmail());
            roleLabel.setText("Role: " + userData.getRole());
        }
    }
}
