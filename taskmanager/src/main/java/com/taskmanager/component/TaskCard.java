package com.taskmanager.component;

import com.taskmanager.controller.TasksViewController;
import com.taskmanager.model.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TaskCard extends VBox {
    private Task task;
    private TasksViewController controller;

    public TaskCard(Task task, TasksViewController controller) {
        this.task = task;
        this.controller = controller;
        setupCard();
        setupDragEvents(); // Aktifkan Drag
    }

    private void setupCard() {
        this.setPadding(new Insets(15));
        this.setSpacing(10);
        this.getStyleClass().add("task-card");
        this.setPrefWidth(250); // Ukuran kartu

        // 1. Header: Title & Priority
        HBox header = new HBox(10);
        Label titleLabel = new Label(task.getTitle());
        titleLabel.getStyleClass().add("task-title");
        titleLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label priorityLabel = new Label(task.getPriority().toUpperCase());
        priorityLabel.getStyleClass().addAll("priority-badge", "priority-" + task.getPriority());

        header.getChildren().addAll(titleLabel, spacer, priorityLabel);

        // 2. Description (Short)
        String descText = task.getDescription();
        if (descText.length() > 50) descText = descText.substring(0, 50) + "...";
        Label descLabel = new Label(descText);
        descLabel.getStyleClass().add("subtitle");
        descLabel.setWrapText(true);

        // 3. Deadline
        Label deadlineLabel = new Label("🕒 " + task.getTimeRemaining());
        deadlineLabel.getStyleClass().add("deadline-badge");
        if (task.getTimeRemaining().contains("OVERDUE")) {
            deadlineLabel.getStyleClass().add("deadline-warning");
        }

        // 4. Action Buttons (Edit & Delete)
        HBox actions = new HBox(8);
        Button editBtn = new Button("Edit");
        editBtn.getStyleClass().add("btn-icon");
        editBtn.setOnAction(e -> controller.handleEditTask(task));

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-icon");
        deleteBtn.setOnAction(e -> controller.handleDeleteTask(task));

        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        actions.getChildren().addAll(deadlineLabel, actionSpacer, editBtn, deleteBtn);

        // 5. Progress
        ProgressBar progressBar = new ProgressBar(task.getProgress() / 100.0);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #4318FF; -fx-pref-height: 5px;");

        this.getChildren().addAll(header, descLabel, progressBar, actions);
    }

    // --- LOGIKA DRAG SOURCE ---
    private void setupDragEvents() {
        this.setOnDragDetected(event -> {
            // Mulai Drag
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);

            // Simpan ID Task ke clipboard agar Column tau task mana yg dipindah
            ClipboardContent content = new ClipboardContent();
            content.putString(task.getId());
            db.setContent(content);

            event.consume();
        });
    }
}