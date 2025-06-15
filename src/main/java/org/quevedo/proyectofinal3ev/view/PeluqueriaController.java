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

/**
 * Controlador para la vista del peluquero en la aplicación.
 * Gestiona la interfaz de usuario para que el personal de peluquería pueda ver y gestionar
 * las citas del día, el historial de servicios, y la información de los clientes (mascotas y dueños).
 */
public class PeluqueriaController extends Controller implements Initializable {
    @FXML
    private TableView<Mascota> tablePacientes; // Esta tabla parece ser un duplicado o no se usa como "pacientes" en el contexto de peluquería, sino más bien como "clientes" (mascotas). Se usa "tableClientes" para la gestión.
    @FXML
    private TableView<VisitaVeterinaria> tableCitas; // Esta tabla no se usa en este controlador, parece ser de veterinaria.
    @FXML
    private TableView<VisitaVeterinaria> tableHistorialCitas; // Esta tabla no se usa en este controlador, parece ser de veterinaria.
    @FXML
    private TableView<ServicioPeluqueria> tableCitasDia;
    @FXML
    private TableView<ServicioPeluqueria> tableHistorialServicios;
    @FXML
    private TableView<Mascota> tableClientes;

    @FXML
    private TableColumn<ServicioPeluqueria, String> colHoraCita;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colMascotaCita;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colDuenoCita;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colServicioCita;

    @FXML
    private TableColumn<ServicioPeluqueria, String> colFechaServicio;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colMascotaServicio;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colTipoServicio;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colPrecioServicio;

    @FXML
    private TableColumn<Mascota, String> colNombreCliente;
    @FXML
    private TableColumn<Mascota, String> colEspecieCliente;
    @FXML
    private TableColumn<Mascota, String> colRazaCliente;
    @FXML
    private TableColumn<Mascota, String> colDuenoCliente;

    private Usuario usuarioActual; // Podría ser usado como un usuario genérico.
    private Usuario peluqueroActual; // El usuario actualmente logueado como peluquero.
    private List<Mascota> pacientes; // Lista de mascotas, se superpone con tableClientes
    private List<VisitaVeterinaria> citas; // No usada en este contexto de peluquería.
    private List<VisitaVeterinaria> historialCitas; // No usada en este contexto de peluquería.

    /**
     * Carga todas las mascotas (consideradas "pacientes" en un contexto más amplio o "clientes" para peluquería)
     * de la base de datos en la tabla {@code tablePacientes}.
     */
    private void cargarPacientes() {
        if (tablePacientes != null) {
            List<Mascota> listaPacientes = MascotaDAO.getAllMascotas();
            pacientes = FXCollections.observableArrayList(listaPacientes);
            tablePacientes.setItems((ObservableList<Mascota>) pacientes);
        }
    }

