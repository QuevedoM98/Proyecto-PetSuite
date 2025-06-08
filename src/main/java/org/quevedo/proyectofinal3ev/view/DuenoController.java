package org.quevedo.proyectofinal3ev.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.quevedo.proyectofinal3ev.DAO.MascotaDAO;
import org.quevedo.proyectofinal3ev.DAO.UsuarioDAO;
import org.quevedo.proyectofinal3ev.DAO.ServicioPeluqueriaDAO;
import org.quevedo.proyectofinal3ev.DAO.VisitaVeterinariaDAO;
import org.quevedo.proyectofinal3ev.controller.Controller;
import org.quevedo.proyectofinal3ev.model.Mascota;
import org.quevedo.proyectofinal3ev.model.ServicioPeluqueria;
import org.quevedo.proyectofinal3ev.model.Usuario;
import org.quevedo.proyectofinal3ev.model.VisitaVeterinaria;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DuenoController extends Controller implements Initializable {

    @FXML
    private TableView<Mascota> tableMascotas;
    @FXML
    private TableView<VisitaVeterinaria> tableHistorialVisitas;
    @FXML
    private TableView<VisitaVeterinaria> tableProximasVisitas;
    @FXML
    private TableView<ServicioPeluqueria> tableFuturasCitasPeluqueria;
    @FXML
    private TableColumn<Mascota, Integer> colId;
    @FXML
    private TableColumn<Mascota, String> colNombre;
    @FXML
    private TableColumn<Mascota, String> colEspecie;
    @FXML
    private TableColumn<Mascota, String> colRaza;
    @FXML
    private TableColumn<Mascota, LocalDate> colFechaNacimiento;
    @FXML
    private TableColumn<VisitaVeterinaria, LocalDate> colFechaVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colMotivoVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colObservacionesVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, LocalDate> colFechaProximaVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colTipoProximaVisita;
    @FXML
    private TableColumn<VisitaVeterinaria, String> colDescripcionProximaVisita;
    @FXML
    private TableColumn<ServicioPeluqueria, LocalDate> colFechaCitaPeluqueria;
    @FXML
    private TableColumn<ServicioPeluqueria, String> colTipoCitaPeluqueria;
    @FXML
    private TableColumn<ServicioPeluqueria, Double> colDescripcionCitaPeluqueria;

    private ObservableList<Mascota> mascotas;
    private ObservableList<VisitaVeterinaria> historialVisitas;
    private ObservableList<VisitaVeterinaria> proximasVisitas;
    private ObservableList<ServicioPeluqueria> futurasCitasPeluqueria;
    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicializar listas observables
        mascotas = FXCollections.observableArrayList();
        historialVisitas = FXCollections.observableArrayList();
        proximasVisitas = FXCollections.observableArrayList();
        futurasCitasPeluqueria = FXCollections.observableArrayList();

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

        // Configurar columnas de la tabla de próximas visitas
        colFechaProximaVisita.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipoProximaVisita.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colDescripcionProximaVisita.setCellValueFactory(new PropertyValueFactory<>("observaciones"));

        // Configurar columnas de la tabla de futuras citas de peluquería
        colFechaCitaPeluqueria.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipoCitaPeluqueria.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colDescripcionCitaPeluqueria.setCellValueFactory(new PropertyValueFactory<>("precio"));

        // Asignar listas observables a las tablas
        tableMascotas.setItems(mascotas);
        tableHistorialVisitas.setItems(historialVisitas);
        tableProximasVisitas.setItems(proximasVisitas);
        tableFuturasCitasPeluqueria.setItems(futurasCitasPeluqueria);
    }


    @Override
    public void onOpen(Object input) throws IOException {
        if (input instanceof Usuario) {
            System.out.println("Usuario recibido: " + ((Usuario) input).getNombre());
            setUsuarioActual((Usuario) input);
        } else {
            System.out.println("No se recibió un usuario válido.");
        }
    }

    @Override
    public void onClose(Object output) {

    }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarDatos();
    }

    private void cargarDatos() {
        if (usuarioActual == null) {
            mostrarAlerta("Error: Usuario no válido. No se pueden cargar los datos.");
            return;
        }

        try {
            // Cargar mascotas del usuario actual
            mascotas.setAll(MascotaDAO.getMascotasByUsuarioId(usuarioActual.getId()));
            tableMascotas.setItems(mascotas);

            // Inicializar listas temporales para acumular datos
            List<VisitaVeterinaria> todasLasVisitas = new ArrayList<>();
            List<ServicioPeluqueria> todasLasCitasPeluqueria = new ArrayList<>();

            // Iterar sobre las mascotas y cargar sus datos
            for (Mascota mascota : mascotas) {
                todasLasVisitas.addAll(VisitaVeterinariaDAO.getVisitasByMascotaId(mascota.getId()));
                todasLasCitasPeluqueria.addAll(ServicioPeluqueriaDAO.getServiciosByMascotaId(mascota.getId()));
            }

            // Separar visitas veterinarias en historial y próximas
            LocalDate hoy = LocalDate.now();
            historialVisitas.setAll(todasLasVisitas.stream()
                    .filter(visita -> visita.getFecha() != null && visita.getFecha().isBefore(hoy))
                    .toList());
            proximasVisitas.setAll(todasLasVisitas.stream()
                    .filter(visita -> visita.getFecha() != null && !visita.getFecha().isBefore(hoy))
                    .toList());

            tableHistorialVisitas.setItems(historialVisitas);
            tableProximasVisitas.setItems(proximasVisitas);

            // Filtrar solo citas futuras de peluquería
            List<ServicioPeluqueria> futurasCitas = todasLasCitasPeluqueria.stream()
                    .filter(cita -> cita.getFecha() != null && !cita.getFecha().isBefore(hoy))
                    .toList();

            futurasCitasPeluqueria.setAll(futurasCitas);
            tableFuturasCitasPeluqueria.setItems(futurasCitasPeluqueria);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void agregarMascota() {
        if (usuarioActual == null || usuarioActual.getId() <= 0) {
            mostrarAlerta("El usuario actual no es válido. No se puede agregar la mascota.");
            return;
        }

        // Implementar correctamente el diálogo para obtener una nueva mascota
        Optional<Mascota> result = showMascotaDialog(null);
        result.ifPresent(nuevaMascota -> {
            try {
                nuevaMascota.setDuenioMascota(usuarioActual); // Asignar el dueño actual
                Mascota mascotaInsertada = MascotaDAO.insert(nuevaMascota);
                if (mascotaInsertada != null) {
                    mascotas.add(mascotaInsertada); // Agregar a la lista observable
                    tableMascotas.refresh(); // Refrescar la tabla
                } else {
                    mostrarAlerta("Error al insertar la mascota.");
                }
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la mascota: " + e.getMessage());
            }
        });
    }

    @FXML
    private void eliminarMascota() {
        Mascota mascotaSeleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            try {
                MascotaDAO.delete(mascotaSeleccionada.getId());
                mascotas.remove(mascotaSeleccionada);
                tableMascotas.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la mascota: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para eliminar.");
        }
    }

    @FXML
    private void modificarMascota() {
        Mascota mascotaSeleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada != null) {
            final Mascota mascotaFinal = mascotaSeleccionada;
            Optional<Mascota> result = showMascotaDialog(mascotaFinal);
            result.ifPresent(mascotaModificada -> {
                try {
                    MascotaDAO.update(mascotaModificada);
                    tableMascotas.refresh();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la mascota: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una mascota para modificar.");
        }
    }

    @FXML
    private void modificarCitaVeterinaria() {
        VisitaVeterinaria citaSeleccionada = tableProximasVisitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            final VisitaVeterinaria citaFinal = citaSeleccionada;
            Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(citaFinal);
            result.ifPresent(citaModificada -> {
                try {
                    VisitaVeterinariaDAO.update(citaModificada);
                    tableProximasVisitas.refresh();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la cita: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una cita para modificar.");
        }
    }

    @FXML
    private void eliminarCitaPeluqueria() {
        ServicioPeluqueria citaSeleccionada = tableFuturasCitasPeluqueria.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            try {
                ServicioPeluqueriaDAO.delete(citaSeleccionada.getId());
                futurasCitasPeluqueria.remove(citaSeleccionada);
                tableFuturasCitasPeluqueria.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la cita de peluquería: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una cita para eliminar.");
        }
    }

    @FXML
    private void salirAplicacion() {
        // Cierra la aplicación
        System.exit(0);
    }

    @FXML
    private void modificarCitaPeluqueria() {
        ServicioPeluqueria citaSeleccionada = tableFuturasCitasPeluqueria.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            final ServicioPeluqueria citaFinal = citaSeleccionada;
            Optional<ServicioPeluqueria> result = showCitaPeluqueriaDialog(citaFinal);
            result.ifPresent(citaModificada -> {
                try {
                    ServicioPeluqueriaDAO.update(citaModificada);
                    tableFuturasCitasPeluqueria.refresh();
                } catch (Exception e) {
                    mostrarAlerta("Error al modificar la cita de peluquería: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Por favor, seleccione una cita para modificar.");
        }
    }

    @FXML
    private void agregarCitaPeluqueria() {
        Mascota mascotaSeleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada == null) {
            mostrarAlerta("Debe seleccionar una mascota para agregar una cita de peluquería.");
            return;
        }

        Optional<ServicioPeluqueria> result = showCitaPeluqueriaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                // Asignar la mascota seleccionada al objeto ServicioPeluqueria
                nuevaCita.setMascota(mascotaSeleccionada);

                // Insertar la nueva cita en la base de datos
                ServicioPeluqueriaDAO.insert(nuevaCita);

                // Agregar la cita a la lista observable y refrescar la tabla
                futurasCitasPeluqueria.add(nuevaCita);
                tableFuturasCitasPeluqueria.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita de peluquería: " + e.getMessage());
            }
        });
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    private Optional<Mascota> showMascotaDialog(Mascota mascota) {
        Dialog<Mascota> dialog = new Dialog<>();
        dialog.setTitle(mascota == null ? "Agregar Mascota" : "Modificar Mascota");

        // Configurar botones
        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        // Crear campos de entrada
        TextField nombreField = new TextField(mascota != null ? mascota.getNombre() : "");
        TextField especieField = new TextField(mascota != null ? mascota.getEspecie() : "");
        TextField razaField = new TextField(mascota != null ? mascota.getRaza() : "");
        DatePicker fechaNacimientoPicker = new DatePicker(mascota != null ? mascota.getFechaNacimiento() : null);

        // Configurar diseño
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(nombreField, 1, 0);
        grid.add(new Label("Especie:"), 0, 1);
        grid.add(especieField, 1, 1);
        grid.add(new Label("Raza:"), 0, 2);
        grid.add(razaField, 1, 2);
        grid.add(new Label("Fecha de Nacimiento:"), 0, 3);
        grid.add(fechaNacimientoPicker, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                Mascota nuevaMascota = mascota != null ? mascota : new Mascota();
                nuevaMascota.setNombre(nombreField.getText());
                nuevaMascota.setEspecie(especieField.getText());
                nuevaMascota.setRaza(razaField.getText());
                nuevaMascota.setFechaNacimiento(fechaNacimientoPicker.getValue());
                return nuevaMascota;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<VisitaVeterinaria> showVisitaVeterinariaDialog(VisitaVeterinaria visita) {
        Dialog<VisitaVeterinaria> dialog = new Dialog<>();
        dialog.setTitle(visita == null ? "Agregar Visita Veterinaria" : "Modificar Visita Veterinaria");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(visita != null ? visita.getFecha() : null);
        TextField motivoField = new TextField(visita != null ? visita.getMotivo() : "");
        TextArea observacionesArea = new TextArea(visita != null ? visita.getObservaciones() : "");
        ComboBox<Usuario> veterinarioComboBox = new ComboBox<>();
        veterinarioComboBox.setItems(FXCollections.observableArrayList(UsuarioDAO.findByType(Usuario.TipoUsuario.VETERINARIA)));
        if (visita != null) veterinarioComboBox.setValue(visita.getVeterinaria());

        // Mostrar solo el nombre del veterinario
        veterinarioComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario != null ? usuario.getNombreUsuario() : "";
            }

            @Override
            public Usuario fromString(String string) {
                return veterinarioComboBox.getItems().stream()
                        .filter(usuario -> usuario.getNombreUsuario().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Motivo:"), 0, 1);
        grid.add(motivoField, 1, 1);
        grid.add(new Label("Observaciones:"), 0, 2);
        grid.add(observacionesArea, 1, 2);
        grid.add(new Label("Veterinario:"), 0, 3);
        grid.add(veterinarioComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                VisitaVeterinaria nuevaVisita = visita != null ? visita : new VisitaVeterinaria();
                nuevaVisita.setFecha(fechaPicker.getValue());
                nuevaVisita.setMotivo(motivoField.getText());
                nuevaVisita.setObservaciones(observacionesArea.getText());
                nuevaVisita.setVeterinaria(veterinarioComboBox.getValue());
                return nuevaVisita;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<ServicioPeluqueria> showCitaPeluqueriaDialog(ServicioPeluqueria cita) {
        Dialog<ServicioPeluqueria> dialog = new Dialog<>();
        dialog.setTitle(cita == null ? "Agregar Cita de Peluquería" : "Modificar Cita de Peluquería");

        ButtonType guardarButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(guardarButtonType, ButtonType.CANCEL);

        DatePicker fechaPicker = new DatePicker(cita != null ? cita.getFecha() : null);
        TextField tipoServicioField = new TextField(cita != null ? cita.getTipoServicio() : "");
        TextField precioField = new TextField(cita != null ? String.valueOf(cita.getPrecio()) : "");
        ComboBox<Usuario> peluqueroComboBox = new ComboBox<>();
        peluqueroComboBox.setItems(FXCollections.observableArrayList(UsuarioDAO.findByType(Usuario.TipoUsuario.PELUQUERIA)));
        if (cita != null) peluqueroComboBox.setValue(cita.getPeluqueria());

        // Mostrar solo el nombre del peluquero
        peluqueroComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario usuario) {
                return usuario != null ? usuario.getNombreUsuario() : "";
            }

            @Override
            public Usuario fromString(String string) {
                return peluqueroComboBox.getItems().stream()
                        .filter(usuario -> usuario.getNombreUsuario().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Fecha:"), 0, 0);
        grid.add(fechaPicker, 1, 0);
        grid.add(new Label("Tipo de Servicio:"), 0, 1);
        grid.add(tipoServicioField, 1, 1);
        grid.add(new Label("Precio:"), 0, 2);
        grid.add(precioField, 1, 2);
        grid.add(new Label("Peluquero:"), 0, 3);
        grid.add(peluqueroComboBox, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                ServicioPeluqueria nuevaCita = cita != null ? cita : new ServicioPeluqueria();
                nuevaCita.setFecha(fechaPicker.getValue());
                nuevaCita.setTipoServicio(tipoServicioField.getText());
                nuevaCita.setPrecio(Double.parseDouble(precioField.getText()));
                nuevaCita.setPeluqueria(peluqueroComboBox.getValue());
                return nuevaCita;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @FXML
    private void agregarCitaVeterinaria() {
        Mascota mascotaSeleccionada = tableMascotas.getSelectionModel().getSelectedItem();
        if (mascotaSeleccionada == null) {
            mostrarAlerta("Debe seleccionar una mascota para agregar una cita veterinaria.");
            return;
        }

        Optional<VisitaVeterinaria> result = showVisitaVeterinariaDialog(null);
        result.ifPresent(nuevaCita -> {
            try {
                // Asignar el id de la mascota seleccionada
                nuevaCita.setMascota(mascotaSeleccionada);

                // Insertar la nueva cita en la base de datos
                VisitaVeterinariaDAO.insert(nuevaCita);

                // Agregar la cita a la lista observable y refrescar la tabla
                proximasVisitas.add(nuevaCita);
                tableProximasVisitas.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al agregar la cita veterinaria: " + e.getMessage());
            }
        });
    }

    @FXML
    private void eliminarCitaVeterinaria() {
        VisitaVeterinaria citaSeleccionada = tableProximasVisitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            try {
                VisitaVeterinariaDAO.delete(citaSeleccionada.getId());
                proximasVisitas.remove(citaSeleccionada);
                tableProximasVisitas.refresh();
            } catch (Exception e) {
                mostrarAlerta("Error al eliminar la cita veterinaria: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Por favor, seleccione una cita para eliminar.");
        }
    }
}
