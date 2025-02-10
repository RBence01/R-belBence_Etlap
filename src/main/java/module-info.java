module hu.petrik.etlap {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens hu.petrik.etlap to javafx.fxml;
    exports hu.petrik.etlap;
}