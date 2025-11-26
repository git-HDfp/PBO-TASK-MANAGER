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

    // Referensi ke elemen FXML
    @FXML private BorderPane rootPane;
    @FXML private Label welcomeLabel;

    // Labels untuk Summary Cards
    @FXML private Label totalTasksLabel;
    @FXML private Label activeTasksLabel;
    @FXML private Label completedTasksLabel;

    // Charts
    @FXML private PieChart statusPieChart;
    @FXML private BarChart<String, Number> productivityChart;

    @FXML
    public void initialize() {
        // 1. Animasi Masuk (Fade In) saat halaman dibuka
        rootPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), rootPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // 2. Set nama user
        // Pastikan LoginController.currentUsername sudah terisi saat login
        String username = LoginController.currentUsername != null ? LoginController.currentUsername : "User";
        welcomeLabel.setText("Hi, " + username);

        // 3. Muat data ke dashboard
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Ambil semua task milik user ini
        List<Task> tasks = CSVHelper.getTasksByUsername(LoginController.currentUsername);

        // Hitung jumlah berdasarkan status
        int draft = 0;
        int progress = 0;
        int editing = 0;
        int done = 0;

        for (Task t : tasks) {
            // Pastikan status tidak null sebelum di-switch
            String status = t.getStatus() != null ? t.getStatus() : "draft";
            switch (status) {
                case "draft" -> draft++;
                case "in_progress" -> progress++;
                case "editing" -> editing++;
                case "done" -> done++;
            }
        }

        // --- UPDATE SUMMARY CARDS ---
        totalTasksLabel.setText(String.valueOf(tasks.size()));
        // Active tasks = In Progress + Editing
        activeTasksLabel.setText(String.valueOf(progress + editing));
        completedTasksLabel.setText(String.valueOf(done));

        // --- UPDATE PIE CHART ---
        statusPieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Draft", draft),
                new PieChart.Data("In Progress", progress),
                new PieChart.Data("Editing", editing),
                new PieChart.Data("Done", done)
        ));

        // --- UPDATE BAR CHART ---
        // Bersihkan data lama jika ada
        productivityChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks");

        series.getData().add(new XYChart.Data<>("Draft", draft));
        series.getData().add(new XYChart.Data<>("In Prog", progress));
        series.getData().add(new XYChart.Data<>("Editing", editing));
        series.getData().add(new XYChart.Data<>("Done", done));

        productivityChart.getData().add(series);
    }

    // --- NAVIGASI ---

    @FXML
    private void handleGoToTasks() {
        // Pindah ke halaman TasksView.fxml
        animateAndChangeScene("/view/TasksView.fxml");
    }

    @FXML
    private void handleLogout() {
        // Kembali ke halaman Login.fxml
        animateAndChangeScene("/view/Login.fxml");
    }

    // Helper method untuk animasi transisi keluar (Fade Out) sebelum ganti scene
    private void animateAndChangeScene(String fxmlPath) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), rootPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Setelah animasi selesai, baru ganti halaman
        fadeOut.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent nextRoot = loader.load();

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.getScene().setRoot(nextRoot);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Gagal memuat halaman: " + fxmlPath);
            }
        });

        fadeOut.play();
    }
}