package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.MainApp;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AdminHomeController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private Label welcomeLabel;

    private AuthToken authToken;
    private ApiService apiService;

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
        welcomeLabel.setText("Welcome, Admin!");
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        apiService.setAuthToken(authToken);
    }

    @FXML
    private void handleManageUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/ManageUsersView.fxml"));
        Parent root = loader.load();
        ManageUsersController manageUsersController = loader.getController();

        // ***** Set the BackendService immediately after loading *****
        manageUsersController.setApiService(apiService);

        mainBorderPane.setCenter(root);
    }

    @FXML
    private void handleManageClubs() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/ManageClubsView.fxml"));
        Parent root = loader.load();
        ManageClubsController manageClubsController = loader.getController();
        manageClubsController.setApiService(apiService); // Set the service immediately
        mainBorderPane.setCenter(root);
    }

    @FXML
    private void handleListUsers() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/ListUsersView.fxml"));
        Parent root = loader.load();
        ListUsersController listUsersController = loader.getController();
        listUsersController.setApiService(apiService);
        mainBorderPane.setCenter(root);
    }

    @FXML
    private void handleListClubs() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/ListClubsView.fxml"));
        Parent root = loader.load();
        ListClubsController listClubsController = loader.getController();
        listClubsController.setApiService(apiService);
        mainBorderPane.setCenter(root);
    }
}