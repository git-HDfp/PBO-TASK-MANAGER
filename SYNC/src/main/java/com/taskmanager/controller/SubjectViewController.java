package com.taskmanager.controller;

import com.taskmanager.component.CustomAlertDialog;
import com.taskmanager.utils.SubjectHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Optional;

public class SubjectViewController {

    @FXML
    private TextField subjectField;
    @FXML
    private ListView<String> subjectListView;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnClose;

    private ObservableList<String> subjects;

    @FXML
    public void initialize() {
        loadSubjects();

        // Add listener to selection to enable/disable buttons
        subjectListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            btnEdit.setDisable(!hasSelection);
            btnDelete.setDisable(!hasSelection);

            if (hasSelection) {
                subjectField.setText(newVal);
            }
        });

        btnEdit.setDisable(true);
        btnDelete.setDisable(true);
    }

    private void loadSubjects() {
        subjects = FXCollections.observableArrayList(SubjectHelper.getAllSubjects());
        subjectListView.setItems(subjects);
    }

    @FXML
    private void handleAdd() {
        String newSubject = subjectField.getText().trim();
        if (newSubject.isEmpty()) {
            showAlert("Error", "Subject name cannot be empty.");
            return;
        }

        if (SubjectHelper.addSubject(newSubject)) {
            loadSubjects();
            subjectField.clear();
            showAlert("Success", "Subject added successfully.");
        } else {
            showAlert("Error", "Subject already exists or invalid.");
        }
    }

    @FXML
    private void handleEdit() {
        String selectedSubject = subjectListView.getSelectionModel().getSelectedItem();
        if (selectedSubject == null)
            return;

        // For now, use the text field value as the new name
        String newName = subjectField.getText().trim();
        if (newName.isEmpty()) {
            showAlert("Error", "Subject name cannot be empty.");
            return;
        }

        if (newName.equals(selectedSubject))
            return;

        if (SubjectHelper.updateSubject(selectedSubject, newName)) {
            loadSubjects();
            subjectField.clear();
            showAlert("Success", "Subject updated successfully.");
        } else {
            showAlert("Error", "Failed to update subject.");
        }
    }

    @FXML
    private void handleDelete() {
        String selectedSubject = subjectListView.getSelectionModel().getSelectedItem();
        if (selectedSubject == null)
            return;

        boolean confirmed = CustomAlertDialog.showConfirmation("Delete Subject",
                "Are you sure you want to delete '" + selectedSubject + "'?\nThis action cannot be undone.");

        if (confirmed) {
            if (SubjectHelper.deleteSubject(selectedSubject)) {
                loadSubjects();
                subjectField.clear();
                showAlert("Success", "Subject deleted successfully.");
            } else {
                showAlert("Error", "Failed to delete subject.");
            }
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        if (title.equals("Error")) {
            CustomAlertDialog.showError(title, content);
        } else {
            CustomAlertDialog.showSuccess(title, content);
        }
    }
}
