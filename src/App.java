import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import utils.JPAUtil;
import utils.Seeder;
import views.ClienteView;   // o la vista inicial que prefieras

public class App extends Application {

    @Override
    public void start(Stage stage) {
        try {
            JPAUtil.ensureReady();           // valida BD
            Seeder.seed();                // crea datos de ejemplo nuevos
            ClienteView.mostrar(stage);      // abre tu pantalla inicial
        } catch (Exception ex) {
            ex.printStackTrace();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error al iniciar la aplicación");
            alert.setContentText("Detalle: " + ex.getMessage());
            alert.showAndWait();
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        JPAUtil.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
