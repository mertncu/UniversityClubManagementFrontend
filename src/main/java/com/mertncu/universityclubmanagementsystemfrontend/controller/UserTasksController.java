package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.AuthReqResDTO;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.model.TaskDTO;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class UserTasksController implements UserContentController {

    @FXML
    private TableView<TaskDTO> tasksTable;

    @FXML
    private TableColumn<TaskDTO, String> taskNameColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskDescriptionColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskDueDateColumn;

    @FXML
    private TableColumn<TaskDTO, String> taskStatusColumn;

    private ApiService apiService;
    private AuthToken authToken;
    private String userId;

    @Override
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public void initializeData() {
        // Set up cell value factories for the table columns
        taskNameColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        taskDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("taskDescription"));
        taskDueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        taskStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Fetch and display tasks for the user
        fetchUserTasks();
    }

    @Override
    public void setViewName(String viewName) {
        // Currently not used in this controller
    }

    private void fetchUserTasks() {
        if (userId != null) {
            apiService.getTasksByUser(userId)
                    .thenAccept(this::handleTaskResponse)
                    .exceptionally(ex -> {
                        showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while fetching tasks.");
                        ex.printStackTrace();
                        return null;
                    });
        } else {
            showAlert(Alert.AlertType.WARNING, "Warning", "User ID is not set.");
        }
    }

    private void handleTaskResponse(AuthReqResDTO response) {
        if (response.getStatusCode() == 200 && response.getTaskDTOs() != null) {
            ObservableList<TaskDTO> tasks = FXCollections.observableArrayList(response.getTaskDTOs());
            tasksTable.setItems(tasks);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to fetch tasks: " + response.getError());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}