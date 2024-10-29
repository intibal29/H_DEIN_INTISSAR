module com.example.h {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.h to javafx.fxml;
    exports com.example.h;
}