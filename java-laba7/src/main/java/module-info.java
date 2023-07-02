module com.example.javalaba7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;


    opens com.example.javalaba7 to javafx.fxml;
    exports com.example.javalaba7;
}