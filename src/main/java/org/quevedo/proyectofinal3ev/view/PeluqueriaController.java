package org.quevedo.proyectofinal3ev.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.quevedo.proyectofinal3ev.DAO.MascotaDAO;
import org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO;
import org.quevedo.proyectofinal3ev.DAO.ServicioPeluqueriaDAO;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PeluqueriaController extends Controller implements Initializable {

    @FXML
    private TableView<Mascota> tableMascotas;
    @FXML
    private TableColumn<Mascota, Integer> colId;
    @FXML
    private TableColumn<Mascota, String> colNombre;
    @FXML
    private TableColumn<Mascota, String> colEspecie;
    @FXML
    private TableColumn<Mascota, String> colRaza;
    @FXML
    private TableColumn<Mascota, String> colFechaNacimiento;
    @FXML
    private TableView<VisitaVeterinaria> tableHistorialVisitas;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colFechaVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMotivoVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colObservacionesVisita;
    @FXML
    private TextArea txtNotasPersonales;
    @FXML
    private TableView<ServicioPeluqueria> tableCitasDia;
    @FXML
    private ObservableList<ServicioPeluqueria> citasDia;

    private ObservableList<Mascota> mascotas;
    private ObservableList<VisitaVeterinaria> historialVisitas;

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla de mascotas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        colRaza.setCellValueFactory(new PropertyValueFactory<>("raza"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));

        // Configurar columnas de la tabla de historial de visitas
        colFechaVisita.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colMotivoVisita.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colObservacionesVisita.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Cargar datos iniciales
        cargarMascotas();
        cargarHistorialVisitas();
    }

    private void cargarMascotas() {
        mascotas = FXCollections.observableArrayList(MascotaDAO.getAllMascotas());
        tableMascotas.setItems(mascotas);
    }

    private void cargarHistorialVisitas() {
        try {
            List<VisitaVeterinaria> listaVisitas = VisitaVeterinariaDAO.getAllVisitas();
            historialVisitas = FXCollections.observableArrayList(listaVisitas != null ? listaVisitas : new ArrayList<>());
            tableHistorialVisitas.setItems(historialVisitas);
        } catch (RuntimeException e) {
            mostrarAlerta("Error al cargar el historial de visitas: " + e.getMessage());
            historialVisitas = FXCollections.observableArrayList(); // Inicializar como lista vacía
            tableHistorialVisitas.setItems(historialVisitas);
        }
    }

    @FXML
    private void agregarMascota() {
        // Lógica para agregar una nueva mascota (puedes abrir un diálogo para ingresar los datos)
        System.out.println("Agregar mascota - Implementar lógica aquí");
    }

    @FXML
    private void eliminarMascota() {
        Mascota seleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            MascotaDAO.delete(seleccionada.getId());
            mascotas.remove(seleccionada);
        } else {
            mostrarAlerta("Seleccione una mascota para eliminar.");
        }
    }

    @FXML
    private void agregarCita() {
        Optional<ServicioPeluqueria> result = showCitaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                ServicioPeluqueriaDAO.insert(nuevaCita);
                citasDia.add(nuevaCita);
                tableCitasDia.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    @FXML
    private void modificarCita() {
        ServicioPeluqueria citaSeleccionada = tableCitasDia.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            Optional<ServicioPeluqueria> result = showCitaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    ServicioPeluqueriaDAO.update(citaModificada);
                    tableCitasDia.refresh();
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
        ServicioPeluqueria citaSeleccionada = tableCitasDia.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            try {
                ServicioPeluqueriaDAO.delete(citaSeleccionada.getId());
                citasDia.remove(citaSeleccionada);
                tableCitasDia.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la cita: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una cita para eliminar.");
        }
    }

    @FXML
    private void verHistorial() {
        try {
            List<ServicioPeluqueria> historial = ServicioPeluqueriaDAO.getHistorialServicios();
            if (historial.isEmpty()) {
                mostrarAlerta("No hay historial de servicios disponible.");
            } else {
                // Mostrar historial en una nueva ventana o tabla
                System.out.println("Historial de servicios cargado.");
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar el historial de servicios: " + e.getMessage());
        }
    }

    @FXML
    private void gestionarServicios() {
        try {
            // Lógica para gestionar servicios (puede abrir un diálogo o nueva vista)
            System.out.println("Gestionar servicios - Implementar lógica aquí.");
        } catch (Exception e) {
            mostrarAlerta("Error al gestionar servicios: " + e.getMessage());
        }
    }

    @FXML
    private void salirAplicacion() {
        System.exit(0);
    }

    private Optional<ServicioPeluqueria> showCitaDialog(ServicioPeluqueria cita) {
        Dialog<ServicioPeluqueria> dialog = new Dialog<>();
        dialog.setTitle(cita == null ? "Agregar Cita" : "Modificar Cita");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(cita != null ? cita.getFecha() : LocalDate.now());
        TextField tipoServicioField = new TextField(cita != null ? cita.getTipoServicio() : "");
        TextField precioField = new TextField(cita != null ? String.valueOf(cita.getPrecio()) : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Tipo de Servicio:"), 0, 1);
        grid.add(tipoServicioField, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(precioField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                ServicioPeluqueria nuevaCita = cita != null ? cita : new ServicioPeluqueria();
                nuevaCita.setFecha(fechaPicker.getValue());
                nuevaCita.setTipoServicio(tipoServicioField.getText());
                nuevaCita.setPrecio(Double.parseDouble(precioField.getText()));
                return nuevaCita;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
