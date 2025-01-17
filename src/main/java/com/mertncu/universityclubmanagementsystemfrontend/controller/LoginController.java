package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.MainApp;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthReqResDTO;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private ApiService apiService = new ApiService();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        AuthReqResDTO loginRequest = new AuthReqResDTO();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        try {
            AuthReqResDTO response = apiService.login(loginRequest);
            System.out.println("Login Response: " + response);

            if (response.getStatusCode() == 200) {
                // Login successful

                // ***** Store the token *****
                AuthToken authToken = new AuthToken();
                authToken.setAccessToken(response.getAccessToken());
                authToken.setRefreshToken(response.getRefreshToken());
                apiService.setAuthToken(authToken);

                System.out.println("Token received and stored: " + authToken.getAccessToken());
                System.out.println("Login successful. Token: " + authToken.getAccessToken());

                // Check the user's role
                if ("ROLE_ADMIN".equals(response.getRole())) {
                    // Load the AdminHomeController
                    loadAdminHomePage(event, authToken);
                } else {
                    // Handle other roles (e.g., load a UserHomeController)
                    // Correctly load the UserHomePage for non-admin users
                    loadUserHomePage(event, authToken); // Call loadUserHomePage() here
                }
            } else {
                // Login failed
                showAlert(Alert.AlertType.ERROR, "Login Failed", response.getError());
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred during login.");
            e.printStackTrace();
        }
    }

    private void loadAdminHomePage(ActionEvent event, AuthToken authToken) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/AdminHomeView.fxml"));
        Parent root = loader.load();

        AdminHomeController adminHomeController = loader.getController();
        adminHomeController.setAuthToken(authToken); // Pass the token
        adminHomeController.setApiService(apiService); // Pass the service

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        // Pencere boyutlarını ayarlıyoruz
        stage.setWidth(900);  // Genişlik
        stage.setHeight(600); // Yükseklik

        // Sahneyi güncelliyoruz
        stage.setScene(scene);
        stage.show();
    }

    private void loadUserHomePage(ActionEvent event, AuthToken authToken) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/UserHomeView.fxml"));
        Parent root = loader.load();

        UserHomeController userHomeController = loader.getController();
        userHomeController.setAuthToken(authToken);
        userHomeController.setApiService(apiService);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}