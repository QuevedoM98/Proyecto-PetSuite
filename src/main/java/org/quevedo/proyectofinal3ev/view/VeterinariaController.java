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
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    // --- NUEVOS FXML PARA PACIENTES ---
    @FXML
    private TableView<org.quevedo.proyectofinal3ev.model.Mascota> tablePacientes;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, Integer> colIdPaciente;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colNombrePaciente;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colEspeciePaciente;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colRazaPaciente;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colDuenoPaciente;

    // --- NUEVOS FXML PARA HISTORIAL DE CITAS ---
    @FXML
    private TableView<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> tableHistorialCitas;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colFechaHistorial;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colMascotaHistorial;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colMotivoHistorial;
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colObservacionesHistorial;

    private ObservableList<org.quevedo.proyectofinal3ev.model.Mascota> pacientes;
    private ObservableList<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> historialCitas;
    private ObservableList<VisitaVeterinaria> citas;

    // Nuevo campo para guardar el usuario logueado
    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar columnas de la tabla de citas del día
        colHora.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaHora() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFechaHora().toLocalTime().toString()
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        colMascota.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""
                )
        );
        colDueno.setCellValueFactory(cellData -> {
            if (cellData.getValue().getMascota() != null && cellData.getValue().getMascota().getDuenioMascota() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getMascota().getDuenioMascota().getNombreUsuario()
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        // Configurar columnas de pacientes
        if (colIdPaciente != null) colIdPaciente.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colNombrePaciente != null) colNombrePaciente.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        if (colEspeciePaciente != null) colEspeciePaciente.setCellValueFactory(new PropertyValueFactory<>("especie"));
        if (colRazaPaciente != null) colRazaPaciente.setCellValueFactory(new PropertyValueFactory<>("raza"));
        if (colDuenoPaciente != null) colDuenoPaciente.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDuenioMascota() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDuenioMascota().getNombreUsuario());
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });

        // Configurar columnas de historial de citas
        if (colFechaHistorial != null) colFechaHistorial.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFechaHora() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFechaHora().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                );
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });
        if (colMascotaHistorial != null) colMascotaHistorial.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""));
        if (colMotivoHistorial != null) colMotivoHistorial.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        if (colObservacionesHistorial != null) colObservacionesHistorial.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Listeners para deseleccionar la otra tabla al seleccionar en una
        tableHistorialCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tableCitas.getSelectionModel().clearSelection();
            }
        });

        tableCitas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tableHistorialCitas.getSelectionModel().clearSelection();
            }
        });

        // Elimina la carga de datos aquí para evitar errores de usuarioActual null
        // Cargar datos iniciales
        // cargarCitasDelDia();
        // cargarPacientes();
        // cargarHistorialCitas();
    }

    private void cargarCitasDelDia() {
        try {
            Usuario veterinarioActual = obtenerVeterinarioActual();
            if (veterinarioActual == null) {
                mostrarAlerta("No se ha definido el veterinario actual.");
                return;
            }
            List<VisitaVeterinaria> listaCitas = VisitaVeterinariaDAO.getVisitasDelDiaPorVeterinaria(veterinarioActual.getId());
            if (listaCitas != null && !listaCitas.isEmpty()) {
                citas = FXCollections.observableArrayList(listaCitas);
                tableCitas.setItems(citas);
            } else {
                citas = FXCollections.observableArrayList();
                tableCitas.setItems(citas);
            }
        } catch (Exception e) {
            mostrarAlerta("Error al cargar las citas del día: " + e.getMessage());
        }
    }

    private void cargarPacientes() {
        if (tablePacientes != null) {
            List<org.quevedo.proyectofinal3ev.model.Mascota> listaPacientes = org.quevedo.proyectofinal3ev.DAO.MascotaDAO.getAllMascotas();
            pacientes = FXCollections.observableArrayList(listaPacientes);
            tablePacientes.setItems(pacientes);
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
                List<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> listaHistorial =
                        org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO.getAllVisitasPorVeterinaria(veterinarioActual.getId());
                historialCitas = FXCollections.observableArrayList(listaHistorial);
                tableHistorialCitas.setItems(historialCitas);
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
        // Lógica para agregar una nueva cita
        Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                VisitaVeterinariaDAO.insert(nuevaCita);
                citas.add(nuevaCita); // Agregar a la lista observable
                tableCitas.refresh(); // Refrescar la tabla
                cargarHistorialCitas(); // <-- Añadido: refrescar historial de citas
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    @FXML
    private void modificarCita() {
        VisitaVeterinaria citaSeleccionada = getCitaSeleccionada();
        if (citaSeleccionada != null) {
            Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    VisitaVeterinariaDAO.update(citaModificada);
                    if (tableCitas.getItems().contains(citaSeleccionada)) {
                        tableCitas.refresh();
                    }
                    if (tableHistorialCitas.getItems().contains(citaSeleccionada)) {
                        tableHistorialCitas.refresh();
                    }
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
        VisitaVeterinaria citaSeleccionada = getCitaSeleccionada();
        if (citaSeleccionada != null) {
            try {
                VisitaVeterinariaDAO.delete(citaSeleccionada.getId());
                if (tableCitas.getItems().contains(citaSeleccionada)) {
                    tableCitas.getItems().remove(citaSeleccionada);
                    tableCitas.refresh();
                }
                if (tableHistorialCitas.getItems().contains(citaSeleccionada)) {
                    tableHistorialCitas.getItems().remove(citaSeleccionada);
                    tableHistorialCitas.refresh();
                }
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
            Usuario veterinarioActual = obtenerVeterinarioActual();
            if (veterinarioActual == null) {
                mostrarAlerta("No se ha definido el veterinario actual.");
                return;
            }
            List<VisitaVeterinaria> historial = VisitaVeterinariaDAO.getAllVisitasPorVeterinaria(veterinarioActual.getId());

            // Crear una nueva ventana con una tabla grande
            TableView<VisitaVeterinaria> tablaGrande = new TableView<>();
            tablaGrande.setPrefWidth(1200);
            tablaGrande.setPrefHeight(600);

            TableColumn<VisitaVeterinaria, String> colFecha = new TableColumn<>("Fecha");
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

            TableColumn<VisitaVeterinaria, String> colMascota = new TableColumn<>("Mascota");
            colMascota.setPrefWidth(200);
            colMascota.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getMascota() != null ? cellData.getValue().getMascota().getNombre() : ""
                )
            );

            TableColumn<VisitaVeterinaria, String> colDueno = new TableColumn<>("Dueño");
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

            TableColumn<VisitaVeterinaria, String> colMotivo = new TableColumn<>("Motivo");
            colMotivo.setPrefWidth(250);
            colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

            TableColumn<VisitaVeterinaria, String> colObservaciones = new TableColumn<>("Observaciones");
            colObservaciones.setPrefWidth(350);
            colObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

            tablaGrande.getColumns().addAll(colFecha, colMascota, colDueno, colMotivo, colObservaciones);
            tablaGrande.setItems(FXCollections.observableArrayList(historial));

            Stage stage = new Stage();
            stage.setTitle("Historial Completo de Citas");
            stage.setScene(new Scene(tablaGrande));
            stage.setWidth(1200);
            stage.setHeight(650);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta("Error al cargar el historial de citas: " + e.getMessage());
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

        // ComboBox para seleccionar mascota
        ComboBox<Mascota> mascotaComboBox = new ComboBox<>();
        List<Mascota> mascotasDisponibles = org.quevedo.proyectofinal3ev.DAO.MascotaDAO.getAllMascotas();
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

                    // Asignar veterinario si es null (para nuevas citas)
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

    // Método auxiliar para obtener el veterinario actual (ajusta según tu lógica de sesión)
    private Usuario obtenerVeterinarioActual() {
        return usuarioActual;
    }

    @Override
    public void onOpen(Object input) throws IOException {
        // Espera que input sea un Usuario (el usuario logueado)
        if (input instanceof Usuario) {
            this.usuarioActual = (Usuario) input;
        }
        // Cargar datos solo cuando usuarioActual ya está definido
        cargarCitasDelDia();
        cargarPacientes();
        cargarHistorialCitas();
    }

    @Override
    public void onClose(Object output) {

    }

    @FXML
    private void agregarPaciente() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.insert(nuevaMascota);
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
                    org.quevedo.proyectofinal3ev.DAO.MascotaDAO.update(mascotaModificada);
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
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarPacientes();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar el paciente: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para eliminar.");
        }
    }

    private Optional<Mascota> showMascotaDialog(Mascota mascota) {
        Dialog<Mascota> dialog = new Dialog<>();
        dialog.setTitle(mascota == null ? "Agregar Paciente" : "Modificar Paciente");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        TextField nombreField = new TextField(mascota != null ? mascota.getNombre() : "");
        TextField especieField = new TextField(mascota != null ? mascota.getEspecie() : "");
        TextField razaField = new TextField(mascota != null ? mascota.getRaza() : "");

        // ComboBox para seleccionar dueño
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
        grid.add(new Label("Dueño:"), 0, 3);
        grid.add(duenioComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombre = nombreField.getText();
                String especie = especieField.getText();
                String raza = razaField.getText();
                Usuario duenio = duenioComboBox.getValue();
                if (nombre.isEmpty() || especie.isEmpty() || raza.isEmpty() || duenio == null) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Mascota nuevaMascota = mascota != null ? mascota : new Mascota();
                nuevaMascota.setNombre(nombre);
                nuevaMascota.setEspecie(especie);
                nuevaMascota.setRaza(raza);
                nuevaMascota.setDuenioMascota(duenio);
                return nuevaMascota;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @FXML
    private void agregarDueno() {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Agregar Dueño");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        TextField nombreUsuarioField = new TextField();
        nombreUsuarioField.setPromptText("Nombre de usuario");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre de usuario:"), 0, 0);
        grid.add(nombreUsuarioField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombreUsuario = nombreUsuarioField.getText();
                String password = passwordField.getText();
                if (nombreUsuario.isEmpty() || password.isEmpty()) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Usuario nuevoDueno = new Usuario();
                nuevoDueno.setNombreUsuario(nombreUsuario);
                nuevoDueno.setPassword(password);
                nuevoDueno.setTipoUsuario(Usuario.TipoUsuario.DUENO);
                return nuevoDueno;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nuevoDueno -> {
            try {
                UsuarioDAO.insert(nuevoDueno);
                mostrarAlerta("Dueño añadido correctamente.");
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el dueño: " + e.getMessage());
            }
        });
    }
}