    /**
     * Carga el historial de visitas veterinarias en la tabla {@code tableHistorialCitas}.
     * Este método parece estar fuera de lugar en un controlador de peluquería,
     * ya que opera con {@link VisitaVeterinaria} y depende de un "veterinario actual".
     *
     * @deprecated Este método no es coherente con el rol de un controlador de peluquería.
     */
    @Deprecated
    private void cargarHistorialCitas() {
        if (tableHistorialCitas != null) {
            try {
                Usuario veterinarioActual = obtenerVeterinarioActual(); // Este método debería obtener el peluquero actual.
                if (veterinarioActual == null) {
                    mostrarAlerta("No se ha definido el usuario actual (peluquero).");
                    return;
                }
                // La siguiente línea debería usar ServicioPeluqueriaDAO, no VisitaVeterinariaDAO
                // List<VisitaVeterinaria> listaHistorial = VisitaVeterinariaDAO.getAllVisitasPorVeterinaria(veterinarioActual.getId());
                // historialCitas = FXCollections.observableArrayList(listaHistorial);
                // tableHistorialCitas.setItems((ObservableList<VisitaVeterinaria>) historialCitas);
            } catch (Exception e) {
                mostrarAlerta("Error al cargar todas las citas: " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene la cita veterinaria seleccionada de las tablas de citas o historial de citas.
     * Este método parece estar fuera de lugar en un controlador de peluquería.
     *
     * @return La {@link VisitaVeterinaria} seleccionada, o {@code null} si no hay ninguna.
     * @deprecated Este método no es coherente con el rol de un controlador de peluquería.
     */
    @Deprecated
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
     * Abre un diálogo para agregar una nueva cita de peluquería.
     * Al confirmar, inserta la nueva cita en la base de datos y refresca las tablas de citas del día
     * y historial de servicios.
     */
    @FXML
    private void agregarCita() {
        Optional<ServicioPeluqueria> result = showCitaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                // Asignar el peluquero actual a la nueva cita
                if (peluqueroActual == null) {
                    mostrarAlerta("Error: Peluquero actual no definido. No se puede agregar la cita.");
                    return;
                }
                nuevaCita.setPeluqueria(peluqueroActual);
                ServicioPeluqueriaDAO.insert(nuevaCita);
                cargarCitasDelDia();
                cargarHistorialServicios();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita: " + e.getMessage());
            }
        });
    }

    /**
     * Obtiene la cita de peluquería seleccionada de las tablas de citas del día o historial de servicios.
     *
     * @return El {@link ServicioPeluqueria} seleccionado, o {@code null} si no hay ninguno.
     */
    private ServicioPeluqueria getCitaPeluqueriaSeleccionada() {
        ServicioPeluqueria citaSeleccionada = null;
        if (tableCitasDia != null && tableCitasDia.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableCitasDia.getSelectionModel().getSelectedItem();
        } else if (tableHistorialServicios != null && tableHistorialServicios.getSelectionModel().getSelectedItem() != null) {
            citaSeleccionada = tableHistorialServicios.getSelectionModel().getSelectedItem();
        }
        return citaSeleccionada;
    }

    /**
     * Abre un diálogo para modificar la cita de peluquería seleccionada.
     * Al confirmar, actualiza la cita en la base de datos y refresca las tablas de citas del día
     * y historial de servicios. Muestra una alerta si no hay ninguna cita seleccionada.
     */
    @FXML
    private void modificarCita() {
        ServicioPeluqueria citaSeleccionada = getCitaPeluqueriaSeleccionada();
        if (citaSeleccionada != null) {
            Optional<ServicioPeluqueria> result = showCitaDialog(citaSeleccionada);
            result.ifPresent(citaModificada -> {
                try {
                    // Mantener el peluquero original si no se cambia
                    if (citaModificada.getPeluqueria() == null && peluqueroActual != null) {
                        citaModificada.setPeluqueria(peluqueroActual);
                    }
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

    /**
     * Elimina la cita de peluquería seleccionada de la base de datos y refresca las tablas de citas del día
     * y historial de servicios. Muestra una alerta si no hay ninguna cita seleccionada.
     */
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

    /**
     * Muestra una nueva ventana con una tabla grande que contiene el historial completo
     * de todos los servicios de peluquería registrados en la base de datos.
     */
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

    /**
     * Muestra un diálogo de selección para que el usuario elija una acción de gestión de "pacientes"
     * (mascotas). Las opciones son agregar, modificar o eliminar.
     * Este método se relaciona con {@code tablePacientes}, que podría ser redundante con {@code tableClientes}.
     */
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

    /**
     * Muestra un diálogo de selección para que el usuario elija una acción de gestión de mascotas
     * (se refiere a las mascotas en {@code tableClientes}). Las opciones son agregar, modificar o eliminar.
     */
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
     * Este método parece estar fuera de lugar en un controlador de peluquería,
     * ya que gestiona visitas veterinarias, no servicios de peluquería.
     *
     * @param visita El objeto {@link VisitaVeterinaria} a modificar, o {@code null} si se va a agregar una nueva visita.
     * @return Un {@link Optional} que contiene la {@link VisitaVeterinaria} resultante si se guarda, o vacío si se cancela.
     * @deprecated Este método no es coherente con el rol de un controlador de peluquería.
     */
    @Deprecated
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
                        Usuario veterinarioActual = obtenerVeterinarioActual(); // Debería ser peluqueroActual aquí.
                        if (veterinarioActual == null) {
                            mostrarAlerta("No se ha definido el peluquero actual.");
                            return null;
                        }
                        nuevaVisita.setVeterinaria(veterinarioActual); // Asignando peluquero a un campo de veterinario.
                    }

                    return nuevaVisita;
                } catch (Exception e) {
                    mostrarAlerta("Error: Verifique que la hora esté en formato HH:mm y todos los campos sean válidos.");
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    /**
     * Obtiene el usuario actual logueado. Este método se llama "obtenerVeterinarioActual"
     * pero debería obtener el {@link Usuario} del tipo PELUQUERIA que está logueado.
     *
     * @return El {@link Usuario} actual.
     */
    private Usuario obtenerVeterinarioActual() {
        return usuarioActual; // Retorna el usuario genérico, se debería usar 'peluqueroActual'
    }

    /**
     * Obtiene el peluquero actual logueado.
     *
     * @return El {@link Usuario} del tipo PELUQUERIA que está logueado.
     */
    private Usuario obtenerPeluqueroActual() {
        return peluqueroActual;
    }

    /**
     * Inicializa el controlador después de que su elemento raíz ha sido completamente procesado.
     * Configura las factorías de celdas para todas las columnas de las tablas,
     * establece listeners para la selección exclusiva entre tablas de citas,
     * y carga los datos iniciales de citas y mascotas.
     *
     * @param url La ubicación utilizada para resolver rutas relativas para el objeto raíz, o null si la ubicación no se conoce.
     * @param resourceBundle Los recursos utilizados para localizar el objeto raíz, o null si el objeto raíz no fue localizado.
     */
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

        // Cargar datos iniciales (se cargarán de nuevo en onOpen si hay un usuario logueado)
        cargarCitasDelDia();
        cargarHistorialServicios();
        cargarMascotas();
    }

    /**
     * Método invocado cuando la vista se abre.
     * Establece el peluquero actual si el input es una instancia de {@link Usuario}
     * y luego recarga los datos de las tablas.
     *
     * @param input El objeto de entrada, que se espera sea una instancia de {@link Usuario}.
     * @throws IOException Si ocurre un error de E/S.
     */
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

    /**
     * Método invocado cuando la vista se cierra.
     * No realiza ninguna acción específica en este controlador.
     *
     * @param output El objeto de salida (no utilizado).
     */
    @Override
    public void onClose(Object output) {

    }

    /**
     * Carga las citas de peluquería programadas para el día actual y para el peluquero actual
     * en la tabla {@code tableCitasDia}.
     */
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

    /**
     * Carga el historial de servicios de peluquería realizados por el peluquero actual
     * en la tabla {@code tableHistorialServicios}.
     */
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

    /**
     * Carga todas las mascotas existentes en la base de datos en la tabla {@code tableClientes}.
     */
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

    /**
     * Abre un diálogo para agregar un nuevo paciente (mascota).
     * Este método parece un duplicado de {@code agregarMascota}.
     * Al confirmar, inserta la nueva mascota en la base de datos y refresca la tabla de pacientes.
     */
    @FXML
    private void agregarPaciente() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                MascotaDAO.insert(nuevaMascota);
                cargarPacientes(); // Refresca tablePacientes
                cargarMascotas(); // Asegura que tableClientes también se actualice.
            } catch (Exception e) {
                mostrarAlerta("Error al agregar el paciente: " + e.getMessage());
            }
        });
    }

