module mpwidget.module {
    opens de.mediaportal.mpwidget.persistence;
    opens de.mediaportal.mpwidget.view;
    opens de.mediaportal.mpwidget;
    opens de.mediaportal.mpwidget.model;

    requires java.sql;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
}
