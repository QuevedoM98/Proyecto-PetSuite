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
import org.quevedo.proyectofinal3ev.DAO.MascotaDAO; // Importar MascotaDAO

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

/**
 * Controlador para la vista del veterinario en la aplicación.
 * Gestiona la interfaz de usuario para que el personal veterinario pueda
 * visualizar y administrar las citas del día, el historial de visitas,
 * y la información de los pacientes (mascotas y sus dueños).
 */
public class VeterinariaController extends Controller implements Initializable {

    /**
     * Tabla FXML para mostrar las citas veterinarias del día.
     */
    @FXML
    private TableView<VisitaVeterinaria> tableCitas;
    /**
     * Columna de la tabla de citas para mostrar la hora de la cita.
     */
    @FXML
    private TableColumn<VisitaVeterinaria, String> colHora;
    /**
     * Columna de la tabla de citas para mostrar el nombre de la mascota.
     */
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMascota;
    /**
     * Columna de la tabla de citas para mostrar el nombre del dueño de la mascota.
     */
    @FXML
    private TableColumn<VisitaVeterinaria, String> colDueno;
    /**
     * Columna de la tabla de citas para mostrar el motivo de la visita.
     */
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMotivo;
    /**
     * Botón FXML para iniciar el proceso de agregar una nueva consulta.
     */
    @FXML
    private Button btnNuevaConsulta;
    /**
     * Botón FXML para ver la gestión de mascotas (pacientes).
     */
    @FXML
    private Button btnVerMascotas;
    /**
     * Botón FXML para acceder a la gestión de inventario (funcionalidad no implementada en este controlador).
     */
    @FXML
    private Button btnInventario;
    /**
     * Etiqueta FXML para mostrar el número de vacunas pendientes (funcionalidad no implementada).
     */
    @FXML
    private Label lblVacunasPendientes;
    /**
     * Icono FXML para representar las vacunas (funcionalidad no implementada).
     */
    @FXML
    private ImageView iconVacunas;

    /**
     * Tabla FXML para mostrar la lista de pacientes (mascotas).
     */
    @FXML
    private TableView<org.quevedo.proyectofinal3ev.model.Mascota> tablePacientes;
    /**
     * Columna de la tabla de pacientes para mostrar el ID de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, Integer> colIdPaciente;
    /**
     * Columna de la tabla de pacientes para mostrar el nombre de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colNombrePaciente;
    /**
     * Columna de la tabla de pacientes para mostrar la especie de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colEspeciePaciente;
    /**
     * Columna de la tabla de pacientes para mostrar la raza de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colRazaPaciente;
    /**
     * Columna de la tabla de pacientes para mostrar el nombre del dueño de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.Mascota, String> colDuenoPaciente;

    /**
     * Tabla FXML para mostrar el historial completo de citas veterinarias.
     */
    @FXML
    private TableView<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> tableHistorialCitas;
    /**
     * Columna de la tabla de historial de citas para mostrar la fecha y hora.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colFechaHistorial;
    /**
     * Columna de la tabla de historial de citas para mostrar el nombre de la mascota.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colMascotaHistorial;
    /**
     * Columna de la tabla de historial de citas para mostrar el motivo de la visita.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colMotivoHistorial;
    /**
     * Columna de la tabla de historial de citas para mostrar las observaciones de la visita.
     */
    @FXML
    private TableColumn<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria, String> colObservacionesHistorial;

    private ObservableList<org.quevedo.proyectofinal3ev.model.Mascota> pacientes;
    private ObservableList<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> historialCitas;
    private ObservableList<VisitaVeterinaria> citas;

    /**
     * El usuario {@link Usuario} actualmente logueado como veterinario.
     */
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las factorías de celdas para todas las columnas de las tablas y
     * establece listeners para la selección exclusiva entre las tablas de citas y historial.
     * La carga de datos inicial se realiza en {@link #onOpen(Object)} una vez que el usuario actual es conocido.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
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
        if (tableHistorialCitas != null && tableCitas != null) {
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
        }
    }

