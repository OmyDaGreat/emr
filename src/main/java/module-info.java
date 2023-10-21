module com.emr.emr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.common;


    opens com.emr.emr to javafx.fxml;
    exports com.emr.emr;
}