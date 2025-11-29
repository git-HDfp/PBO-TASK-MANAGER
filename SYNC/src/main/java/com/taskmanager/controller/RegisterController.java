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

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;
    @FXML
    private Hyperlink loginLink;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);

        // Add enter key navigation
        usernameField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(e -> handleRegister());

        // Clear error on input
        usernameField.textProperty().addListener((obs, old, newVal) -> clearError());
        emailField.textProperty().addListener((obs, old, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearError());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> clearError());

        // Real-time validation feedback
        usernameField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && !usernameField.getText().isEmpty()) {
                validateUsername();
            }
        });

        emailField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && !emailField.getText().isEmpty()) {
                validateEmail();
            }
        });

        passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && !passwordField.getText().isEmpty()) {
                validatePassword();
            }
        });

        // Real-time password matching validation
        passwordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation - Empty fields
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        // Validation - Username length
        if (username.length() < 3) {
            showError("Username must be at least 3 characters long");
            usernameField.requestFocus();
            return;
        }

        if (username.length() > 50) {
            showError("Username must not exceed 50 characters");
            usernameField.requestFocus();
            return;
        }

        // Validation - Username format (alphanumeric and underscore only)
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores");
            usernameField.requestFocus();
            return;
        }

        // Validation - Email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return;
        }

        if (email.length() > 100) {
            showError("Email must not exceed 100 characters");
            emailField.requestFocus();
            return;
        }

        // Validation - Password length
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            passwordField.requestFocus();
            return;
        }

        if (password.length() > 100) {
            showError("Password must not exceed 100 characters");
            passwordField.requestFocus();
            return;
        }

        // Validation - Password match
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }

        // Disable button during processing
        registerButton.setDisable(true);

        try {
            // Check if username already exists
            if (CSVHelper.userExists(username)) {
                showError("Username already exists. Please choose another one.");
                usernameField.requestFocus();
                registerButton.setDisable(false);
                return;
            }

            // Create new user
            User newUser = new User(username, password, email);

            // Save to CSV
            CSVHelper.saveUser(newUser);

            // Show success message
            showSuccessAndRedirect(username);

        } catch (Exception e) {
            showError("Registration failed. Please try again.");
            e.printStackTrace();
            registerButton.setDisable(false);
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 650);
            scene.setFill(javafx.scene.paint.Color.web("#13141f"));
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validateUsername() {
        String username = usernameField.getText().trim();
        if (username.length() > 0 && username.length() < 3) {
            showError("Username must be at least 3 characters");
        } else if (!username.matches("^[a-zA-Z0-9_]*$")) {
            showError("Username can only contain letters, numbers, and underscores");
        } else if (CSVHelper.userExists(username)) {
            showError("Username already taken");
        }
    }

    private void validateEmail() {
        String email = emailField.getText().trim();
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Please enter a valid email address");
        }
    }

    private void validatePassword() {
        String password = passwordField.getText();
        if (!password.isEmpty() && password.length() < 6) {
            showError("Password must be at least 6 characters");
        }
    }

    private void validatePasswords() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.isEmpty() && !confirmPassword.isEmpty() && !password.equals(confirmPassword)) {
            showPersistentError("Passwords do not match");
        } else if (password.equals(confirmPassword) && errorLabel.getText().equals("Passwords do not match")) {
            clearError();
        }
    }

    private void showSuccessAndRedirect(String username) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText("Welcome to Task Management!");

        // Add a modern checkmark icon with theme colors
        Label checkmark = new Label("✓");
        checkmark.setStyle(
                "-fx-font-size: 48px; -fx-text-fill: #5e6ad2; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(94, 106, 210, 0.6), 8, 0, 0, 0);");
        alert.setGraphic(checkmark);

        alert.setContentText(
                "Account created successfully for: " + username + "\n\nYou will now be redirected to the login page.");

        // Customize the dialog with theme colors
        alert.getDialogPane().setStyle(
                "-fx-background-color: #1e2032; -fx-border-color: #5e6ad2; -fx-border-width: 2px; -fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 30, 0, 0, 15);");
        alert.getDialogPane().lookupButton(ButtonType.OK).setStyle(
                "-fx-background-color: linear-gradient(to right, #5e6ad2, #4e5ac0); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(94, 106, 210, 0.4), 15, 0, 0, 5);");

        // Style the header and content text
        alert.getDialogPane().lookup(".header-panel")
                .setStyle("-fx-background-color: #1e2032; -fx-text-fill: #ffffff; -fx-font-weight: bold;");
        alert.getDialogPane().lookup(".content").setStyle("-fx-text-fill: #d0d0e0;");

        alert.showAndWait();
        handleBackToLogin();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        // Auto-hide error after 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    private void showPersistentError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        // No auto-hide for persistent errors
    }

    private void clearError() {
        if (errorLabel.isVisible()) {
            errorLabel.setVisible(false);
        }
    }
}