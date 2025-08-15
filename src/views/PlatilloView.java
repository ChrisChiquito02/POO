package views;

import controllers.PlatilloController;
import controllers.PlatilloValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Platillo;

import java.util.List;

public class PlatilloView {

    private static final ObservableList<Platillo> platillos = FXCollections.observableArrayList();
    private static final PlatilloController controller = new PlatilloController();

    public static void mostrar(Stage stage) {
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");

        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Precio");

        Button btnAgregar = new Button("Agregar");
        Button btnModificar = new Button("Modificar");
        Button btnEliminar = new Button("Eliminar");

        TableView<Platillo> tabla = new TableView<>(platillos);

        TableColumn<Platillo, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Platillo, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        tabla.getColumns().addAll(colNombre, colPrecio);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                txtNombre.setText(sel.getNombre());
                txtPrecio.setText(String.valueOf(sel.getPrecio()));
            }
        });

        btnAgregar.setOnAction(e -> {
            double precio;
            try {
                precio = Double.parseDouble(txtPrecio.getText());
            } catch (NumberFormatException ex) {
                mostrarAlerta("Validación", "Precio inválido.");
                return;
            }
            Platillo p = new Platillo(txtNombre.getText().trim(), precio);
            String error = PlatilloValidator.validar(p);
            if (!error.isEmpty()) {
                mostrarAlerta("Validación", error);
                return;
            }
            if (controller.existePorNombre(p.getNombre())) {
                mostrarAlerta("Duplicado", "Ya existe un platillo con ese nombre.");
                return;
            }
            controller.crearPlatillo(p);
            recargarPlatillosDesdeBD();
            txtNombre.clear();
            txtPrecio.clear();
            tabla.getSelectionModel().clearSelection();
        });

        btnModificar.setOnAction(e -> {
            Platillo seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Sin selección", "Selecciona un platillo.");
                return;
            }
            double precio;
            try {
                precio = Double.parseDouble(txtPrecio.getText());
            } catch (NumberFormatException ex) {
                mostrarAlerta("Validación", "Precio inválido.");
                return;
            }
            Platillo p = new Platillo(txtNombre.getText().trim(), precio);
            p.setId(seleccionado.getId());
            String error = PlatilloValidator.validar(p);
            if (!error.isEmpty()) {
                mostrarAlerta("Validación", error);
                return;
            }
            if (controller.existePorNombre(p.getNombre(), p.getId())) {
                mostrarAlerta("Duplicado", "Ya existe un platillo con ese nombre.");
                return;
            }
            controller.actualizarPlatillo(p);
            recargarPlatillosDesdeBD();
            txtNombre.clear();
            txtPrecio.clear();
            tabla.getSelectionModel().clearSelection();
        });

        btnEliminar.setOnAction(e -> {
            Platillo seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado == null) {
                mostrarAlerta("Sin selección", "Selecciona un platillo.");
                return;
            }
            controller.eliminarPlatillos(List.of(seleccionado));
            recargarPlatillosDesdeBD();
            txtNombre.clear();
            txtPrecio.clear();
        });

        VBox form = new VBox(10, txtNombre, txtPrecio, new HBox(10, btnAgregar, btnModificar, btnEliminar));
        form.setPadding(new Insets(10));
        form.setAlignment(Pos.CENTER_LEFT);

        Button btnClientes = new Button("Clientes");
        btnClientes.setOnAction(e -> ClienteView.mostrar(stage));
        Button btnPlatillos = new Button("Platillos");
        btnPlatillos.setDisable(true);
        btnPlatillos.setOnAction(e -> PlatilloView.mostrar(stage));
        Button btnPedidos = new Button("Pedidos");
        btnPedidos.setOnAction(e -> PedidoView.mostrar(stage));
        Button btnSalir = new Button("Salir");
        btnSalir.setOnAction(e -> stage.close());

        HBox navBar = new HBox(10, btnClientes, btnPlatillos, btnPedidos, btnSalir);
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
root.getStyleClass().add("root");
        root.setTop(form);
        root.setCenter(tabla);
        root.setBottom(navBar);

        stage.setTitle("Platillos");
        {
                Scene scene = new Scene(root, 1000, 680);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(PlatilloView.class.getResource("/styles/neo.css").toExternalForm());
                stage.setScene(scene);
           }
        stage.show();

        recargarPlatillosDesdeBD();
    }

    private static void recargarPlatillosDesdeBD() {
        platillos.setAll(controller.obtenerTodos());
    }

    private static void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}