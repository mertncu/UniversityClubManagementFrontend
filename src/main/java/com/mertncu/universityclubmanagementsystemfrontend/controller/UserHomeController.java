package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.MainApp;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class UserHomeController {

    @FXML
    private StackPane contentPane;

    private ApiService apiService;
    private AuthToken authToken;
    private String userId;
    private CompletableFuture<Void> userProfileFuture;

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        fetchUserProfile();
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    private void fetchUserProfile() {
        userProfileFuture = apiService.getMyProfile()
                .thenAccept(profileResponse -> {
                    if (profileResponse.getStatusCode() == 200 && profileResponse.getUser() != null) {
                        this.userId = profileResponse.getUser().getId();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch user profile.");
                    }
                })
                .exceptionally(ex -> {
                    showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching user profile.");
                    ex.printStackTrace();
                    return null;
                });
    }

    private void loadInitialView() {
        showTasks();
    }

    @FXML
    private void showTasks() {
        loadViewWhenUserReady("/UserTasksView.fxml", "Tasks");
    }

    @FXML
    private void showMeetings() {
        loadViewWhenUserReady("/UserMeetingsView.fxml", "Meetings");
    }

    @FXML
    private void showFiles() {
        loadViewWhenUserReady("/UserFilesView.fxml", "Files");
    }

    @FXML
    private void showEvents() {
        loadViewWhenUserReady("/UserEventsView.fxml", "Events");
    }

    private void loadViewWhenUserReady(String fxml, String viewName) {
        userProfileFuture.thenRun(() -> {
            System.out.println("loadViewWhenUserReady: thenRun executing!");
            javafx.application.Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(MainApp.class.getResource(fxml));
                    Parent view = loader.load();

                    Object controller = loader.getController();
                    if (controller instanceof UserContentController) {
                        ((UserContentController) controller).setApiService(apiService);
                        ((UserContentController) controller).setAuthToken(authToken);
                        ((UserContentController) controller).setUserId(userId);
                        ((UserContentController) controller).setViewName(viewName);
                        ((UserContentController) controller).initializeData();
                    }

                    contentPane.getChildren().setAll(view);
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load " + viewName + " view.");
                    e.printStackTrace();
                }
            });
        });
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}