package com.mertncu.universityclubmanagementsystemfrontend;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private static final String BASE_URL = "http://localhost:8080/auth";

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        try {
            AuthReqResDTO response = authenticate(email, password);

            if (response != null && response.getRole() != null) {
                if ("ROLE_ADMIN".equals(response.getRole())) {
                    navigateTo("/admin.fxml", response);
                } else if ("ROLE_USER".equals(response.getRole())) {
                    navigateTo("/user.fxml", response);
                } else {
                    showError("Bilinmeyen rol: " + response.getRole());
                }
            } else {
                showError("Login failed. Please check your details.");
            }
        } catch (Exception e) {
            showError("Hata: " + e.getMessage());
        }
    }

    private AuthReqResDTO authenticate(String email, String password) throws Exception {
        URL url = new URL(BASE_URL + "/login");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        ObjectMapper mapper = new ObjectMapper();
        AuthReqResDTO request = new AuthReqResDTO();
        request.setEmail(email);
        request.setPassword(password);

        String requestBody = mapper.writeValueAsString(request);
        connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String response = new String(connection.getInputStream().readAllBytes());
            System.out.println("Response: " + response);  // Yanıtı yazdırın
            return mapper.readValue(response, AuthReqResDTO.class);
        } else {
            throw new Exception("Login Error: " + connection.getResponseMessage());
        }
    }

    private void navigateTo(String fxmlPath, AuthReqResDTO userData) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            // Kullanıcı bilgilerini doğru sayfaya iletme
            if (fxmlPath.equals("/admin.fxml")) {
                AdminController controller = new AdminController();
                loader.setController(controller);
                controller.setUserData(userData); // Kullanıcı bilgilerini AdminController'a gönderiyoruz
            } else if (fxmlPath.equals("/user.fxml")) {
                UserController controller = new UserController();
                loader.setController(controller);
                controller.setUserData(userData); // Kullanıcı bilgilerini UserController'a gönderiyoruz
            }

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
        } catch (Exception e) {
            showError("Navigasyon hatası: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
