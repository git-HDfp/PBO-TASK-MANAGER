package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import com.taskmanager.utils.SubjectHelper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TaskEditorController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ComboBox<String> subjectComboBox;
    @FXML
    private ComboBox<String> priorityComboBox;
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private DatePicker deadlinePicker;
    @FXML
    private TextField timeField;

    @FXML
    private Slider progressSlider;
    @FXML
    private Label progressLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Label titleLabel;
    @FXML
    private Label errorLabel;

    private TasksViewController tasksViewController;
    private Stage dialogStage;
    private Task editingTask;
    private boolean isEditMode = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTasksViewController(TasksViewController controller) {
        this.tasksViewController = controller;
    }

    @FXML
    public void initialize() {

        subjectComboBox.getItems().setAll(SubjectHelper.getAllSubjects());
        if (!subjectComboBox.getItems().isEmpty()) {
            subjectComboBox.setValue(subjectComboBox.getItems().get(0));
        }

        priorityComboBox.getItems().addAll("High", "Medium", "Low");
        priorityComboBox.setValue("Medium");

        statusComboBox.getItems().addAll("draft", "in_progress", "done");
        statusComboBox.setValue("draft");

        progressSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int progress = newVal.intValue();
            progressLabel.setText(progress + "%");
            updateStatusBasedOnProgress(progress);
        });
    }

    private void updateStatusBasedOnProgress(int progress) {
        if (progress == 0) {

            statusComboBox.setValue("draft");
        } else if (progress > 0 && progress < 100) {
            statusComboBox.setValue("in_progress");
        } else if (progress == 100) {
            statusComboBox.setValue("done");
        }
    }

    public void setTask(Task task) {
        this.editingTask = task;
        this.isEditMode = true;
        if (titleLabel != null)
            titleLabel.setText("Edit Task");
        saveButton.setText("Update");

        titleField.setText(task.getTitle());
        descriptionArea.setText(task.getDescription());
        subjectComboBox.setValue(task.getCategory());
        priorityComboBox.setValue(task.getPriority());
        statusComboBox.setValue(task.getStatus());
        progressSlider.setValue(task.getProgress());

        if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
            try {
                String[] parts = task.getDeadline().split("T");
                deadlinePicker.setValue(LocalDate.parse(parts[0]));
                if (parts.length > 1)
                    timeField.setText(parts[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText();
        if (title == null || title.trim().isEmpty())
            return;

        String deadlineString = "";
        if (deadlinePicker.getValue() != null) {
            String date = deadlinePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String time = timeField.getText().trim();
            if (time.isEmpty())
                time = "23:59";
            deadlineString = date + "T" + time;
        }

        if (isEditMode) {
            editingTask.setTitle(title);
            editingTask.setDescription(descriptionArea.getText());
            editingTask.setCategory(subjectComboBox.getValue());
            editingTask.setPriority(priorityComboBox.getValue());
            editingTask.setStatus(statusComboBox.getValue());
            editingTask.setProgress((int) progressSlider.getValue());
            editingTask.setDeadline(deadlineString);

            CSVHelper.updateTask(editingTask);
        } else {
            Task newTask = new Task(
                    title,
                    descriptionArea.getText(),
                    subjectComboBox.getValue(),
                    priorityComboBox.getValue(),
                    LoginController.currentUsername,
                    deadlineString);
            newTask.setStatus(statusComboBox.getValue());
            newTask.setProgress((int) progressSlider.getValue());

            CSVHelper.saveTask(newTask);
        }

        if (tasksViewController != null) {
            tasksViewController.refresh();
        }

        if (dialogStage != null)
            dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        if (dialogStage != null)
            dialogStage.close();
    }
}
