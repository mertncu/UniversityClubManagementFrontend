package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.AuthReqResDTO;
import com.mertncu.universityclubmanagementsystemfrontend.model.Club;
import com.mertncu.universityclubmanagementsystemfrontend.model.User;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class ManageUsersController {

    @FXML private TextField userIdField;
    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;
    @FXML private TextField userRoleField;
    @FXML private ComboBox<String> clubComboBox;
    @FXML private Button createUserButton;
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;

    private ApiService apiService;
    private ObservableList<String> clubNames = FXCollections.observableArrayList();
    private ObservableList<User> userList = FXCollections.observableArrayList();

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        initializeTable();
        loadUsers();
        try {
            loadClubNames();
        } catch (IOException e) {
            showAlert("Error", "Failed to load clubs.");
            e.printStackTrace();
        }
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
            if (response != null) { // Check for null response
                if (response.getStatusCode() == 200 && response.getUsers() != null) {
                    userList.clear();
                    userList.addAll(response.getUsers());
                    usersTable.setItems(userList);
                } else {
                    showAlert("Error", "Failed to load users: " + response.getError());
                }
            } else {
                showAlert("Error", "Failed to load users: Server returned null response.");
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while loading users.");
            e.printStackTrace();
        }
    }

    private void loadClubNames() throws IOException {
        List<Club> clubs = apiService.getAllClubs();
        clubs.forEach(club -> clubNames.add(club.getName()));
        clubComboBox.setItems(clubNames);
    }

    @FXML
    private void handleCreateUser() {
        User newUser = new User();
        newUser.setId(userIdField.getText());
        newUser.setName(userNameField.getText());
        newUser.setEmail(userEmailField.getText());
        newUser.setRole(userRoleField.getText());
        try {
            AuthReqResDTO response = apiService.createUser(newUser);
            if (response.getStatusCode() == 200) {
                showAlert("Success", "User created successfully.");
                // Assign user to club if a club is selected
                String selectedClubName = clubComboBox.getValue();
                if (selectedClubName != null && !selectedClubName.isEmpty()) {
                    assignUserToClub(newUser.getId(), selectedClubName);
                }
                clearUserFields();
                loadUsers(); // Refresh the user list
            } else {
                showAlert("Error", "Failed to create user: " + response.getError());
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while creating the user.");
            e.printStackTrace();
        }
    }

    private void assignUserToClub(String userId, String clubName) throws IOException {
        Integer clubId = apiService.getClubIdByName(clubName);
        if (clubId != null) {
            apiService.assignUserToClub(userId, clubId);
        } else {
            showAlert("Error", "Could not find club ID.");
        }
    }
    @FXML
    private void handleUpdateUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No Selection", "Please select a user to update.");
            return;
        }

        // Use the selected user's ID for updating
        String userId = selectedUser.getId();

        // Populate fields with selected user's data
        userIdField.setText(selectedUser.getId());
        userNameField.setText(selectedUser.getName());
        userEmailField.setText(selectedUser.getEmail());
        userRoleField.setText(selectedUser.getRole());
        // Note: Not updating club assignment here

        // Change the button action to perform update
        createUserButton.setText("Update User");
        createUserButton.setOnAction(event -> {
            User updatedUser = new User();
            updatedUser.setId(userId); // Use the original user ID
            updatedUser.setName(userNameField.getText());
            updatedUser.setEmail(userEmailField.getText());
            updatedUser.setRole(userRoleField.getText());

            try {
                AuthReqResDTO response = apiService.updateUser(userId, updatedUser);
                if (response.getStatusCode() == 200) {
                    showAlert("Success", "User updated successfully.");
                    clearUserFields();
                    loadUsers(); // Refresh the user list
                    resetCreateUserButton(); // Reset the button to 'Create User'
                } else {
                    showAlert("Error", "Failed to update user: " + response.getError());
                }
            } catch (IOException e) {
                showAlert("Error", "An error occurred while updating the user.");
                e.printStackTrace();
            }
        });
    }

    // Add a method to reset the button after updating
    private void resetCreateUserButton() {
        createUserButton.setText("Create User");
        createUserButton.setOnAction(event -> handleCreateUser());
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("No Selection", "Please select a user to delete.");
            return;
        }

        try {
            AuthReqResDTO response = apiService.deleteUser(selectedUser.getId());
            if (response.getStatusCode() == 200) {
                showAlert("Success", "User deleted successfully.");
                loadUsers(); // Refresh the user list
            } else {
                showAlert("Error", "Failed to delete user: " + response.getError());
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while deleting the user.");
            e.printStackTrace();
        }
    }

    private void clearUserFields() {
        userIdField.clear();
        userNameField.clear();
        userEmailField.clear();
        userRoleField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}