module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
}