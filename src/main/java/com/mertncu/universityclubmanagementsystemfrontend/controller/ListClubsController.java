package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.Club;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class ListClubsController {

    @FXML private TableView<Club> clubsTable;
    @FXML private TableColumn<Club, Long> clubIdColumn;
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
        clubsTable.setItems(clubList);
    }

    private void loadClubs() {
        try {
            List<Club> clubs = apiService.getAllClubs();
            clubList.clear();
            clubList.addAll(clubs);
        } catch (IOException e) {
            showAlert("Error", "An error occurred while loading clubs.");
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