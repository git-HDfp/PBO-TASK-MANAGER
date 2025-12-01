package com.taskmanager.controller;

import com.taskmanager.component.TaskCard;
import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import com.taskmanager.utils.SubjectHelper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class TasksViewController {
    @FXML
    private BorderPane rootPane;

    @FXML
    private VBox draftColumn;
    @FXML
    private VBox inProgressColumn;
    @FXML
    private VBox doneColumn;

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
    private Button btnDashboard;
    @FXML
    private Button btnTasks;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnLogout;

    @FXML
    private Label subjectsTitle;
    @FXML
    private ScrollPane subjectsScrollPane;
    @FXML
    private VBox subjectsContainer;
    @FXML
    private Button btnManageSubjects;

    private boolean isSidebarCollapsed = false;
    private static final double SIDEBAR_EXPANDED_WIDTH = 260;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 70;

    private String currentFilterSubject = null; // null = show all

    @FXML
    public void initialize() {
        rootPane.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), rootPane);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        setupColumnDrag(draftColumn, "draft");
        setupColumnDrag(inProgressColumn, "in_progress");
        setupColumnDrag(doneColumn, "done");

        if (sidebar != null) {
            sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
            if (sidebarTitle != null)
                sidebarTitle.setVisible(true);
            if (menuTitle != null)
                menuTitle.setVisible(true);
            if (accountTitle != null)
                accountTitle.setVisible(true);

            ContentDisplay cd = ContentDisplay.LEFT;
            if (btnDashboard != null)
                btnDashboard.setContentDisplay(cd);
            if (btnTasks != null)
                btnTasks.setContentDisplay(cd);
            if (btnProfile != null)
                btnProfile.setContentDisplay(cd);
            if (btnLogout != null)
                btnLogout.setContentDisplay(cd);
        }

        populateSubjects();
        refresh();
    }

    private void populateSubjects() {
        if (subjectsContainer == null)
            return;

        subjectsContainer.getChildren().clear();
        List<String> subjects = SubjectHelper.getAllSubjects();

        for (String subject : subjects) {
            Button subjectBtn = new Button(subject);
            subjectBtn.setMaxWidth(Double.MAX_VALUE);
            subjectBtn.getStyleClass().add("nav-btn");

            if (subject.equals(currentFilterSubject)) {
                subjectBtn.getStyleClass().add("nav-btn-active");
            }

            subjectBtn.setOnAction(e -> {

                if (subject.equals(currentFilterSubject)) {
                    currentFilterSubject = null;
                } else {
                    currentFilterSubject = subject;
                }
                populateSubjects(); // Repopulate to update visual state
                refresh();
            });

            subjectsContainer.getChildren().add(subjectBtn);
        }
    }

    @FXML
    private void handleManageSubjects() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SubjectView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT); // Clean look
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            stage.setScene(scene);

            stage.setOnHidden(e -> populateSubjects());

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open Subject Manager");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleMyTasks() {

        currentFilterSubject = null;
        refresh();
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
        if (subjectsTitle != null)
            subjectsTitle.setVisible(true);

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

    private void setupColumnDrag(VBox column, String status) {
        column.setOnDragOver(event -> {
            if (event.getGestureSource() != column && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        column.setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasString()) {
                String taskId = event.getDragboard().getString();

                List<Task> allTasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);
                for (Task t : allTasks) {
                    if (t.getId().equals(taskId)) {
                        String oldStatus = t.getStatus();

                        t.setStatus(status);

                        if ("draft".equals(status)) {

                            t.setProgress(0);
                        } else if ("in_progress".equals(status)) {
                            if ("draft".equals(oldStatus)) {

                                t.setProgress(10);
                            } else if ("done".equals(oldStatus)) {

                                t.setProgress(90);
                            }
                        } else if ("done".equals(status)) {

                            t.setProgress(100);
                        }

                        CSVHelper.updateTask(t);
                        success = true;
                        break;
                    }
                }
                refresh();
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public void refresh() {
        draftColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();

        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        for (Task task : tasks) {

            if (currentFilterSubject != null && !task.getCategory().equals(currentFilterSubject)) {
                continue;
            }

            TaskCard card = new TaskCard(task, this);

            switch (task.getStatus()) {
                case "draft" -> draftColumn.getChildren().add(card);
                case "in_progress" -> inProgressColumn.getChildren().add(card);
                case "done" -> doneColumn.getChildren().add(card);
                default -> draftColumn.getChildren().add(card);
            }
        }
    }

    public void handleEditTask(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TaskEditor.fxml"));
            Parent root = loader.load();

            TaskEditorController controller = loader.getController();
            controller.setTasksViewController(this);
            controller.setTask(task);

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

    public void handleDeleteTask(Task task) {

        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Delete Task");

        VBox dialogRoot = new VBox(20);
        dialogRoot.setStyle("-fx-background-color: transparent;");
        dialogRoot.setPadding(new Insets(0));
        dialogRoot.setAlignment(Pos.CENTER);

        VBox glassPanel = new VBox(20);
        glassPanel.getStyleClass().add("glass-panel");
        glassPanel.setPadding(new Insets(30));
        glassPanel.setAlignment(Pos.CENTER);
        glassPanel.setMaxWidth(400);
        glassPanel.setMaxHeight(Region.USE_PREF_SIZE);

        Label iconLabel = new Label("⚠️");
        iconLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: #f38ba8;");

        Label titleLabel = new Label("Delete Task");
        titleLabel.getStyleClass().add("title-large");
        titleLabel.setStyle(titleLabel.getStyle() + "-fx-text-fill: #f38ba8;");

        Label messageLabel = new Label("Are you sure you want to delete\n\"" + task.getTitle() + "\"?");
        messageLabel.getStyleClass().add("subtitle");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.getStyleClass().add("btn-glass-primary");
        cancelBtn.setOnAction(e -> dialogStage.close());

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-glass-secondary");
        deleteBtn.setOnAction(e -> {
            CSVHelper.deleteTask(task.getId());
            refresh();
            dialogStage.close();
        });

        buttonBox.getChildren().addAll(cancelBtn, deleteBtn);

        glassPanel.getChildren().addAll(iconLabel, titleLabel, messageLabel, buttonBox);

        dialogRoot.getChildren().add(glassPanel);

        Scene scene = new Scene(dialogRoot);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        dialogRoot.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogRoot);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        dialogStage.setScene(scene);
        dialogStage.showAndWait();
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

