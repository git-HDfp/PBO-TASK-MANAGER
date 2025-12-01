package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.utils.CSVHelper;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class ProfileController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox sidebar;
    @FXML
    private Label sidebarTitle;
    @FXML
    private Label sidebarSubtitle;
    @FXML
    private Label menuTitle;
    @FXML
    private Label accountTitle;
    @FXML
    private Button toggleBtn;
    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnTasks;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;

    private boolean isSidebarCollapsed = false;
    private static final double SIDEBAR_EXPANDED_WIDTH = 260;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 70;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField emailField;
    @FXML
    private Label emailStatusLabel;
    @FXML
    private Label createdAtLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Label successLabel;

    @FXML
    private PasswordField currentPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label passwordErrorLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Label passwordSuccessLabel;

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label completedTasksLabel;
    @FXML
    private Label inProgressTasksLabel;

    private User currentUser;

    @FXML
    public void initialize() {

        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        loadUserData();
        loadUserStatistics();

        if (sidebar != null) {
            sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
        }

        emailField.textProperty().addListener((obs, old, newVal) -> {
            emailStatusLabel.setVisible(false);
            successLabel.setVisible(false);
        });

        currentPasswordField.textProperty().addListener((obs, old, newVal) -> {
            passwordErrorLabel.setVisible(false);

        });

        currentPasswordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused && !currentPasswordField.getText().isEmpty()) {
                validateCurrentPassword();
            }
        });

        newPasswordField.textProperty().addListener((obs, old, newVal) -> {
            passwordErrorLabel.setVisible(false);

        });

        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> {
            passwordErrorLabel.setVisible(false);

        });

        newPasswordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
        confirmPasswordField.textProperty().addListener((obs, old, newVal) -> validatePasswords());
    }

    @FXML
    private void handleSidebarToggle() {
        isSidebarCollapsed = !isSidebarCollapsed;

        Timeline timeline = new Timeline();
        KeyValue widthValue = new KeyValue(sidebar.prefWidthProperty(),
                isSidebarCollapsed ? SIDEBAR_COLLAPSED_WIDTH : SIDEBAR_EXPANDED_WIDTH,
                Interpolator.EASE_BOTH);

        KeyFrame frame = new KeyFrame(Duration.millis(300), widthValue);
        timeline.getKeyFrames().add(frame);
        timeline.play();

        if (sidebarTitle != null)
            sidebarTitle.setVisible(!isSidebarCollapsed);
        if (sidebarSubtitle != null)
            sidebarSubtitle.setVisible(!isSidebarCollapsed);
        if (menuTitle != null)
            menuTitle.setVisible(true);
        if (accountTitle != null)
            accountTitle.setVisible(true);

        ContentDisplay cd = isSidebarCollapsed ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT;
        if (btnDashboard != null)
            btnDashboard.setContentDisplay(cd);
        if (btnTasks != null)
            btnTasks.setContentDisplay(cd);
        if (btnProfile != null)
            btnProfile.setContentDisplay(cd);
        if (btnLogout != null)
            btnLogout.setContentDisplay(cd);
    }

    private void loadUserData() {
        String username = LoginController.currentUsername;
        if (username == null) {
            showError("User session not found", emailStatusLabel);
            return;
        }

        currentUser = CSVHelper.getUserByUsername(username);
        if (currentUser == null) {
            showError("Failed to load user data", emailStatusLabel);
            return;
        }

        welcomeLabel.setText("Hi, " + username);
        usernameLabel.setText(username);
        emailField.setText(currentUser.getEmail());

        try {
            LocalDateTime dateTime = LocalDateTime.parse(currentUser.getCreatedAt(),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));
            createdAtLabel.setText(formattedDate);
        } catch (Exception e) {
            createdAtLabel.setText(currentUser.getCreatedAt());
        }
    }

    private void loadUserStatistics() {
        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        int total = tasks.size();
        int completed = 0;
        int inProgress = 0;

        for (Task task : tasks) {
            String status = task.getStatus() != null ? task.getStatus() : "draft";
            if ("done".equals(status)) {
                completed++;
            } else if ("in_progress".equals(status)) {
                inProgress++;
            }
        }

        totalTasksLabel.setText(String.valueOf(total));
        completedTasksLabel.setText(String.valueOf(completed));
        inProgressTasksLabel.setText(String.valueOf(inProgress));
    }

    @FXML
    private void handleSaveProfile() {
        String newEmail = emailField.getText().trim();

        if (newEmail.isEmpty()) {
            showError("Email cannot be empty", emailStatusLabel);
            return;
        }

        if (!isValidEmail(newEmail)) {
            showError("Please enter a valid email address", emailStatusLabel);
            return;
        }

        currentUser.setEmail(newEmail);

        if (CSVHelper.updateUser(currentUser)) {
            showSuccess("Profile updated successfully!", successLabel);
            saveButton.setDisable(false);
        } else {
            showError("Failed to update profile", emailStatusLabel);
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("All password fields are required", passwordErrorLabel);
            return;
        }

        if (!currentUser.verifyPassword(currentPassword)) {
            showError("Current password is incorrect", passwordErrorLabel);
            currentPasswordField.clear();
            currentPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("New password must be at least 6 characters", passwordErrorLabel);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match", passwordErrorLabel);
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }

        if (currentUser.verifyPassword(newPassword)) {
            showError("New password must be different from current password", passwordErrorLabel);
            return;
        }

        currentUser.setPasswordHash(User.hashPassword(newPassword));

        if (CSVHelper.updateUser(currentUser)) {

            showModernSuccessDialog("Password Changed",
                    "Your password has been successfully updated! Keep it safe. 🔒");

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showError("Failed to change password", passwordErrorLabel);
        }
    }

    @FXML
    private void handleGoToDashboard() {
        animateAndChangeScene("/view/Dashboard.fxml");
    }

    @FXML
    private void handleGoToTasks() {
        animateAndChangeScene("/view/TasksView.fxml");
    }

    @FXML
    private void handleLogout() {
        LoginController.currentUsername = null;
        animateAndChangeScene("/view/Login.fxml");
    }

    private void animateAndChangeScene(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent nextRoot = loader.load();

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.getScene().setRoot(nextRoot);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Failed to load page: " + fxmlPath);
            }
        });

        fadeOut.play();
    }

    private void showError(String message, Label errorLabel) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> errorLabel.setVisible(false));
        pause.play();
    }

    private void showSuccess(String message, Label successLabel) {
        successLabel.setText("✅ " + message);
        successLabel.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> successLabel.setVisible(false));
        pause.play();
    }

    private void validateCurrentPassword() {
        String currentPassword = currentPasswordField.getText();
        if (!currentPassword.isEmpty() && !currentUser.verifyPassword(currentPassword)) {
            showPersistentError("Current password is incorrect", passwordErrorLabel);
        } else if (currentUser.verifyPassword(currentPassword)
                && passwordErrorLabel.getText().equals("❌ Current password is incorrect")) {
            clearError(passwordErrorLabel);
        }
    }

    private void validatePasswords() {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.isEmpty() && !confirmPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            showPersistentError("New passwords do not match", passwordErrorLabel);
        } else if (newPassword.equals(confirmPassword)
                && passwordErrorLabel.getText().equals("❌ New passwords do not match")) {
            clearError(passwordErrorLabel);
        }
    }

    private void showPersistentError(String message, Label errorLabel) {
        errorLabel.setText("❌ " + message);
        errorLabel.setVisible(true);

    }

    private void clearError(Label errorLabel) {
        if (errorLabel.isVisible()) {
            errorLabel.setVisible(false);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void showModernSuccessDialog(String title, String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle(
                "-fx-background-color: #1a1a2e; -fx-background-radius: 20; -fx-border-color: #2ecc71; -fx-border-width: 2; -fx-border-radius: 20; -fx-padding: 30;");

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(20);
        root.setEffect(shadow);

        Label iconLabel = new Label("✨");
        iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label messageLabel = new Label(message);
        messageLabel.setStyle(
                "-fx-font-size: 14px; -fx-text-fill: #a0a0a0; -fx-wrap-text: true; -fx-text-alignment: center;");

        Button closeBtn = new Button("Awesome!");
        closeBtn.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 30; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> dialog.close());

        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle(
                "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 30; -fx-cursor: hand;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle(
                "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 30; -fx-background-radius: 30; -fx-cursor: hand;"));

        root.getChildren().addAll(iconLabel, titleLabel, messageLabel, closeBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        dialog.setScene(scene);

        dialog.centerOnScreen();

        dialog.showAndWait();
    }
}

