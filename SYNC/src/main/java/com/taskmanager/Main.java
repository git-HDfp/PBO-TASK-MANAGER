
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

        CSVHelper.initializeFiles();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 650);
            scene.setFill(javafx.scene.paint.Color.web("#13141f"));

            String css = getClass().getResource("/view/style.css").toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("SYNC - Study Your Next Coursework");
            primaryStage.setScene(scene);

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
