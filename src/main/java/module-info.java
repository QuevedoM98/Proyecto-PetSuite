module org.quevedo.proyectofinal3ev {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.xml.bind;


    opens org.quevedo.proyectofinal3ev to javafx.fxml;
    exports org.quevedo.proyectofinal3ev;
    exports org.quevedo.proyectofinal3ev.basedatos;
    opens org.quevedo.proyectofinal3ev.basedatos to javafx.fxml;
    opens org.quevedo.proyectofinal3ev.view to javafx.fxml;
    opens org.quevedo.proyectofinal3ev.controller to javafx.fxml;
}