package views;

import controllers.PedidoController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Pedido;
import models.PedidoDetalle;

import java.time.format.DateTimeFormatter;

public class PedidoView {

    private final TableView<Pedido> tablaPedidos = new TableView<>();
    private final TableView<PedidoDetalle> tablaDetalles = new TableView<>();
    private final PedidoController controller = new PedidoController();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void mostrar(Stage owner) {
        new PedidoView().show(owner);
    }

    private void show(Stage owner) {
        BorderPane root = new BorderPane();
root.getStyleClass().add("root");
        root.setPadding(new Insets(10));
        Stage stage = new Stage();

        // ==========================
        // Tabla de Pedidos (encabezado)
        // ==========================
        TableColumn<Pedido, String> colId = new TableColumn<>("Pedido");
        colId.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() != null && cd.getValue().getId() != null
                        ? cd.getValue().getId().toString() : ""
        ));
        colId.setPrefWidth(80);

        TableColumn<Pedido, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() != null && cd.getValue().getCliente() != null
                        ? cd.getValue().getCliente().getNombre() : ""
        ));
        colCliente.setPrefWidth(220);

        TableColumn<Pedido, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() != null && cd.getValue().getFecha() != null
                        ? cd.getValue().getFecha().format(FMT) : ""
        ));
        colFecha.setPrefWidth(160);

        tablaPedidos.getColumns().setAll(colId, colCliente, colFecha);

        // ==========================
        // Tabla de Detalles
        // ==========================
        TableColumn<PedidoDetalle, String> colPlatillo = new TableColumn<>("Platillo");
        colPlatillo.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() != null && cd.getValue().getPlatillo() != null
                        ? cd.getValue().getPlatillo().getNombre() : ""
        ));
        colPlatillo.setPrefWidth(220);

        TableColumn<PedidoDetalle, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(
                cd.getValue() != null ? cd.getValue().getCantidad() : 0
        ));
        colCant.setPrefWidth(90);

        TableColumn<PedidoDetalle, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(
                cd.getValue() != null && cd.getValue().getPlatillo() != null
                        ? cd.getValue().getPlatillo().getPrecio() : 0.0
        ));
        colPrecio.setPrefWidth(100);

        TableColumn<PedidoDetalle, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(
                cd.getValue() != null ? cd.getValue().getSubtotal() : 0.0
        ));
        colSub.setPrefWidth(110);

        tablaDetalles.getColumns().setAll(colPlatillo, colCant, colPrecio, colSub);

        // ==========================
        // Datos
        // ==========================
        // Este método del controller DEBE usar JOIN FETCH (ya lo hicimos)
        refrescarPedidos();

        // al seleccionar encabezado → mostrar líneas
        tablaPedidos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            tablaDetalles.getItems().clear();
            if (sel != null && sel.getDetalles() != null) {
                tablaDetalles.getItems().setAll(sel.getDetalles());
            }
        });

        if (!tablaPedidos.getItems().isEmpty()) {
            tablaPedidos.getSelectionModel().selectFirst();
        }

        SplitPane split = new SplitPane(tablaPedidos, tablaDetalles);
        split.setDividerPositions(0.55);

        Button agregar = new Button("Agregar");
        agregar.setOnAction(e -> new PedidoForm().mostrar(stage, null, this::refrescarPedidos));

        Button editar = new Button("Editar");
        editar.setOnAction(e -> {
            Pedido sel = tablaPedidos.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Selecciona un pedido");
                return;
            }
            new PedidoForm().mostrar(stage, sel, this::refrescarPedidos);
        });

        Button eliminar = new Button("Eliminar");
        eliminar.setOnAction(e -> {
            Pedido sel = tablaPedidos.getSelectionModel().getSelectedItem();
            if (sel == null) {
                mostrarAlerta("Selecciona un pedido");
                return;
            }
            controller.eliminarPedidos(java.util.List.of(sel));
            refrescarPedidos();
        });

        Button cerrar = new Button("Cerrar");
        cerrar.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());
        HBox actions = new HBox(8, agregar, editar, eliminar, cerrar);
        actions.setPadding(new Insets(10, 0, 0, 0));

        root.setCenter(split);
        root.setBottom(actions);

        stage.setTitle("Pedidos");
        stage.initOwner(owner);
        {
                Scene scene = new Scene(root, 1000, 680);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(App.class.getResource("/styles/neo.css").toExternalForm());
                stage.setScene(scene);
           }
        stage.show();
    }

    private void refrescarPedidos() {
        tablaPedidos.getItems().setAll(controller.obtenerTodos());
        tablaDetalles.getItems().clear();
        if (!tablaPedidos.getItems().isEmpty()) {
            tablaPedidos.getSelectionModel().selectFirst();
        }
    }

    private void mostrarAlerta(String msg) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}