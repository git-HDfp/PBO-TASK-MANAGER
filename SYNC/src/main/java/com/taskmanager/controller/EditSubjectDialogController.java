package com.taskmanager.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditSubjectDialogController {

    @FXML
    private TextField subjectNameField;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    private String newSubjectName;
    private boolean saved = false;

    public void setSubjectName(String subjectName) {
        this.subjectNameField.setText(subjectName);

        this.subjectNameField.selectAll();

        this.subjectNameField.requestFocus();
    }

    @FXML
    private void handleSave() {
        String trimmedName = subjectNameField.getText().trim();

        if (trimmedName.isEmpty()) {

            subjectNameField.getStyleClass().add("error-input");
            return;
        }

        this.newSubjectName = trimmedName;
        this.saved = true;
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        this.saved = false;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean isSaved() {
        return saved;
    }

    public String getNewSubjectName() {
        return newSubjectName;
    }
}

