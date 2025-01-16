package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.Club;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class ManageClubsController {

    @FXML private TextField clubIdField;
    @FXML private TextField clubNameField;
    @FXML private TextArea clubDescriptionArea;
    @FXML private Button createClubButton; // Use this button for both create and update
    @FXML private TableView<Club> clubsTable;
    @FXML private TableColumn<Club, String> clubIdColumn;
    @FXML private TableColumn<Club, String> clubNameColumn;
    @FXML private TableColumn<Club, String> clubDescriptionColumn;

    private ApiService apiService;
    private ObservableList<Club> clubList = FXCollections.observableArrayList();

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
        initializeTable();
        loadClubs();
    }

    private void initializeTable() {
        clubIdColumn.setCellValueFactory(new PropertyValueFactory<>("clubId"));
        clubNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        clubDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void loadClubs() {
        try {
            List<Club> clubs = apiService.getAllClubs();
            clubList.clear();
            clubList.addAll(clubs);
            clubsTable.setItems(clubList);
        } catch (IOException e) {
            showAlert("Error", "An error occurred while loading clubs.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateClub() {
        Club newClub = new Club();
        newClub.setName(clubNameField.getText());
        newClub.setDescription(clubDescriptionArea.getText());

        try {
            Club createdClub = apiService.createClub(newClub);
            if (createdClub != null) {
                showAlert("Success", "Club created successfully.");
                clearClubFields();
                loadClubs(); // Refresh the club list
            } else {
                showAlert("Error", "Failed to create club.");
            }
        } catch (IOException e) {
            showAlert("Error", "An error occurred while creating the club.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateClub() {
        Club selectedClub = clubsTable.getSelectionModel().getSelectedItem();
        if (selectedClub == null) {
            showAlert("No Selection", "Please select a club to update.");
            return;
        }

        // Populate the fields with the selected club's data
        clubIdField.setText(selectedClub.getClubId().toString());
        clubNameField.setText(selectedClub.getName());
        clubDescriptionArea.setText(selectedClub.getDescription());

        // Change the button action to perform update
        createClubButton.setText("Update Club");
        createClubButton.setOnAction(event -> {
            Club updatedClub = new Club();
            updatedClub.setClubId(Long.parseLong(clubIdField.getText()));
            updatedClub.setName(clubNameField.getText());
            updatedClub.setDescription(clubDescriptionArea.getText());

            try {
                Club result = apiService.updateClub(updatedClub.getClubId(), updatedClub);
                if (result != null) {
                    showAlert("Success", "Club updated successfully.");
                    clearClubFields();
                    loadClubs(); // Refresh the club list
                    resetCreateClubButton(); // Reset the button to 'Create Club'
                } else {
                    showAlert("Error", "Failed to update club.");
                }
            } catch (IOException e) {
                showAlert("Error", "An error occurred while updating the club.");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleDeleteClub() {
        Club selectedClub = clubsTable.getSelectionModel().getSelectedItem();
        if (selectedClub == null) {
            showAlert("No Selection", "Please select a club to delete.");
            return;
        }

        try {
            apiService.deleteClub(selectedClub.getClubId());
            showAlert("Success", "Club deleted successfully.");
            loadClubs(); // Refresh the club list
        } catch (IOException e) {
            showAlert("Error", "An error occurred while deleting the club.");
            e.printStackTrace();
        }
    }

    private void clearClubFields() {
        clubIdField.clear();
        clubNameField.clear();
        clubDescriptionArea.clear();
    }

    private void resetCreateClubButton() {
        createClubButton.setText("Create Club");
        createClubButton.setOnAction(event -> handleCreateClub());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}