package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.AuthReqResDTO;
import com.mertncu.universityclubmanagementsystemfrontend.model.User;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ListUsersController {

    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> userIdColumn;
    @FXML
    private TableColumn<User, String> userNameColumn;
    @FXML
    private TableColumn<User, String> userEmailColumn;
    @FXML
    private TableColumn<User, String> userRoleColumn;

    private ApiService apiService;
    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        initializeTable();
        loadUsers();
    }

    private void initializeTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void loadUsers() {
        try {
            AuthReqResDTO response = apiService.getAllUsers();
            if (response != null) {
                if (response.getStatusCode() == 200 && response.getUsers() != null) {
                    // Success: Load users into the table
                    userList.clear();
                    userList.addAll(response.getUsers());
                    usersTable.setItems(userList);
                } else {
                    // Error: Display the error message from the response
                    showAlert("Error", "Failed to load users: " + response.getMessage());
                }
            } else {
                showAlert("Error", "Failed to load users: Server returned null response.");
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while loading users.");
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}