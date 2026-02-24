import javafx.application.Application;
import javafx.stage.Stage;
import view.app.AppLauncher;

/**
 * Classe principale de l'application JavaFX.
 *
 * <p>
 * Démarre l'application en appelant {@link AppLauncher} pour initialiser les vues
 * et la logique de navigation.
 * </p>
 */
public class MainApp extends Application {

    public MainApp(){}

    @Override
    public void start(Stage stage) throws Exception {
        // Lance l'application via AppLauncher dans view/app/AppLauncher.java
        AppLauncher.startApplication(stage, getClass());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
