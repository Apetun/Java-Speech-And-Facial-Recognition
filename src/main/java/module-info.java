module com.example.oops {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires sphinx4.core;

    opens com.example.oops to javafx.fxml;
    exports com.example.oops;
}