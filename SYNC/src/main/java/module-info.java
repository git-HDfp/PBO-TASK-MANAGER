module com.taskmanager {

    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires transitive javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens com.taskmanager to javafx.fxml;
    opens com.taskmanager.controller to javafx.fxml;
    opens com.taskmanager.model to javafx.base;

    exports com.taskmanager;
    exports com.taskmanager.controller;
    exports com.taskmanager.model;
    exports com.taskmanager.utils;
}
