package com.example.h;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * El controlador {@code HelloController} gestiona la lógica de la interfaz principal,
 * donde se pueden agregar personas a una tabla, seleccionarlas y mostrar sus datos.
 */
public class HelloController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtEdad;
    @FXML
    private TextField txtFiltro;
    @FXML
    private TableView<Persona> tablaPersonas;
    @FXML
    private TableColumn<Persona, String> colNombre;
    @FXML
    private TableColumn<Persona, String> colApellidos;
    @FXML
    private TableColumn<Persona, Integer> colEdad;

    private final ObservableList<Persona> listaPersonas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colApellidos.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellidos()));
        colEdad.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getEdad()));

        tablaPersonas.setItems(listaPersonas);
        txtFiltro.textProperty().addListener((observable, oldValue, newValue) -> filtrarLista());

        cargarDatos(); // Cargar datos desde la base de datos
    }

    private void cargarDatos() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Persona")) {

            listaPersonas.clear(); // Limpiar la lista antes de cargar nuevos datos

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                String apellidos = resultSet.getString("apellidos");
                int edad = resultSet.getInt("edad");

                listaPersonas.add(new Persona(id, nombre, apellidos, edad));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    @FXML
    private void filtrarLista() {
        String filtro = txtFiltro.getText().toLowerCase();
        ObservableList<Persona> listaFiltrada = FXCollections.observableArrayList();

        for (Persona persona : listaPersonas) {
            if (persona.getNombre().toLowerCase().contains(filtro)) {
                listaFiltrada.add(persona);
            }
        }
        tablaPersonas.setItems(listaFiltrada);
    }

    @FXML
    private void agregarPersona() {
        mostrarVentanaModal("Agregar Persona", null);
    }

    @FXML
    private void modificarPersona() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "No hay ninguna persona seleccionada.");
            return;
        }
        mostrarVentanaModal("Modificar Persona", personaSeleccionada);
    }

    @FXML
    private void eliminarPersona() {
        Persona personaSeleccionada = tablaPersonas.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta("Error", "No hay ninguna persona seleccionada.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM Persona WHERE id = ?")) {
            statement.setInt(1, personaSeleccionada.getId());
            statement.executeUpdate();
            listaPersonas.remove(personaSeleccionada);
            mostrarAlerta("Éxito", "Persona eliminada correctamente.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar la persona: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarVentanaModal(String titulo, Persona persona) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/f/AgregarPersona.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            AgregarPersonaController agregarPersonaController = loader.getController();
            if (persona != null) {
                agregarPersonaController.cargarDatos(persona);
            }

            stage.showAndWait();

            Persona nuevaPersona = agregarPersonaController.getPersona();
            if (nuevaPersona != null) {
                if (persona == null) {
                    // Insertar en la base de datos
                    try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement("INSERT INTO Persona (nombre, apellidos, edad) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        statement.setString(1, nuevaPersona.getNombre());
                        statement.setString(2, nuevaPersona.getApellidos());
                        statement.setInt(3, nuevaPersona.getEdad());
                        statement.executeUpdate();

                        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                nuevaPersona.setId(generatedKeys.getInt(1));
                                listaPersonas.add(nuevaPersona);
                                mostrarAlerta("Éxito", "Persona agregada correctamente.");
                            }
                        }
                    } catch (SQLException e) {
                        mostrarAlerta("Error", "No se pudo agregar la persona: " + e.getMessage());
                    }
                } else {
                    // Actualizar en la base de datos
                    try (Connection connection = DatabaseConnection.getConnection();
                         PreparedStatement statement = connection.prepareStatement("UPDATE Persona SET nombre = ?, apellidos = ?, edad = ? WHERE id = ?")) {
                        statement.setString(1, nuevaPersona.getNombre());
                        statement.setString(2, nuevaPersona.getApellidos());
                        statement.setInt(3, nuevaPersona.getEdad());
                        statement.setInt(4, persona.getId());
                        statement.executeUpdate();

                        int index = listaPersonas.indexOf(persona);
                        listaPersonas.set(index, nuevaPersona);
                        mostrarAlerta("Éxito", "Persona modificada correctamente.");
                    } catch (SQLException e) {
                        mostrarAlerta("Error", "No se pudo modificar la persona: " + e.getMessage());
                    }
                }
            }


    }


}
