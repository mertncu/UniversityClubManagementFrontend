module com.mertncu.universityclubmanagementsystemfrontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    exports com.mertncu.universityclubmanagementsystemfrontend to javafx.graphics;
    opens com.mertncu.universityclubmanagementsystemfrontend.controller to javafx.fxml;
    opens com.mertncu.universityclubmanagementsystemfrontend.model to com.google.gson, javafx.base;
}