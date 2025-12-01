package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateLabel;
    @FXML
    private Label avatarLabel;

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label highPriorityLabel;
    @FXML
    private Label mediumPriorityLabel;
    @FXML
    private Label lowPriorityLabel;
    @FXML
    private Label completionRateLabel;

    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> productivityChart;

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
    private Button btnNotification;
    @FXML
    private Label notificationBadge;
    @FXML
    private Label notificationMessage;

    private boolean isSidebarCollapsed = false;
    private static final double SIDEBAR_EXPANDED_WIDTH = 260;
    private static final double SIDEBAR_COLLAPSED_WIDTH = 70;

    @FXML
    public void initialize() {
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        String username = LoginController.currentUsername != null ? LoginController.currentUsername : "User";
        welcomeLabel.setText(username);

        dateLabel.setText(
                java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        if (username != null && !username.isEmpty()) {
            avatarLabel.setText(username.substring(0, 1).toUpperCase());
        }

        if (sidebar != null) {
            sidebar.setPrefWidth(SIDEBAR_EXPANDED_WIDTH);
            if (sidebarTitle != null)
                sidebarTitle.setVisible(true);
            if (menuTitle != null)
                menuTitle.setVisible(true);
            if (accountTitle != null)
                accountTitle.setVisible(true);

            ContentDisplay contentDisplay = ContentDisplay.LEFT;
            if (btnDashboard != null)
                btnDashboard.setContentDisplay(contentDisplay);
            if (btnTasks != null)
                btnTasks.setContentDisplay(contentDisplay);
            if (btnProfile != null)
                btnProfile.setContentDisplay(contentDisplay);
            if (btnLogout != null)
                btnLogout.setContentDisplay(contentDisplay);
        }

        loadDashboardData();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        List<Task> urgentTasks = getUrgentTasks();
        int count = urgentTasks.size();

        if (count > 0) {
            notificationBadge.setText(String.valueOf(count));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }

    private List<Task> getUrgentTasks() {
        List<Task> urgent = new ArrayList<>();
        List<Task> allTasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);
        LocalDateTime now = LocalDateTime.now();

        for (Task task : allTasks) {
            if ("done".equals(task.getStatus()))
                continue;

            if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                try {
                    LocalDateTime deadline = LocalDateTime.parse(task.getDeadline());

                    if (deadline.isBefore(now.plusHours(24))) {
                        urgent.add(task);
                    }
                } catch (Exception e) {

                }
            }
        }
        return urgent;
    }

    @FXML
    private void handleNotification() {
        List<Task> urgentTasks = getUrgentTasks();

        if (urgentTasks.isEmpty()) {

            notificationMessage
                    .setText("🎉 You're all caught up! No urgent tasks to worry about.");
            notificationMessage.setVisible(true);

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(5), notificationMessage);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> notificationMessage.setVisible(false));
            fadeOut.play();
        } else {
            Stage dialog = new Stage();
            dialog.setTitle("Urgent Tasks");
            dialog.initModality(Modality.APPLICATION_MODAL);

            VBox container = new VBox(15);
            container.setStyle("-fx-padding: 20; -fx-background-color: #1a1a2e;");

            Label titleLabel = new Label(String.format("%d Deadlines Approaching!", urgentTasks.size()));
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
            container.getChildren().add(titleLabel);

            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMM - HH:mm",
                    new Locale("id", "ID"));

            VBox tasksContainer = new VBox(10);
            tasksContainer.setStyle("-fx-background-color: transparent;");

            for (Task task : urgentTasks) {
                HBox taskRow = new HBox(15);
                taskRow.setAlignment(Pos.CENTER_LEFT);
                taskRow.setStyle(
                        "-fx-background-color: #25274d; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

                Label icon = new Label("📋");
                icon.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-min-width: 40; -fx-alignment: center;");

                VBox taskInfo = new VBox(5);
                HBox.setHgrow(taskInfo, Priority.ALWAYS);

                Label taskTitle = new Label(task.getTitle());
                taskTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");
                taskInfo.getChildren().add(taskTitle);

                if (task.getCategory() != null && !task.getCategory().isEmpty()) {
                    Label taskSubject = new Label(task.getCategory());
                    taskSubject.setStyle("-fx-font-size: 12px; -fx-text-fill: #a0a0a0;");
                    taskInfo.getChildren().add(taskSubject);
                }

                VBox deadlineBox = new VBox(3);
                deadlineBox.setAlignment(Pos.CENTER_RIGHT);

                if (task.getDeadline() != null && !task.getDeadline().isEmpty()) {
                    try {
                        LocalDateTime deadline = LocalDateTime.parse(task.getDeadline());
                        LocalDateTime now = LocalDateTime.now();
                        String formattedDeadline = deadline.format(dayFormatter);

                        Label deadlineLabel = new Label(formattedDeadline);

                        boolean isOverdue = now.isAfter(deadline);

                        if (isOverdue) {

                            deadlineLabel
                                    .setStyle("-fx-font-size: 13px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

                            Label overdueLabel = new Label("OVERDUE");
                            overdueLabel
                                    .setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

                            deadlineBox.getChildren().addAll(deadlineLabel, overdueLabel);
                        } else {

                            deadlineLabel
                                    .setStyle("-fx-font-size: 13px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            deadlineBox.getChildren().add(deadlineLabel);
                        }
                    } catch (Exception e) {
                        Label deadlineLabel = new Label(task.getDeadline());
                        deadlineLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                        deadlineBox.getChildren().add(deadlineLabel);
                    }
                }

                taskRow.getChildren().addAll(icon, taskInfo, deadlineBox);
                tasksContainer.getChildren().add(taskRow);
            }

            ScrollPane scrollPane = new ScrollPane(tasksContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(300);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            scrollPane.getStyleClass().add("urgent-scroll");

            container.getChildren().add(scrollPane);

            Label encouragementLabel = new Label("Let's get these tasks done! 💪");
            encouragementLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;");

            Button closeBtn = new Button("Close");
            closeBtn.setStyle(
                    "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 10 30; -fx-background-radius: 5; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
            closeBtn.setOnAction(e -> dialog.close());

            HBox btnBox = new HBox(encouragementLabel);
            btnBox.setAlignment(Pos.CENTER_LEFT);
            btnBox.setSpacing(20);
            btnBox.setStyle("-fx-padding: 10 0 0 0;");

            HBox closeBox = new HBox(closeBtn);
            closeBox.setAlignment(Pos.CENTER_RIGHT);
            closeBox.setStyle("-fx-padding: 10 0 0 0;");

            container.getChildren().addAll(btnBox, closeBox);

            Scene scene = new Scene(container, 600, 500);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());
            dialog.setScene(scene);
            dialog.showAndWait();
        }
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

        ContentDisplay contentDisplay = isSidebarCollapsed ? ContentDisplay.GRAPHIC_ONLY : ContentDisplay.LEFT;
        if (btnDashboard != null)
            btnDashboard.setContentDisplay(contentDisplay);
        if (btnTasks != null)
            btnTasks.setContentDisplay(contentDisplay);
        if (btnProfile != null)
            btnProfile.setContentDisplay(contentDisplay);
        if (btnLogout != null)
            btnLogout.setContentDisplay(contentDisplay);
    }

    private void loadDashboardData() {
        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        int draft = 0;
        int progress = 0;
        int done = 0;

        int highPriority = 0;
        int mediumPriority = 0;
        int lowPriority = 0;

        for (Task t : tasks) {
            String status = t.getStatus() != null ? t.getStatus() : "draft";
            switch (status) {
                case "draft" -> draft++;
                case "in_progress" -> progress++;
                case "done" -> done++;
            }

            String priority = t.getPriority() != null ? t.getPriority().toLowerCase() : "low";
            switch (priority) {
                case "high" -> highPriority++;
                case "medium" -> mediumPriority++;
                case "low" -> lowPriority++;
            }
        }

        totalTasksLabel.setText(String.valueOf(tasks.size()));
        highPriorityLabel.setText(String.valueOf(highPriority));
        mediumPriorityLabel.setText(String.valueOf(mediumPriority));
        lowPriorityLabel.setText(String.valueOf(lowPriority));

        int total = tasks.size();
        int completionRate = total > 0 ? (int) Math.round(((double) done / total) * 100) : 0;
        if (completionRateLabel != null) {
            completionRateLabel.setText(completionRate + "%");
        }

        statusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Draft", draft),
                new PieChart.Data("In Progress", progress),
                new PieChart.Data("Done", done)));

        productivityChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks");
        series.getData().add(new XYChart.Data<>("Draft", draft));
        series.getData().add(new XYChart.Data<>("In Progress", progress));
        series.getData().add(new XYChart.Data<>("Done", done));
        productivityChart.getData().add(series);
    }

    @FXML
    private void handleGoToTasks() {
        animateAndChangeScene("/view/TasksView.fxml");
    }

    @FXML
    private void handleGoToProfile() {
        animateAndChangeScene("/view/ProfileView.fxml");
    }

    @FXML
    private void handleLogout() {
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
}
