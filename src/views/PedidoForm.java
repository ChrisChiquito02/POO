package views;

import controllers.ClienteController;
import controllers.PedidoController;
import controllers.PlatilloController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Cliente;
import models.Pedido;
import models.PedidoDetalle;
import models.Platillo;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;


public class PedidoForm {

    private final PedidoController pedidoController = new PedidoController();
    private final ClienteController clienteController = new ClienteController();
    private final PlatilloController platilloController = new PlatilloController();

    public void mostrar(Stage owner, Pedido pedido, Runnable refrescar) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(pedido == null ? "Nuevo Pedido" : "Editar Pedido");

        ComboBox<Cliente> cmbCliente = new ComboBox<>();
        cmbCliente.getItems().setAll(clienteController.obtenerTodos());


        //mostrar nombre del cliente y correo
        cmbCliente.setCellFactory(cb -> new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre() + " — " + item.getCorreo());
            }
        });
        cmbCliente.setButtonCell(new ListCell<Cliente>() {
            @Override
            protected void updateItem(Cliente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override
            public String toString(Cliente c) {
                return c == null ? "" : c.getNombre();
            }
            @Override
            public Cliente fromString(String s) {
                if (s == null) return null;
                return cmbCliente.getItems().stream().filter(c -> s.equals(c.getNombre())).findFirst().orElse(null);
            }
        });


        ComboBox<Platillo> cmbPlatillo = new ComboBox<>();
        cmbPlatillo.getItems().setAll(platilloController.obtenerTodos());


        // mostrar nombre del platillo y precio
        cmbPlatillo.setCellFactory(cb -> new ListCell<Platillo>() {
            @Override
            protected void updateItem(Platillo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    String precio = String.format("$%.2f", item.getPrecio());
                    setText(item.getNombre() + " — " + precio);
                }
            }
        });
        cmbPlatillo.setButtonCell(new ListCell<Platillo>() {
            @Override
            protected void updateItem(Platillo item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNombre());
            }
        });
        cmbPlatillo.setConverter(new StringConverter<Platillo>() {
            @Override
            public String toString(Platillo p) {
                return p == null ? "" : p.getNombre();
            }
            @Override
            public Platillo fromString(String s) {
                if (s == null) return null;
                return cmbPlatillo.getItems().stream().filter(p -> s.equals(p.getNombre())).findFirst().orElse(null);
            }
        });

        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Cantidad");
        Button btnAgregarDetalle = new Button("Agregar platillo");

        ObservableList<PedidoDetalle> detalles = FXCollections.observableArrayList();
        TableView<PedidoDetalle> tablaDetalles = new TableView<>(detalles);
        TableColumn<PedidoDetalle, String> colPlatillo = new TableColumn<>("Platillo");
        colPlatillo.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(
                cd.getValue().getPlatillo() != null ? cd.getValue().getPlatillo().getNombre() : ""));
        TableColumn<PedidoDetalle, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        TableColumn<PedidoDetalle, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyObjectWrapper<>(cd.getValue().getSubtotal()));
        tablaDetalles.getColumns().addAll(colPlatillo, colCant, colSub);

        btnAgregarDetalle.setOnAction(e -> {
            Platillo pl = cmbPlatillo.getValue();
            int cant;
            try {
                cant = Integer.parseInt(txtCantidad.getText());
            } catch (NumberFormatException ex) {
                mostrarAlerta("Cantidad inválida");
                return;
            }
            if (pl == null || cant <= 0) {
                mostrarAlerta("Selecciona un platillo y cantidad > 0");
                return;
            }
            PedidoDetalle det = new PedidoDetalle();
            det.setPlatillo(pl);
            det.setCantidad(cant);
            detalles.add(det);
            txtCantidad.clear();
        });

        Button btnQuitarDetalle = new Button("Quitar");
        btnQuitarDetalle.setOnAction(e -> {
            PedidoDetalle sel = tablaDetalles.getSelectionModel().getSelectedItem();
            if (sel != null) detalles.remove(sel);
        });

        Button btnGuardar = new Button("Guardar");
        Button btnCancelar = new Button("Cancelar");

        if (pedido != null) {
            cmbCliente.setValue(pedido.getCliente());
            for (PedidoDetalle d : pedido.getDetalles()) {
                PedidoDetalle nd = new PedidoDetalle();
                nd.setPlatillo(d.getPlatillo());
                nd.setCantidad(d.getCantidad());
                detalles.add(nd);
            }
        }

        btnGuardar.setOnAction(e -> {
            Cliente cl = cmbCliente.getValue();
            if (cl == null || detalles.isEmpty()) {
                mostrarAlerta("Selecciona cliente y al menos un platillo");
                return;
            }
            Pedido p = (pedido == null) ? new Pedido() : pedido;
            p.setCliente(cl);
            p.getDetalles().clear();
            for (PedidoDetalle d : detalles) {
                d.setPedido(p);
            }
            p.getDetalles().addAll(detalles);
            if (p.getId() == null) {
                pedidoController.crearPedido(p);
            } else {
                pedidoController.actualizarPedido(p);
            }
            if (refrescar != null) refrescar.run();
            stage.close();
        });

        btnCancelar.setOnAction(e -> stage.close());

        HBox formDetalle = new HBox(10, cmbPlatillo, txtCantidad, btnAgregarDetalle, btnQuitarDetalle);
        formDetalle.setAlignment(Pos.CENTER_LEFT);
        VBox top = new VBox(10, cmbCliente, formDetalle);
        top.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
root.getStyleClass().add("root");
        root.setTop(top);
        root.setCenter(tablaDetalles);
        root.setBottom(new HBox(10, btnGuardar, btnCancelar));
        BorderPane.setMargin(root.getBottom(), new Insets(10));
        ((HBox) root.getBottom()).setAlignment(Pos.CENTER);

        {
                Scene scene = new Scene(root, 1000, 680);
                scene.getStylesheets().clear();
                scene.getStylesheets().add(PedidoForm.class.getResource("/styles/neo.css").toExternalForm());
                stage.setScene(scene);
           }
        stage.showAndWait();
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}