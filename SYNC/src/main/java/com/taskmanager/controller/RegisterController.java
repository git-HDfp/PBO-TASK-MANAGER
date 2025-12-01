package com.taskmanager.controller;

import com.taskmanager.model.User;
import com.taskmanager.utils.CSVHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
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

        usernameField.setOnAction(e -> emailField.requestFocus());
        emailField.setOnAction(e -> passwordField.requestFocus());
        passwordField.setOnAction(e -> confirmPasswordField.requestFocus());
        confirmPasswordField.setOnAction(e -> handleRegister());

        usernameField.textProperty().addListener((obs, old, newVal) -> clearError());
        emailField.textProperty().addListener((obs, old, newVal) -> clearError());
        passwordField.textProperty().addListener((obs, old, newVal) -> clearError());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> clearError());

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

        passwordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

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

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores");
            usernameField.requestFocus();
            return;
        }

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

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }

        registerButton.setDisable(true);

        try {

            if (CSVHelper.userExists(username)) {
                showError("Username already exists. Please choose another one.");
                usernameField.requestFocus();
                registerButton.setDisable(false);
                return;
            }

            User newUser = new User(username, password, email);

            CSVHelper.saveUser(newUser);

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

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.setResizable(false);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setPrefWidth(400);

        content.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1e293b, #0f172a); " +
                        "-fx-background-radius: 24; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.1); " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 24; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.4), 30, 0, 0, 15);");

        Label checkIcon = new Label("✓");
        checkIcon.setStyle(
                "-fx-font-size: 48px; " +
                        "-fx-text-fill: #4ade80; " + // Bright green
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Segoe UI Emoji', 'Arial';");

        VBox iconContainer = new VBox(checkIcon);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setMaxSize(90, 90);
        iconContainer.setMinSize(90, 90);
        iconContainer.setStyle(
                "-fx-background-color: rgba(74, 222, 128, 0.1); " +
                        "-fx-background-radius: 45; " +
                        "-fx-border-color: rgba(74, 222, 128, 0.2); " +
                        "-fx-border-radius: 45; " +
                        "-fx-border-width: 1px;");

        Label titleLabel = new Label("Welcome Aboard!");
        titleLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 26px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Segoe UI', 'Arial';");

        Label subtitleLabel = new Label("Your account has been created successfully");
        subtitleLabel.setStyle(
                "-fx-text-fill: #94a3b8; " + // Slate-400
                        "-fx-font-size: 14px; " +
                        "-fx-font-family: 'Segoe UI', 'Arial';");

        VBox userCard = new VBox(12);
        userCard.setAlignment(Pos.CENTER);
        userCard.setPadding(new Insets(20, 40, 20, 40));
        userCard.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.03); " +
                        "-fx-background-radius: 16; " +
                        "-fx-border-color: rgba(255, 255, 255, 0.05); " +
                        "-fx-border-radius: 16;");

        String initial = (username != null && !username.isEmpty()) ? username.substring(0, 1).toUpperCase() : "?";
        Label avatarLabel = new Label(initial);
        avatarLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 24px; " +
                        "-fx-font-weight: bold;");

        VBox avatarCircle = new VBox(avatarLabel);
        avatarCircle.setAlignment(Pos.CENTER);
        avatarCircle.setMaxSize(50, 50);
        avatarCircle.setMinSize(50, 50);
        avatarCircle.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #6366f1, #8b5cf6); " +
                        "-fx-background-radius: 25; " +
                        "-fx-effect: dropshadow(gaussian, rgba(99, 102, 241, 0.4), 10, 0, 0, 5);");

        Label userLabel = new Label(username);
        userLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-font-size: 18px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-family: 'Segoe UI', 'Arial';");

        userCard.getChildren().addAll(avatarCircle, userLabel);

        Label loadingLabel = new Label("Redirecting to login...");
        loadingLabel.setStyle(
                "-fx-text-fill: #64748b; " + // Slate-500
                        "-fx-font-size: 13px; " +
                        "-fx-font-style: italic;");

        content.getChildren().addAll(iconContainer, titleLabel, subtitleLabel, userCard, loadingLabel);

        Scene scene = new Scene(content);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), content);
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        ScaleTransition iconBounce = new ScaleTransition(Duration.millis(600), iconContainer);
        iconBounce.setFromX(0.0);
        iconBounce.setFromY(0.0);
        iconBounce.setToX(1.0);
        iconBounce.setToY(1.0);
        iconBounce.setDelay(Duration.millis(100));

        scaleIn.play();
        fadeIn.play();
        iconBounce.play();

        final int[] countdown = { 3 };
        loadingLabel.setText("Redirecting to login in " + countdown[0] + "s...");

        PauseTransition countdownTimer = new PauseTransition(Duration.seconds(1));
        countdownTimer.setOnFinished(e -> {
            countdown[0]--;
            if (countdown[0] > 0) {
                loadingLabel.setText("Redirecting to login in " + countdown[0] + "s...");
                countdownTimer.play();
            } else {
                dialogStage.close();
                handleBackToLogin();
            }
        });
        countdownTimer.play();

        dialogStage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    private void showPersistentError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);

    }

    private void clearError() {
        if (errorLabel.isVisible()) {
            errorLabel.setVisible(false);
        }
    }
}
