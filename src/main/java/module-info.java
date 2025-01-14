module com.mertncu.universityclubmanagementsystemfrontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires static lombok;
    requires com.fasterxml.jackson.databind;

    opens com.mertncu.universityclubmanagementsystemfrontend to javafx.fxml;
    exports com.mertncu.universityclubmanagementsystemfrontend;
}