    /**
     * Abre un diálogo para modificar la mascota (paciente) seleccionada en {@code tablePacientes}.
     * Al confirmar, actualiza la mascota en la base de datos y refresca la tabla de pacientes.
     */
    @FXML
    private void modificarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            Optional<Mascota> result = showMascotaDialog(mascotaSeleccionada);
            result.ifPresent(mascotaModificada -> {
                try {
                    MascotaDAO.update(mascotaModificada);
                    cargarPacientes(); // Refresca tablePacientes
                    cargarMascotas(); // Asegura que tableClientes también se actualice.
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar el paciente: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para modificar.");
        }
    }

    /**
     * Elimina la mascota (paciente) seleccionada de {@code tablePacientes} de la base de datos
     * y refresca la tabla.
     */
    @FXML
    private void eliminarPaciente() {
        Mascota mascotaSeleccionada = tablePacientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarPacientes(); // Refresca tablePacientes
                cargarMascotas(); // Asegura que tableClientes también se actualice.
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar el paciente: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione un paciente para eliminar.");
        }
    }

    /**
     * Abre un diálogo para agregar una nueva mascota.
     * Este método es llamado desde "gestionarMascotas" y actualiza {@code tableClientes}.
     */
    private void agregarMascota() {
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.insert(nuevaMascota);
                cargarMascotas(); // Refresca tableClientes
                cargarPacientes(); // Asegura que tablePacientes también se actualice.
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la mascota: " + e.getMessage());
            }
        });
    }

    /**
     * Abre un diálogo para modificar la mascota seleccionada en {@code tableClientes}.
     * Al confirmar, actualiza la mascota en la base de datos y refresca la tabla.
     */
    private void modificarMascota() {
        Mascota mascotaSeleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            Optional<Mascota> result = showMascotaDialog(mascotaSeleccionada);
            result.ifPresent(mascotaModificada -> {
                try {
                    org.quevedo.proyectofinal3ev.DAO.MascotaDAO.update(mascotaModificada);
                    cargarMascotas(); // Refresca tableClientes
                    cargarPacientes(); // Asegura que tablePacientes también se actualice.
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la mascota: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para modificar.");
        }
    }

    /**
     * Elimina la mascota seleccionada de {@code tableClientes} de la base de datos
     * y refresca la tabla.
     */
    private void eliminarMascota() {
        Mascota mascotaSeleccionada = tableClientes.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                org.quevedo.proyectofinal3ev.DAO.MascotaDAO.delete(mascotaSeleccionada.getId());
                cargarMascotas(); // Refresca tableClientes
                cargarPacientes(); // Asegura que tablePacientes también se actualice.
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la mascota: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para eliminar.");
        }
    }

    /**
     * Abre un diálogo para agregar un nuevo dueño de mascota (usuario de tipo DUENO) a la base de datos.
     * Muestra alertas si los campos están vacíos o si ocurre un error en la inserción.
     */
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

    /**
     * Muestra una alerta de tipo INFORMACIÓN con el mensaje proporcionado.
     *
     * @param mensaje El mensaje a mostrar en la alerta.
     */
    private void mostrarInfo(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un diálogo para agregar o modificar una {@link Mascota}.
     * Este diálogo permite introducir el nombre, especie, raza, fecha de nacimiento y seleccionar un dueño.
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

    /**
     * Muestra un diálogo para agregar o modificar un {@link ServicioPeluqueria}.
     * Permite introducir la fecha, hora, tipo de servicio, precio y seleccionar una mascota.
     * Al confirmar, retorna un objeto {@link ServicioPeluqueria} con los datos ingresados.
     *
     * @param servicio El objeto {@link ServicioPeluqueria} a modificar, o {@code null} si se va a agregar un nuevo servicio.
     * @return Un {@link Optional} que contiene el {@link ServicioPeluqueria} resultante si se guarda, o vacío si se cancela o hay errores de validación.
     */
    private Optional<ServicioPeluqueria> showCitaDialog(ServicioPeluqueria servicio) {
        Dialog<ServicioPeluqueria> dialog = new Dialog<>();
        dialog.setTitle(servicio == null ? "Agregar Cita de Peluquería" : "Modificar Cita de Peluquería");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(servicio != null ? servicio.getFecha() : null);
        TextField horaField = new TextField(servicio != null && servicio.getFechaHora() != null
                ? servicio.getFechaHora().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                : "");
        TextField tipoServicioField = new TextField(servicio != null ? servicio.getTipoServicio() : "");
        TextField precioField = new TextField(servicio != null ? String.valueOf(servicio.getPrecio()) : "");

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
                    double precio = Double.parseDouble(precioField.getText());

                    if (fecha == null || hora == null || mascotaSeleccionada == null || tipoServicio.isEmpty()) {
                        mostrarAlerta("Todos los campos (excepto precio, que debe ser un número) son obligatorios.");
                        return null;
                    }

                    ServicioPeluqueria nuevoServicio = servicio != null ? servicio : new ServicioPeluqueria();
                    nuevoServicio.setFecha(fecha);
                    nuevoServicio.setFechaHora(LocalDateTime.of(fecha, hora));
                    nuevoServicio.setMascota(mascotaSeleccionada);
                    nuevoServicio.setTipoServicio(tipoServicio);
                    nuevoServicio.setPrecio(precio);

                    if (nuevoServicio.getPeluqueria() == null) {
                        if (peluqueroActual == null) {
                            mostrarAlerta("Error: Peluquero actual no definido. No se puede guardar la cita.");
                            return null;
                        }
                        nuevoServicio.setPeluqueria(peluqueroActual);
                    }

                    return nuevoServicio;
                } catch (NumberFormatException e) {
                    mostrarAlerta("Error: El precio debe ser un número válido.");
                    return null;
                } catch (Exception e) {
                    mostrarAlerta("Error: Verifique que la hora esté en formato HH:mm y todos los campos sean válidos.");
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }
}