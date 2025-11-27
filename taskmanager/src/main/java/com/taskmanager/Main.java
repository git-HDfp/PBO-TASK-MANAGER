
package com.taskmanager;

import com.taskmanager.utils.CSVHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Inisialisasi File CSV (Pastikan folder data/ dibuat)
        CSVHelper.initializeFiles();

        // 2. Load Halaman Login sebagai tampilan awal
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            // 3. Setup Scene
            // Ukuran default 1000x650 untuk login, nanti Dashboard akan lebih besar
            Scene scene = new Scene(root, 1000, 650);
            scene.setFill(javafx.scene.paint.Color.web("#13141f"));

            // 4. Load CSS Global (PENTING: Agar style.css terbaca dari awal)
            String css = getClass().getResource("/view/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            // 5. Setup Stage (Jendela Aplikasi)
            primaryStage.setTitle("Task Management - YuAi Team");
            primaryStage.setScene(scene);

            // Opsional: Tambahkan icon jika ada file icon
            // primaryStage.getIcons().add(new
            // Image(getClass().getResourceAsStream("/icon.png")));

            primaryStage.setResizable(false); // Login screen fixed size
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("GAGAL MEMUAT HALAMAN LOGIN: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}