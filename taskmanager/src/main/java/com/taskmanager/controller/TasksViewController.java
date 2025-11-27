package com.taskmanager.controller;

import com.taskmanager.component.TaskCard;
import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TasksViewController {
    @FXML
    private BorderPane rootPane;
    @FXML
    private VBox draftColumn;
    @FXML
    private VBox inProgressColumn;
    @FXML
    private VBox editingColumn;
    @FXML
    private VBox doneColumn;

    @FXML
    public void initialize() {
        // Animasi Masuk
        rootPane.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        // Setup Drag and Drop untuk setiap kolom
        setupColumnDrag(draftColumn, "draft");
        setupColumnDrag(inProgressColumn, "in_progress");
        setupColumnDrag(editingColumn, "editing");
        setupColumnDrag(doneColumn, "done");

        refresh();
    }

    // --- LOGIKA DRAG & DROP ---
    private void setupColumnDrag(VBox column, String status) {
        // Saat kartu ditarik di atas kolom
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        // Saat kartu dilepas (drop) di kolom
        column.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasString()) {
                String taskId = event.getDragboard().getString();

                // Cari task, update status, simpan
                List<Task> allTasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);
                for (Task t : allTasks) {
                    if (t.getId().equals(taskId)) {
                        t.setStatus(status);
                        CSVHelper.updateTask(t); // Simpan ke CSV
                        success = true;
                        break;
                    }
                }
                refresh(); // Refresh UI
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void refresh() {
        draftColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        editingColumn.getChildren().clear();
        doneColumn.getChildren().clear();

        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        for (Task task : tasks) {
            // Passing 'this' (controller ini) ke TaskCard agar bisa panggil edit/delete
            TaskCard card = new TaskCard(task, this);

            switch (task.getStatus()) {
                case "draft" -> draftColumn.getChildren().add(card);
                case "in_progress" -> inProgressColumn.getChildren().add(card);
                case "editing" -> editingColumn.getChildren().add(card);
                case "done" -> doneColumn.getChildren().add(card);
                default -> draftColumn.getChildren().add(card);
            }
        }
    }

    // --- FITUR EDIT TASK ---
    public void handleEditTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskEditor.fxml"));
            Parent root = loader.load();

            TaskEditorController controller = loader.getController();
            controller.setTasksViewController(this);
            controller.setTask(task); // Load data task yang mau diedit

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);

            controller.setDialogStage(stage);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- FITUR DELETE TASK ---
    public void handleDeleteTask(Task task) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Task");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("Do you really want to delete '" + task.getTitle() + "'?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CSVHelper.deleteTask(task.getId());
            refresh();
        }
    }

    @FXML
    private void handleAddTask() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskEditor.fxml"));
            Parent root = loader.load();
            TaskEditorController controller = loader.getController();
            controller.setTasksViewController(this);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);

            controller.setDialogStage(stage);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToDashboard() {
        changeScene("/view/Dashboard.fxml");
    }

    @FXML
    private void handleGoToProfile() {
        changeScene("/view/ProfileView.fxml");
    }

    @FXML
    private void handleLogout() {
        changeScene("/view/Login.fxml");
    }

    private void changeScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}