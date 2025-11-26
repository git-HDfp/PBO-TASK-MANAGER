package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskEditorController {
    @FXML private TextField titleField;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;

    // Field Baru untuk Deadline
    @FXML private DatePicker deadlinePicker;
    @FXML private TextField timeField;

    @FXML private Slider progressSlider;
    @FXML private Label progressLabel;
    @FXML private Button saveButton;
    @FXML private Label titleLabel;
    @FXML private Label errorLabel; // Pastikan ada di FXML atau hapus jika tidak

    // GANTI: Bukan DashboardController, tapi TasksViewController
    private TasksViewController tasksViewController;

    private Stage dialogStage;
    private Task editingTask;
    private boolean isEditMode = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    // GANTI Setter ini
    public void setTasksViewController(TasksViewController controller) {
        this.tasksViewController = controller;
    }

    @FXML
    public void initialize() {
        categoryComboBox.getItems().addAll("Main", "Secondary", "Tertiary");
        categoryComboBox.setValue("Main");

        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.setValue("Medium");

        statusComboBox.getItems().addAll("draft", "in_progress", "editing", "done");
        statusComboBox.setValue("draft");

        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            progressLabel.setText(newVal.intValue() + "%");
        });
    }

    public void setTask(Task task) {
        this.editingTask = task;
        this.isEditMode = true;
        if (titleLabel != null) titleLabel.setText("Edit Task");
        saveButton.setText("Update");

        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        categoryComboBox.setValue(task.getCategory());
        priorityComboBox.setValue(task.getPriority());
        statusComboBox.setValue(task.getStatus());
        progressSlider.setValue(task.getProgress());

        // Set Deadline di UI jika ada
        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            try {
                // Format deadline di CSV: YYYY-MM-DDTHH:MM
                String[] parts = task.getDeadline().split("T");
                deadlinePicker.setValue(LocalDate.parse(parts[0]));
                if (parts.length > 1) timeField.setText(parts[1]);
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty()) return; // Simple validation

        // Ambil Deadline dari Input
        String deadlineString = "";
        if (deadlinePicker.getValue() != null) {
            String date = deadlinePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String time = timeField.getText().trim();
            if (time.isEmpty()) time = "23:59";
            // Gabungkan jadi format ISO Local Date Time
            deadlineString = date + "T" + time;
        }

        if (isEditMode) {
            editingTask.setTitle(title);
            editingTask.setDescription(descriptionArea.getText());
            editingTask.setCategory(categoryComboBox.getValue());
            editingTask.setPriority(priorityComboBox.getValue());
            editingTask.setStatus(statusComboBox.getValue());
            editingTask.setProgress((int) progressSlider.getValue());
            editingTask.setDeadline(deadlineString); // Set deadline baru

            CSVHelper.updateTask(editingTask);
        } else {
            // FIX: Constructor Task sekarang punya 6 parameter (+deadline)
            Task newTask = new Task(
                    title,
                    descriptionArea.getText(),
                    categoryComboBox.getValue(),
                    priorityComboBox.getValue(),
                    LoginController.currentUsername,
                    deadlineString // Parameter ke-6
            );
            newTask.setStatus(statusComboBox.getValue());
            newTask.setProgress((int) progressSlider.getValue());

            CSVHelper.saveTask(newTask);
        }

        // FIX: Panggil refresh punya TasksViewController
        if (tasksViewController != null) {
            tasksViewController.refresh();
        }

        if (dialogStage != null) dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        if (dialogStage != null) dialogStage.close();
    }
}