    /**
     * Carga las citas veterinarias programadas para el día actual y para el veterinario actual
     * en la tabla {@code tableCitas}.
     */
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

    /**
     * Carga todas las mascotas existentes en la base de datos en la tabla {@code tablePacientes}.
     */
    private void cargarPacientes() {
        if (tablePacientes != null) {
            List<org.quevedo.proyectofinal3ev.model.Mascota> listaPacientes = MascotaDAO.getAllMascotas();
            pacientes = FXCollections.observableArrayList(listaPacientes);
            tablePacientes.setItems(pacientes);
        }
    }

    /**
     * Carga el historial completo de visitas veterinarias para el veterinario actual
     * en la tabla {@code tableHistorialCitas}.
     */
    private void cargarHistorialCitas() {
        if (tableHistorialCitas != null) {
            try {
                Usuario veterinarioActual = obtenerVeterinarioActual();
                if (veterinarioActual == null) {
                    mostrarAlerta("No se ha definido el veterinario actual.");
                    return;
                }
                List<org.quevedo.proyectofinal3ev.model.VisitaVeterinaria> listaHistorial =
                        VisitaVeterinariaDAO.getAllVisitasPorVeterinaria(veterinarioActual.getId());
                historialCitas = FXCollections.observableArrayList(listaHistorial);
                tableHistorialCitas.setItems(historialCitas);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar todas las citas: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene la cita veterinaria actualmente seleccionada de las tablas de citas del día o historial de citas.
     *
     * @return La {@link VisitaVeterinaria} seleccionada, o {@code null} si no hay ninguna.
     */
    private VisitaVeterinaria getCitaSeleccionada() {
        VisitaVeterinaria citaSeleccionada = null;

        if (tableCitas != null && tableCitas.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableCitas.getSelectionModel().getSelectedItem();
        } else if (tableHistorialCitas != null && tableHistorialCitas.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableHistorialCitas.getSelectionModel().getSelectedItem();
        }

        return citaSeleccionada;
    }

    /**
     * Abre un diálogo para agregar una nueva cita veterinaria.
     * Al confirmar, inserta la nueva cita en la base de datos y refresca las tablas de citas del día
     * y el historial de citas.
     */
    @FXML
    private void agregarCita() {
        Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                VisitaVeterinariaDAO.insert(nuevaCita);
                cargarCitasDelDia(); // Recargar para asegurar la consistencia y orden
                cargarHistorialCitas(); // <-- Añadido: refrescar historial de citas
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    /**
     * Abre un diálogo para modificar la cita veterinaria seleccionada.
     * Al confirmar, actualiza la cita en la base de datos y refresca las tablas de citas del día
     * y el historial de citas. Muestra una alerta si no hay ninguna cita seleccionada.
     */
    @FXML
    private void modificarCita() {
        VisitaVeterinaria citaSeleccionada = getCitaSeleccionada();
        if (citaSeleccionada != null) {
            Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    VisitaVeterinariaDAO.update(citaModificada);
                    cargarCitasDelDia(); // Recargar para asegurar la consistencia y orden
                    cargarHistorialCitas();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la cita: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una cita para modificar.");
        }
    }

    /**
     * Elimina la cita veterinaria seleccionada de la base de datos y refresca las tablas de citas del día
     * y el historial de citas. Muestra una alerta si no hay ninguna cita seleccionada.
     */
    @FXML
    private void eliminarCita() {
        VisitaVeterinaria citaSeleccionada = getCitaSeleccionada();
        if (citaSeleccionada != null) {
            try {
                VisitaVeterinariaDAO.delete(citaSeleccionada.getId());
                cargarCitasDelDia(); // Recargar para asegurar la consistencia y orden
                cargarHistorialCitas();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la cita: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una cita para eliminar.");
        }
    }

    /**
     * Muestra una nueva ventana con una tabla grande que contiene el historial completo
     * de todas las citas veterinarias para el veterinario actual.
     */
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

    /**
     * Muestra un diálogo de selección para que el usuario elija una acción de gestión de pacientes (mascotas).
     * Las opciones son agregar, modificar o eliminar.
     */
    @FXML
    private void gestionarPacientes() {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Agregar Paciente",
                "Agregar Paciente", "Modificar Paciente", "Eliminar Paciente", "Agregar Dueño");
        dialog.setTitle("Gestión de Pacientes y Dueños");
        dialog.setHeaderText("Elige una acción");
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
                case "Agregar Dueño":
                    agregarDueno();
                    break;
            }
        });
    }

    /**
     * Cierra la aplicación.
     */
    @FXML
    private void salirAplicacion() {
        System.exit(0);
    }

    /**
     * Muestra una alerta de tipo ERROR con el mensaje proporcionado.
     *
     * @param mensaje El mensaje a mostrar en la alerta.
     */
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo para agregar o modificar una {@link VisitaVeterinaria}.
     * Este diálogo permite introducir la fecha, hora, motivo, observaciones y seleccionar una mascota.
     *
     * @param visita El objeto {@link VisitaVeterinaria} a modificar, o {@code null} si se va a agregar una nueva visita.
     * @return Un {@link Optional} que contiene la {@link VisitaVeterinaria} resultante si se guarda, o vacío si se cancela o hay errores de validación.
     */
    private Optional<VisitaVeterinaria> showVisitaVeterinariaDialog(VisitaVeterinaria visita) {
        Dialog<VisitaVeterinaria> dialog = new Dialog<>();
        dialog.setTitle(visita == null ? "Agregar Cita" : "Modificar Cita");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(visita != null ? visita.getFecha() : null);
        TextField horaField = new TextField(visita != null && visita.getFechaHora() != null
                ? visita.getFechaHora().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                : "");
        TextField motivoField = new TextField(visita != null ? visita.getMotivo() : "");
        TextArea observacionesArea = new TextArea(visita != null ? visita.getObservaciones() : "");

        // ComboBox para seleccionar mascota
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
                    mostrarAlerta("Error: Verifique que la hora esté en formato HH:mm y los campos sean válidos.");
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Método auxiliar para obtener el usuario actual logueado, que se espera sea un veterinario.
     *
     * @return El {@link Usuario} logueado.
     */
    private Usuario obtenerVeterinarioActual() {
        return usuarioActual;
    }

    /**
     * Método invocado cuando la vista se abre.
     * Establece el usuario actual si el input es una instancia de {@link Usuario}
     * y luego recarga los datos de las tablas de citas y pacientes.
     *
     * @param input El objeto de entrada, que se espera sea una instancia de {@link Usuario}.
     * @throws IOException Si ocurre un error de E/S.
     */
    @Override
    public void onOpen(Object input) throws IOException {
        // Espera que input sea un Usuario (el usuario logueado)
        if (input instanceof Usuario) {
            this.usuarioActual = (Usuario) input;
        }
        // Cargar datos solo cuando usuarioActual ya está definido
        if (usuarioActual != null) {
            cargarCitasDelDia();
            cargarPacientes();
            cargarHistorialCitas();
        }
    }

    /**
     * Método invocado cuando la vista se cierra.
     * No realiza ninguna acción específica en este controlador.
     *
     * @param output El objeto de salida (no utilizado).
     */
    @Override
    public void onClose(Object output) {
        // No hay acciones específicas al cerrar esta vista
    }

    /**
     * Abre un diálogo para agregar una nueva mascota (paciente).
     * Al confirmar, inserta la nueva mascota en la base de datos y refresca la tabla de pacientes.
     */
    @FXML
    private void agregarPaciente() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                MascotaDAO.insert(nuevaMascota);
                cargarPacientes(); // Refrescar la tabla de pacientes
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el paciente: " + e.getMessage());
            }
        });
    }

    /**
     * Abre un diálogo para modificar la mascota (paciente) seleccionada en la tabla de pacientes.
     * Al confirmar, actualiza la mascota en la base de datos y refresca la tabla de pacientes.
     * Muestra una alerta si no hay ninguna mascota seleccionada.
     */
    @FXML
    private void modificarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            Optional<Mascota> result = showMascotaDialog(mascotaSeleccionada);
            result.ifPresent(mascotaModificada -> {
                try {
                    MascotaDAO.update(mascotaModificada);
                    cargarPacientes(); // Refrescar la tabla de pacientes
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar el paciente: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para modificar.");
        }
    }

    /**
     * Elimina la mascota (paciente) seleccionada de la tabla de pacientes de la base de datos
     * y refresca la tabla. Muestra una alerta si no hay ninguna mascota seleccionada.
     */
    @FXML
    private void eliminarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarPacientes(); // Refrescar la tabla de pacientes
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar el paciente: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para eliminar.");
        }
    }

    /**
     * Muestra un diálogo para agregar o modificar una {@link Mascota}.
     * Este diálogo permite introducir el nombre, especie, raza y seleccionar un dueño existente.
     *
     * @param mascota El objeto {@link Mascota} a modificar, o {@code null} si se va a agregar una nueva mascota.
     * @return Un {@link Optional} que contiene la {@link Mascota} resultante si se guarda, o vacío si se cancela o hay errores de validación.
     */
    private Optional<Mascota> showMascotaDialog(Mascota mascota) {
        Dialog<Mascota> dialog = new Dialog<>();
        dialog.setTitle(mascota == null ? "Agregar Paciente" : "Modificar Paciente");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        TextField nombreField = new TextField(mascota != null ? mascota.getNombre() : "");
        TextField especieField = new TextField(mascota != null ? mascota.getEspecie() : "");
        TextField razaField = new TextField(mascota != null ? mascota.getRaza() : "");
        DatePicker fechaNacimientoPicker = new DatePicker(mascota != null ? mascota.getFechaNacimiento() : null); // Añadido campo de fecha de nacimiento

        // ComboBox para seleccionar dueño
        ComboBox<Usuario> duenioComboBox = new ComboBox<>();
        List<Usuario> usuarios = UsuarioDAO.findByType(Usuario.TipoUsuario.DUENO);
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
                LocalDate fechaNacimiento = fechaNacimientoPicker.getValue(); // Obtener fecha de nacimiento
                if (nombre.isEmpty() || especie.isEmpty() || raza.isEmpty() || duenio == null || fechaNacimiento == null) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Mascota nuevaMascota = mascota != null ? mascota : new Mascota();
                nuevaMascota.setNombre(nombre);
                nuevaMascota.setEspecie(especie);
                nuevaMascota.setRaza(raza);
                nuevaMascota.setFechaNacimiento(fechaNacimiento); // Establecer fecha de nacimiento
                nuevaMascota.setDuenioMascota(duenio);
                return nuevaMascota;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Abre un diálogo para agregar un nuevo dueño de mascota (usuario de tipo DUENO) a la base de datos.
     * Permite introducir el nombre de usuario y la contraseña.
     * Muestra alertas si los campos están vacíos o si ocurre un error en la inserción.
     */
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
        TextField emailField = new TextField(); // Añadido campo de email
        emailField.setPromptText("Email");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre de usuario:"), 0, 0);
        grid.add(nombreUsuarioField, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Email:"), 0, 2); // Añadido campo de email a la cuadrícula
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                String nombreUsuario = nombreUsuarioField.getText();
                String password = passwordField.getText();
                String email = emailField.getText(); // Obtener email
                if (nombreUsuario.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    mostrarAlerta("Todos los campos son obligatorios.");
                    return null;
                }
                Usuario nuevoDueno = new Usuario();
                nuevoDueno.setNombreUsuario(nombreUsuario);
                nuevoDueno.setPassword(password);
                nuevoDueno.setEmail(email); // Establecer email
                nuevoDueno.setTipoUsuario(Usuario.TipoUsuario.DUENO);
                return nuevoDueno;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(nuevoDueno -> {
            try {
                UsuarioDAO.insert(nuevoDueno);
                mostrarAlerta("Dueño añadido correctamente.");
                cargarPacientes(); // Refrescar la lista de dueños en el diálogo de mascotas
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el dueño: " + e.getMessage());
            }
        });
    }
}