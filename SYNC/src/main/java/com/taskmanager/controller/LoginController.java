package com.taskmanager.controller;

import com.taskmanager.model.User;
import com.taskmanager.utils.CSVHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink registerLink;

    public static String currentUsername;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        usernameField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> handleLogin());

        usernameField.textProperty().addListener((obs, old, newVal) -> {
            if (errorLabel.isVisible()) {
                errorLabel.setVisible(false);
            }
        });

        passwordField.textProperty().addListener((obs, old, newVal) -> {
            if (errorLabel.isVisible()) {
                errorLabel.setVisible(false);
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (username.length() < 3) {
            showError("Username must be at least 3 characters");
            return;
        }

        loginButton.setDisable(true);

        try {

            User user = CSVHelper.getUserByUsername(username);

            if (user == null) {
                showError("User not found. Please check your username.");
                loginButton.setDisable(false);
                return;
            }

            if (!user.verifyPassword(password)) {
                showError("Incorrect password. Please try again.");

                passwordField.requestFocus();
                loginButton.setDisable(false);
                return;
            }

            currentUsername = username;
            openDashboard();

        } catch (Exception e) {
            showError("An error occurred. Please try again.");
            e.printStackTrace();
            loginButton.setDisable(false);
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 650);
            scene.setFill(javafx.scene.paint.Color.web("#13141f"));
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open registration page");
        }
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1400, 800);
            scene.setFill(javafx.scene.paint.Color.web("#13141f"));
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open dashboard");
            loginButton.setDisable(false);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Please contact administrator to reset your password.\n\nEmail: admin@taskmanager.com");
        alert.showAndWait();
    }
}
