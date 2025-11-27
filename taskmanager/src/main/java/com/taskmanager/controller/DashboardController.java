package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.utils.CSVHelper;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.List;

public class DashboardController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalTasksLabel;
    @FXML
    private Label highPriorityLabel;
    @FXML
    private Label mediumPriorityLabel;
    @FXML
    private Label lowPriorityLabel;

    @FXML
    private PieChart statusPieChart;
    @FXML
    private BarChart<String, Number> productivityChart;

    @FXML
    public void initialize() {
        // Smooth fade in animation
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Set username
        String username = LoginController.currentUsername != null ? LoginController.currentUsername : "User";
        welcomeLabel.setText("Hi, " + username);

        // Load dashboard data
        loadDashboardData();
    }

    private void loadDashboardData() {
        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        int draft = 0;
        int progress = 0;
        int editing = 0;
        int done = 0;

        int highPriority = 0;
        int mediumPriority = 0;
        int lowPriority = 0;

        for (Task t : tasks) {
            String status = t.getStatus() != null ? t.getStatus() : "draft";
            switch (status) {
                case "draft" -> draft++;
                case "in_progress" -> progress++;
                case "editing" -> editing++;
                case "done" -> done++;
            }

            String priority = t.getPriority() != null ? t.getPriority().toLowerCase() : "low";
            switch (priority) {
                case "high" -> highPriority++;
                case "medium" -> mediumPriority++;
                case "low" -> lowPriority++;
            }
        }

        // Update cards
        totalTasksLabel.setText(String.valueOf(tasks.size()));
        highPriorityLabel.setText(String.valueOf(highPriority));
        mediumPriorityLabel.setText(String.valueOf(mediumPriority));
        lowPriorityLabel.setText(String.valueOf(lowPriority));

        // Update pie chart
        statusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Draft", draft),
                new PieChart.Data("In Progress", progress),
                new PieChart.Data("Editing", editing),
                new PieChart.Data("Done", done)));

        // Update bar chart
        productivityChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks");
        series.getData().add(new XYChart.Data<>("Draft", draft));
        series.getData().add(new XYChart.Data<>("In Progress", progress));
        series.getData().add(new XYChart.Data<>("Editing", editing));
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

    // Optimized transition with 250ms for smoother feel
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