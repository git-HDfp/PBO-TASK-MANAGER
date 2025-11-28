module com.taskmanager {
    // Library JavaFX yang dibutuhkan
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;

    // Library Tambahan (ControlsFX, FormsFX, dll - sesuai pom.xml Anda)
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // IZINKAN AKSES KE PACKAGE ANDA
    // 'opens' digunakan agar @FXML di controller bisa dibaca oleh JavaFX
    opens com.taskmanager to javafx.fxml;
    opens com.taskmanager.controller to javafx.fxml;
    opens com.taskmanager.model to javafx.base; // Penting agar TableView/Charts bisa baca data Model

    // EKSPOR PACKAGE AGAR BISA DIAKSES KELUAR
    exports com.taskmanager;
    exports com.taskmanager.controller;
    exports com.taskmanager.model;
    exports com.taskmanager.utils;
}