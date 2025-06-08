package org.quevedo.proyectofinal3ev.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class VeterinariaController extends Controller implements Initializable {

    @FXML
    private TableView<VisitaVeterinaria> tableCitas;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colHora;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMascota;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colDueno;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMotivo;
    @FXML
    private Button btnNuevaConsulta;
    @FXML
    private Button btnVerMascotas;
    @FXML
    private Button btnInventario;
    @FXML
    private Label lblVacunasPendientes; // Asegurarse de que esté vinculado correctamente
    @FXML
    private ImageView iconVacunas;

    private ObservableList<VisitaVeterinaria> citas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas de la tabla
        colHora.setCellValueFactory(new PropertyValueFactory<>("fecha")); // Asume que "fecha" es un atributo de tipo String o LocalDate
        colMascota.setCellValueFactory(new PropertyValueFactory<>("nombreMascota")); // Asegúrate de que exista un getter para este atributo
        colDueno.setCellValueFactory(new PropertyValueFactory<>("nombreDueno")); // Asegúrate de que exista un getter para este atributo
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo")); // Asume que "motivo" es un atributo de tipo String

        // Cargar datos iniciales
        cargarCitasDelDia();
        actualizarVacunasPendientes();
    }

    private void cargarCitasDelDia() {
        List<VisitaVeterinaria> listaCitas = VisitaVeterinariaDAO.getAllVisitas();
        citas = FXCollections.observableArrayList(listaCitas);
        tableCitas.setItems(citas);
    }

    private void actualizarVacunasPendientes() {
        if (lblVacunasPendientes != null) { // Verificar que no sea null
            int vacunasPendientes = calcularVacunasPendientes();
            lblVacunasPendientes.setText("Vacunas pendientes: " + vacunasPendientes);
        }
    }

    private int calcularVacunasPendientes() {
        // Lógica para calcular vacunas pendientes (puedes reemplazar con datos reales)
        return 5; // Ejemplo estático
    }

    @FXML
    private void agregarCita() {
        // Lógica para agregar una nueva cita
        Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                VisitaVeterinariaDAO.insert(nuevaCita);
                citas.add(nuevaCita); // Agregar a la lista observable
                tableCitas.refresh(); // Refrescar la tabla
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    @FXML
    private void modificarCita() {
        // Lógica para modificar una cita seleccionada
        VisitaVeterinaria citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    VisitaVeterinariaDAO.update(citaModificada);
                    tableCitas.refresh(); // Refrescar la tabla
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la cita: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una cita para modificar.");
        }
    }

    @FXML
    private void eliminarCita() {
        // Lógica para eliminar una cita seleccionada
        VisitaVeterinaria citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            try {
                VisitaVeterinariaDAO.delete(citaSeleccionada.getId());
                citas.remove(citaSeleccionada); // Eliminar de la lista observable
                tableCitas.refresh(); // Refrescar la tabla
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la cita: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una cita para eliminar.");
        }
    }

    @FXML
    private void verHistorial() {
        // Lógica para mostrar el historial de citas
        try {
            List<VisitaVeterinaria> historial = VisitaVeterinariaDAO.getAllVisitas(); // Obtener todas las visitas
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Historial de Citas");
            alert.setHeaderText("Historial completo de citas");
            StringBuilder contenido = new StringBuilder();
            for (VisitaVeterinaria visita : historial) {
                contenido.append("Fecha: ").append(visita.getFecha())
                        .append(", Mascota: ").append(visita.getMascota().getNombre())
                        .append(", Motivo: ").append(visita.getMotivo())
                        .append("\n");
            }
            alert.setContentText(contenido.toString());
            alert.showAndWait();
        } catch (Exception e) {
            mostrarAlerta("Error al cargar el historial de citas: " + e.getMessage());
        }
    }

    @FXML
    private void gestionarPacientes() {
        // Lógica para gestionar pacientes (puedes personalizar según los requisitos)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Gestión de Pacientes");
        alert.setHeaderText("Funcionalidad en desarrollo");
        alert.setContentText("Aquí podrás gestionar los pacientes en el futuro.");
        alert.showAndWait();
    }

    @FXML
    private void salirAplicacion() {
        // Lógica para salir de la aplicación
        System.exit(0);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Optional<VisitaVeterinaria> showVisitaVeterinariaDialog(VisitaVeterinaria visita) {
        // Implementación del diálogo para agregar o modificar citas
        Dialog<VisitaVeterinaria> dialog = new Dialog<>();
        dialog.setTitle(visita == null ? "Agregar Cita" : "Modificar Cita");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(visita != null ? visita.getFecha() : null);
        TextField motivoField = new TextField(visita != null ? visita.getMotivo() : "");
        TextArea observacionesArea = new TextArea(visita != null ? visita.getObservaciones() : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Motivo:"), 0, 1);
        grid.add(motivoField, 1, 1);
        grid.add(new Label("Observaciones:"), 0, 2);
        grid.add(observacionesArea, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                VisitaVeterinaria nuevaVisita = visita != null ? visita : new VisitaVeterinaria();
                nuevaVisita.setFecha(fechaPicker.getValue());
                nuevaVisita.setMotivo(motivoField.getText());
                nuevaVisita.setObservaciones(observacionesArea.getText());
                return nuevaVisita;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}