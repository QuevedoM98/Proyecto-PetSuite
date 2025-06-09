package org.quevedo.proyectofinal3ev.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import org.quevedo.proyectofinal3ev.DAO.MascotaDAO;
import org.quevedo.proyectofinal3ev.DAO.ServicioPeluqueriaDAO;
import org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PeluqueriaController extends Controller implements Initializable {
    @FXML
    private TableView<Mascota> tablePacientes;
    @FXML
    private TableView<VisitaVeterinaria> tableCitas;
    @FXML
    private TableView<VisitaVeterinaria> tableHistorialCitas;
    @FXML private TableView<ServicioPeluqueria> tableCitasDia;
    @FXML private TableView<ServicioPeluqueria> tableHistorialServicios;
    @FXML private TableView<Mascota> tableClientes;

    @FXML private TableColumn<ServicioPeluqueria, String> colHoraCita;
    @FXML private TableColumn<ServicioPeluqueria, String> colMascotaCita;
    @FXML private TableColumn<ServicioPeluqueria, String> colDuenoCita;
    @FXML private TableColumn<ServicioPeluqueria, String> colServicioCita;

    @FXML private TableColumn<ServicioPeluqueria, String> colFechaServicio;
    @FXML private TableColumn<ServicioPeluqueria, String> colMascotaServicio;
    @FXML private TableColumn<ServicioPeluqueria, String> colTipoServicio;
    @FXML private TableColumn<ServicioPeluqueria, String> colPrecioServicio;

    @FXML private TableColumn<Mascota, String> colNombreCliente;
    @FXML private TableColumn<Mascota, String> colEspecieCliente;
    @FXML private TableColumn<Mascota, String> colRazaCliente;
    @FXML private TableColumn<Mascota, String> colDuenoCliente;

    private Usuario usuarioActual;
    private Usuario peluqueroActual;
    private List<Mascota> pacientes;
    private List<VisitaVeterinaria> citas;
    private List<VisitaVeterinaria> historialCitas;

    private void cargarPacientes() {
        if (tablePacientes != null) {
            List<Mascota> listaPacientes = MascotaDAO.getAllMascotas();
            pacientes = FXCollections.observableArrayList(listaPacientes);
            tablePacientes.setItems((ObservableList<Mascota>) pacientes);
        }
    }

    private void cargarHistorialCitas() {
        if (tableHistorialCitas != null) {
            try {
                Usuario veterinarioActual = obtenerVeterinarioActual();
                if (veterinarioActual == null) {
                    mostrarAlerta("No se ha definido el veterinario actual.");
                    return;
                }
                List<VisitaVeterinaria> listaHistorial =
                        VisitaVeterinariaDAO.getAllVisitasPorVeterinaria(veterinarioActual.getId());
                historialCitas = FXCollections.observableArrayList(listaHistorial);
                tableHistorialCitas.setItems((ObservableList<VisitaVeterinaria>) historialCitas);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar todas las citas: " + e.getMessage());
            }
        }
    }

    private VisitaVeterinaria getCitaSeleccionada() {
        VisitaVeterinaria citaSeleccionada = null;

        if (tableCitas != null && tableCitas.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        } else if (tableHistorialCitas != null && tableHistorialCitas.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableHistorialCitas.getSelectionModel().getSelectedItem();
        }

        return citaSeleccionada;
    }

    @FXML
    private void agregarCita() {
        Optional<ServicioPeluqueria> result = showCitaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                ServicioPeluqueriaDAO.insert(nuevaCita);
                cargarCitasDelDia();
                cargarHistorialServicios();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    private ServicioPeluqueria getCitaPeluqueriaSeleccionada() {
        ServicioPeluqueria citaSeleccionada = null;
        if (tableCitasDia != null && tableCitasDia.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableCitasDia.getSelectionModel().getSelectedItem();
        } else if (tableHistorialServicios != null && tableHistorialServicios.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableHistorialServicios.getSelectionModel().getSelectedItem();
        }
        return citaSeleccionada;
    }

    @FXML
    private void modificarCita() {
        ServicioPeluqueria citaSeleccionada = getCitaPeluqueriaSeleccionada();
        if (citaSeleccionada != null) {
            Optional<ServicioPeluqueria> result = showCitaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    ServicioPeluqueriaDAO.update(citaModificada);
                    cargarCitasDelDia();
                    cargarHistorialServicios();
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
        ServicioPeluqueria citaSeleccionada = getCitaPeluqueriaSeleccionada();
        if (citaSeleccionada != null) {
            try {
                ServicioPeluqueriaDAO.delete(citaSeleccionada.getId());
                cargarCitasDelDia();
                cargarHistorialServicios();
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
            // Mostrar historial de servicios de peluquería en una tabla grande
            List<ServicioPeluqueria> historial = ServicioPeluqueriaDAO.getHistorialServicios();

            TableView<ServicioPeluqueria> tablaGrande = new TableView<>();
            tablaGrande.setPrefWidth(1200);
            tablaGrande.setPrefHeight(600);

            TableColumn<ServicioPeluqueria, String> colFecha = new TableColumn<>("Fecha");
            colFecha.setPrefWidth(180);
            colFecha.setCellValueFactory(cellData -> {
                if (cellData.getValue().getFechaHora() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });

            TableColumn<ServicioPeluqueria, String> colMascota = new TableColumn<>("Mascota");
            colMascota.setPrefWidth(200);
            colMascota.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""
                    )
            );

            TableColumn<ServicioPeluqueria, String> colDueno = new TableColumn<>("Dueño");
            colDueno.setPrefWidth(200);
            colDueno.setCellValueFactory(cellData -> {
                if (cellData.getValue().getMascota() != null && cellData.getValue().getMascota().getDuenioMascota() != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                            cellData.getValue().getMascota().getDuenioMascota().getNombreUsuario()
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty("");
                }
            });

            TableColumn<ServicioPeluqueria, String> colTipoServicio = new TableColumn<>("Tipo de Servicio");
            colTipoServicio.setPrefWidth(250);
            colTipoServicio.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipoServicio())
            );

            TableColumn<ServicioPeluqueria, String> colPrecio = new TableColumn<>("Precio");
            colPrecio.setPrefWidth(150);
            colPrecio.setCellValueFactory(cellData ->
                    new javafx.beans.property.SimpleStringProperty(String.format("%.2f", cellData.getValue().getPrecio()))
            );

            tablaGrande.getColumns().addAll(colFecha, colMascota, colDueno, colTipoServicio, colPrecio);
            tablaGrande.setItems(FXCollections.observableArrayList(historial));

            Stage stage = new Stage();
            stage.setTitle("Historial Completo de Servicios de Peluquería");
            stage.setScene(new Scene(tablaGrande));
            stage.setWidth(1200);
            stage.setHeight(650);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error al cargar el historial de servicios: " + e.getMessage());
        }
    }

    @FXML
    private void gestionarPacientes() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Agregar Paciente",
                "Agregar Paciente", "Modificar Paciente", "Eliminar Paciente");
        dialog.setTitle("Gestión de Pacientes");
        dialog.setHeaderText("Elige una acción para pacientes");
        dialog.setContentText("Acción:");

        dialog.showAndWait().ifPresent(opcion -> {
            switch (opcion) {
                case "Agregar Paciente":
                    agregarPaciente();
                    break;
                case "Modificar Paciente":
                    modificarPaciente();
                    break;
                case "Eliminar Paciente":
                    eliminarPaciente();
                    break;
            }
        });
    }

    @FXML
    private void gestionarMascotas() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Agregar Mascota",
                "Agregar Mascota", "Modificar Mascota", "Eliminar Mascota");
        dialog.setTitle("Gestión de Mascotas");
        dialog.setHeaderText("Elige una acción para mascotas");
        dialog.setContentText("Acción:");

        dialog.showAndWait().ifPresent(opcion -> {
            switch (opcion) {
                case "Agregar Mascota":
                    agregarMascota();
                    break;
                case "Modificar Mascota":
                    modificarMascota();
                    break;
                case "Eliminar Mascota":
                    eliminarMascota();
                    break;
            }
        });
    }

    @FXML
    private void salirAplicacion() {
        System.exit(0);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Optional<VisitaVeterinaria> showVisitaVeterinariaDialog(VisitaVeterinaria visita) {
        Dialog<VisitaVeterinaria> dialog = new Dialog<>();
        dialog.setTitle(visita == null ? "Agregar Cita" : "Modificar Cita");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(visita != null ? visita.getFecha() : null);
        TextField horaField = new TextField(visita != null && visita.getFechaHora() != null
                ? visita.getFechaHora().toLocalTime().toString()
                : "");
        TextField motivoField = new TextField(visita != null ? visita.getMotivo() : "");
        TextArea observacionesArea = new TextArea(visita != null ? visita.getObservaciones() : "");

        ComboBox<Mascota> mascotaComboBox = new ComboBox<>();
        List<Mascota> mascotasDisponibles = MascotaDAO.getAllMascotas();
        mascotaComboBox.setItems(FXCollections.observableArrayList(mascotasDisponibles));
        mascotaComboBox.setCellFactory(lv -> new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        mascotaComboBox.setButtonCell(new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        if (visita != null && visita.getMascota() != null) {
            mascotaComboBox.getSelectionModel().select(visita.getMascota());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Hora (HH:mm):"), 0, 1);
        grid.add(horaField, 1, 1);
        grid.add(new Label("Mascota:"), 0, 2);
        grid.add(mascotaComboBox, 1, 2);
        grid.add(new Label("Motivo:"), 0, 3);
        grid.add(motivoField, 1, 3);
        grid.add(new Label("Observaciones:"), 0, 4);
        grid.add(observacionesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                try {
                    LocalDate fecha = fechaPicker.getValue();
                    LocalTime hora = LocalTime.parse(horaField.getText());
                    Mascota mascotaSeleccionada = mascotaComboBox.getValue();
                    if (mascotaSeleccionada == null) {
                        mostrarAlerta("Debe seleccionar una mascota.");
                        return null;
                    }
                    VisitaVeterinaria nuevaVisita = visita != null ? visita : new VisitaVeterinaria();
                    nuevaVisita.setFecha(fecha);
                    nuevaVisita.setFechaHora(LocalDateTime.of(fecha, hora));
                    nuevaVisita.setMascota(mascotaSeleccionada);
                    nuevaVisita.setMotivo(motivoField.getText());
                    nuevaVisita.setObservaciones(observacionesArea.getText());

                    if (nuevaVisita.getVeterinaria() == null) {
                        Usuario veterinarioActual = obtenerVeterinarioActual();
                        if (veterinarioActual == null) {
                            mostrarAlerta("No se ha definido el veterinario actual.");
                            return null;
                        }
                        nuevaVisita.setVeterinaria(veterinarioActual);
                    }

                    return nuevaVisita;
                } catch (Exception e) {
                    mostrarAlerta("Error: Verifique que la hora esté en formato HH:mm.");
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Usuario obtenerVeterinarioActual() {
        return usuarioActual;
    }

    private Usuario obtenerPeluqueroActual() {
        return peluqueroActual;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configuración columnas tabla Citas del Día
        if (colHoraCita != null)
            colHoraCita.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaHora() != null
                    ? cellData.getValue().getFechaHora().toLocalTime().toString()
                    : ""
            ));
        if (colMascotaCita != null)
            colMascotaCita.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""
            ));
        if (colDuenoCita != null)
            colDuenoCita.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMascota() != null && cellData.getValue().getMascota().getDuenioMascota() != null
                    ? cellData.getValue().getMascota().getDuenioMascota().getNombreUsuario()
                    : ""
            ));
        if (colServicioCita != null)
            colServicioCita.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipoServicio()
            ));

        // Configuración columnas tabla Historial de Servicios
        if (colFechaServicio != null)
            colFechaServicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFechaHora() != null
                    ? cellData.getValue().getFechaHora().toLocalDate().toString()
                    : ""
            ));
        if (colMascotaServicio != null)
            colMascotaServicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""
            ));
        if (colTipoServicio != null)
            colTipoServicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTipoServicio()
            ));
        if (colPrecioServicio != null)
            colPrecioServicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                String.format("%.2f", cellData.getValue().getPrecio())
            ));

        // Configuración columnas tabla Clientes
        if (colNombreCliente != null)
            colNombreCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getNombre()
            ));
        if (colEspecieCliente != null)
            colEspecieCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEspecie()
            ));
        if (colRazaCliente != null)
            colRazaCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRaza()
            ));
        if (colDuenoCliente != null)
            colDuenoCliente.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDuenioMascota() != null ? cellData.getValue().getDuenioMascota().getNombreUsuario() : ""
            ));

        // Listeners para selección exclusiva entre tablas de citas
        if (tableCitasDia != null && tableHistorialServicios != null) {
            tableCitasDia.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    tableHistorialServicios.getSelectionModel().clearSelection();
                }
            });
            tableHistorialServicios.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null) {
                    tableCitasDia.getSelectionModel().clearSelection();
                }
            });
        }

        // Cargar datos iniciales
        cargarCitasDelDia();
        cargarHistorialServicios();
        cargarMascotas();
    }

    @Override
    public void onOpen(Object input) throws IOException {
        if (input instanceof Usuario) {
            this.peluqueroActual = (Usuario) input;
        }
        if (peluqueroActual != null) {
            cargarCitasDelDia();
            cargarHistorialServicios();
            cargarMascotas();
        }
    }

    @Override
    public void onClose(Object output) {

    }

    private void cargarCitasDelDia() {
        if (tableCitasDia != null && peluqueroActual != null) {
            try {
                List<ServicioPeluqueria> citasDia = ServicioPeluqueriaDAO.getCitasDelDiaPorPeluqueria(peluqueroActual.getId());
                ObservableList<ServicioPeluqueria> obsCitasDia = FXCollections.observableArrayList(citasDia);
                tableCitasDia.setItems(obsCitasDia);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar las citas del día: " + e.getMessage());
            }
        }
    }

    private void cargarHistorialServicios() {
        if (tableHistorialServicios != null && peluqueroActual != null) {
            try {
                List<ServicioPeluqueria> historial = ServicioPeluqueriaDAO.getHistorialServiciosPorPeluqueria(peluqueroActual.getId());
                ObservableList<ServicioPeluqueria> obsHistorial = FXCollections.observableArrayList(historial);
                tableHistorialServicios.setItems(obsHistorial);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar el historial de servicios: " + e.getMessage());
            }
        }
    }

    @FXML
    private void cargarMascotas() {
        if (tableClientes != null) {
            try {
                List<Mascota> listaMascotas = MascotaDAO.getAllMascotas();
                ObservableList<Mascota> mascotas = FXCollections.observableArrayList(listaMascotas);
                tableClientes.setItems(mascotas);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar las mascotas: " + e.getMessage());
            }
        }
    }

    @FXML
    private void agregarPaciente() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                MascotaDAO.insert(nuevaMascota);
                cargarPacientes();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el paciente: " + e.getMessage());
            }
        });
    }

    @FXML
    private void modificarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            Optional<Mascota> result = showMascotaDialog(mascotaSeleccionada);
            result.ifPresent(mascotaModificada -> {
                try {
                    MascotaDAO.update(mascotaModificada);
                    cargarPacientes();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar el paciente: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para modificar.");
        }
    }

    @FXML
    private void eliminarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarPacientes();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar el paciente: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para eliminar.");
        }
    }

    private void agregarMascota() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.insert(nuevaMascota);
                cargarMascotas();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la mascota: " + e.getMessage());
            }
        });
    }

    private void modificarMascota() {
        Mascota mascotaSeleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            Optional<Mascota> result = showMascotaDialog(mascotaSeleccionada);
            result.ifPresent(mascotaModificada -> {
                try {
                    org.quevedo.proyectofinal3ev.DAO.MascotaDAO.update(mascotaModificada);
                    cargarMascotas();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la mascota: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para modificar.");
        }
    }

    private void eliminarMascota() {
        Mascota mascotaSeleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarMascotas();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la mascota: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para eliminar.");
        }
    }

    @FXML
    private void agregarDueno() {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Agregar Dueño");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        TextField nombreField = new TextField();
        TextField passwordField = new TextField();
        TextField emailField = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre de usuario:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombre = nombreField.getText();
                String password = passwordField.getText();
                String email = emailField.getText();
                if (nombre.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Usuario nuevoDueno = new Usuario();
                nuevoDueno.setNombreUsuario(nombre);
                nuevoDueno.setPassword(password);
                nuevoDueno.setEmail(email);
                nuevoDueno.setTipoUsuario(Usuario.TipoUsuario.DUENO);
                return nuevoDueno;
            }
            return null;
        });

        Optional<Usuario> result = dialog.showAndWait();
        result.ifPresent(nuevoDueno -> {
            try {
                org.quevedo.proyectofinal3ev.DAO.UsuarioDAO.insert(nuevoDueno);
                mostrarInfo("Dueño agregado correctamente.");
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el dueño: " + e.getMessage());
            }
        });
    }

    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private Optional<Mascota> showMascotaDialog(Mascota mascota) {
        Dialog<Mascota> dialog = new Dialog<>();
        dialog.setTitle(mascota == null ? "Agregar Paciente" : "Modificar Paciente");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        TextField nombreField = new TextField(mascota != null ? mascota.getNombre() : "");
        TextField especieField = new TextField(mascota != null ? mascota.getEspecie() : "");
        TextField razaField = new TextField(mascota != null ? mascota.getRaza() : "");
        DatePicker fechaNacimientoPicker = new DatePicker(mascota != null ? mascota.getFechaNacimiento() : null);

        ComboBox<Usuario> duenioComboBox = new ComboBox<>();
        List<Usuario> usuarios = org.quevedo.proyectofinal3ev.DAO.UsuarioDAO.findByType(Usuario.TipoUsuario.DUENO);
        duenioComboBox.setItems(FXCollections.observableArrayList(usuarios));
        duenioComboBox.setCellFactory(lv -> new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombreUsuario());
            }
        });
        duenioComboBox.setButtonCell(new ListCell<Usuario>() {
            @Override
            protected void updateItem(Usuario item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombreUsuario());
            }
        });
        if (mascota != null && mascota.getDuenioMascota() != null) {
            duenioComboBox.getSelectionModel().select(mascota.getDuenioMascota());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Especie:"), 0, 1);
        grid.add(especieField, 1, 1);
        grid.add(new Label("Raza:"), 0, 2);
        grid.add(razaField, 1, 2);
        grid.add(new Label("Fecha de nacimiento:"), 0, 3);
        grid.add(fechaNacimientoPicker, 1, 3);
        grid.add(new Label("Dueño:"), 0, 4);
        grid.add(duenioComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombre = nombreField.getText();
                String especie = especieField.getText();
                String raza = razaField.getText();
                Usuario duenio = duenioComboBox.getValue();
                LocalDate fechaNacimiento = fechaNacimientoPicker.getValue();
                if (nombre.isEmpty() || especie.isEmpty() || raza.isEmpty() || duenio == null || fechaNacimiento == null) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Mascota nuevaMascota = mascota != null ? mascota : new Mascota();
                nuevaMascota.setNombre(nombre);
                nuevaMascota.setEspecie(especie);
                nuevaMascota.setRaza(raza);
                nuevaMascota.setFechaNacimiento(fechaNacimiento);
                nuevaMascota.setDuenioMascota(duenio);
                return nuevaMascota;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<ServicioPeluqueria> showCitaDialog(ServicioPeluqueria servicio) {
        Dialog<ServicioPeluqueria> dialog = new Dialog<>();
        dialog.setTitle(servicio == null ? "Agregar Cita de Peluquería" : "Modificar Cita de Peluquería");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(servicio != null ? servicio.getFecha() : null);
        TextField horaField = new TextField(servicio != null && servicio.getFechaHora() != null
                ? servicio.getFechaHora().toLocalTime().toString()
                : "");
        ComboBox<Mascota> mascotaComboBox = new ComboBox<>();
        List<Mascota> mascotasDisponibles = MascotaDAO.getAllMascotas();
        mascotaComboBox.setItems(FXCollections.observableArrayList(mascotasDisponibles));
        mascotaComboBox.setCellFactory(lv -> new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        mascotaComboBox.setButtonCell(new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        if (servicio != null && servicio.getMascota() != null) {
            mascotaComboBox.getSelectionModel().select(servicio.getMascota());
        }

        TextField tipoServicioField = new TextField(servicio != null ? servicio.getTipoServicio() : "");
        TextField precioField = new TextField(servicio != null ? String.valueOf(servicio.getPrecio()) : "");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Hora (HH:mm):"), 0, 1);
        grid.add(horaField, 1, 1);
        grid.add(new Label("Mascota:"), 0, 2);
        grid.add(mascotaComboBox, 1, 2);
        grid.add(new Label("Tipo de Servicio:"), 0, 3);
        grid.add(tipoServicioField, 1, 3);
        grid.add(new Label("Precio:"), 0, 4);
        grid.add(precioField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                try {
                    LocalDate fecha = fechaPicker.getValue();
                    LocalTime hora = LocalTime.parse(horaField.getText());
                    Mascota mascotaSeleccionada = mascotaComboBox.getValue();
                    String tipoServicio = tipoServicioField.getText();
                    String precioStr = precioField.getText().replaceAll("[^\\d.]", "");
                    double precio = Double.parseDouble(precioStr);

                    if (mascotaSeleccionada == null || tipoServicio.isEmpty() || fecha == null) {
                        mostrarAlerta("Todos los campos son obligatorios.");
                        return null;
                    }

                    ServicioPeluqueria nuevoServicio = servicio != null ? servicio : new ServicioPeluqueria();
                    nuevoServicio.setFecha(fecha);
                    nuevoServicio.setFechaHora(LocalDateTime.of(fecha, hora));
                    nuevoServicio.setMascota(mascotaSeleccionada);
                    nuevoServicio.setTipoServicio(tipoServicio);
                    nuevoServicio.setPrecio(precio);

                    if (nuevoServicio.getPeluqueria() == null) {
                        Usuario peluquero = obtenerPeluqueroActual();
                        if (peluquero == null) {
                            mostrarAlerta("No se ha definido el usuario actual.");
                            return null;
                        }
                        nuevoServicio.setPeluqueria(peluquero);
                    }

                    return nuevoServicio;
                } catch (Exception e) {
                    mostrarAlerta("Error: Verifique que la hora esté en formato HH:mm y el precio sea numérico.");
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
