package com.taskmanager.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CustomAlertDialog {

    public enum AlertType {
        SUCCESS, ERROR, CONFIRMATION, INFO
    }

    private Stage dialogStage;
    private boolean result = false;

    public CustomAlertDialog() {
        // Constructor
    }

    public boolean show(AlertType type, String title, String message) {
        return show(type, title, message, null);
    }

    public boolean show(AlertType type, String title, String message, String confirmText) {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        // Main container
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("custom-alert-container");

        // Icon and title
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label iconLabel = new Label();
        iconLabel.getStyleClass().add("alert-icon");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");

        headerBox.getChildren().addAll(iconLabel, titleLabel);

        // Message
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("alert-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        if (type == AlertType.CONFIRMATION) {
            Button cancelButton = new Button("Cancel");
            cancelButton.getStyleClass().add("btn-glass-gray");
            cancelButton.setOnAction(e -> {
                result = false;
                dialogStage.close();
            });

            Button confirmButton = new Button(confirmText != null ? confirmText : "Confirm");
            confirmButton.getStyleClass().add("btn-glass-secondary");
            confirmButton.setOnAction(e -> {
                result = true;
                dialogStage.close();
            });

            buttonBox.getChildren().addAll(cancelButton, confirmButton);
        } else {
            Button okButton = new Button("OK");
            okButton.getStyleClass().add("btn-glass-primary");
            okButton.setOnAction(e -> dialogStage.close());
            buttonBox.getChildren().add(okButton);
        }

        // Set icon based on type
        switch (type) {
            case SUCCESS:
                iconLabel.setText("✅");
                container.setStyle("-fx-background-color: linear-gradient(to bottom, #1e2032, #25273d);");
                break;
            case ERROR:
                iconLabel.setText("❌");
                container.setStyle("-fx-background-color: linear-gradient(to bottom, #1e2032, #25273d);");
                break;
            case CONFIRMATION:
                iconLabel.setText("⚠️");
                container.setStyle("-fx-background-color: linear-gradient(to bottom, #1e2032, #25273d);");
                break;
            case INFO:
                iconLabel.setText("ℹ️");
                container.setStyle("-fx-background-color: linear-gradient(to bottom, #1e2032, #25273d);");
                break;
        }

        container.getChildren().addAll(headerBox, messageLabel, buttonBox);

        Scene scene = new Scene(container);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
        dialogStage.setScene(scene);

        // Make it draggable
        makeDraggable(container);

        dialogStage.showAndWait();

        return result;
    }

    private void makeDraggable(VBox container) {
        final double[] xOffset = { 0 };
        final double[] yOffset = { 0 };

        container.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        container.setOnMouseDragged(event -> {
            dialogStage.setX(event.getScreenX() - xOffset[0]);
            dialogStage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    // Static methods for easy use
    public static void showSuccess(String title, String message) {
        new CustomAlertDialog().show(AlertType.SUCCESS, title, message);
    }

    public static void showError(String title, String message) {
        new CustomAlertDialog().show(AlertType.ERROR, title, message);
    }

    public static void showInfo(String title, String message) {
        new CustomAlertDialog().show(AlertType.INFO, title, message);
    }

    public static boolean showConfirmation(String title, String message) {
        return new CustomAlertDialog().show(AlertType.CONFIRMATION, title, message);
    }

    public static boolean showConfirmation(String title, String message, String confirmText) {
        return new CustomAlertDialog().show(AlertType.CONFIRMATION, title, message, confirmText);
    }
}
