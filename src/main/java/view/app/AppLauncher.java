package view.app;

import dao.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Commercial;
import model.Enterprise;
import service.*;
import util.InitDB;
import view.controller.ajouter_commercial.ControllerAjouterCommercial;
import view.controller.liste_clients.ControllerListClient;
import view.controller.welcome.WelcomeController;

import java.io.IOException;

/**
 * Classe utilitaire pour lancer l'application JavaFX et gérer le chargement
 * des différentes vues selon l'état initial (Entreprise / Commercial).
 *
 * <p>
 * Cette classe est finale et ne peut pas être instanciée.
 * Elle centralise la création des DAO, services, contextes et controllers.
 * </p>
 */
public final class AppLauncher {

    /**
     * Démarre l'application.
     * <p>
     * Vérifie l'état initial de la base et de l'application pour décider
     * quelle vue charger : Welcome, Ajouter Commercial ou Dashboard principal.
     * </p>
     *
     * @param stage fenêtre principale JavaFX
     * @param clazz classe de référence pour charger les ressources FXML
     * @throws Exception en cas d'erreur d'initialisation ou de chargement des vues
     */
    public static void startApplication(Stage stage, Class<?> clazz) throws Exception {
        try {
            // Création/initialisation des tables en base de données
            InitDB.createTable();
        } catch (Exception e) {
            System.exit(1); // fermeture immédiate si impossible d'initialiser
            throw new RuntimeException(e);
        }

        // Initialisation des DAO et services
        final IEnterpriseDAO enterpriseDAO = new EnterpriseDAO();
        final IEnterpriseService enterpriseService = new EnterpriseService(enterpriseDAO);
        final ICommercialDAO commercialDAO = new CommercialDAO();
        final ICommercialService commercialService = new CommercialService(commercialDAO);

        AppContext appContext = new AppContext(commercialService, enterpriseService);

        Enterprise enterprise = appContext.getEnterprise();
        Commercial commercial = appContext.getCommercial();

        // Entreprise nll redirections vers la vue d'accueil'
        if(enterprise == null) {
            loadWelcomeView(stage, enterpriseService, clazz, appContext);
        // Commercial null redirection vers la vue d'ajout commercial'
        }else if (commercial == null) {
            loadAddCommercialPage(stage, clazz, appContext);
        // Commercial non null chargement du dashboard
        }else {
            loadMainDashboard(stage, clazz, appContext);
        }
    }

    /**
     * Charge la vue principale du dashboard (liste clients) et initialise le controller.
     *
     * @param stage      fenêtre principale JavaFX
     * @param clazz      classe de référence pour charger les ressources FXML
     * @param appContext contexte de l'application
     * @throws Exception en cas d'erreur de chargement du FXML ou des services
     */
    public static void loadMainDashboard(Stage stage, Class<?> clazz, AppContext appContext) throws Exception {
        final IClientDAO clientDAO = new ClientDAO();
        final ICommentaireDAO commentaireDAO = new CommentaireDAO();
        final IClientService clientService = new ClientService(clientDAO);
        final ICommentaireService commentaireService = new CommentaireService(commentaireDAO, clientService);

        FXMLLoader loader = new FXMLLoader(clazz.getResource("/view/liste_clients/liste_client.fxml"));
        Parent root = loader.load();

        ControllerListClient controller = loader.getController();
        controller.setClientService(clientService);
        controller.setCommentaireService(commentaireService);
        controller.setAppContext(appContext);
        controller.loadClients();
        controller.initData();

        Scene scene = new Scene(root, 1500, 1000);
        stage.setScene(scene);
        stage.setTitle("Liste des clients");
        stage.show();
    }

    /**
     * Charge la vue d'accueil pour la création d'une entreprise.
     *
     * @param stage             fenêtre principale JavaFX
     * @param enterpriseService service pour gérer les entreprises
     * @param clazz             classe de référence pour charger le FXML
     * @param context           contexte de l'application
     * @throws Exception en cas d'erreur de chargement du FXML
     */
    public static void loadWelcomeView(Stage stage, IEnterpriseService enterpriseService, Class<?> clazz, AppContext context) throws Exception {
        FXMLLoader loader = new FXMLLoader(clazz.getResource("/view/welcome/welcome.fxml"));
        Parent root = loader.load();

        WelcomeController controller = loader.getController();
        controller.setEnterpriseService(enterpriseService);
        controller.setAppContext(context);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Bienvenue sur le CRM");
        stage.show();
    }

    /**
     * Charge la vue pour ajouter un commercial.
     *
     * @param stage   fenêtre principale JavaFX
     * @param clazz   classe de référence pour charger le FXML
     * @param context contexte de l'application
     * @throws IOException en cas d'erreur de chargement du FXML
     */
    public static void loadAddCommercialPage(Stage stage, Class<?> clazz, AppContext context) throws IOException {

        final ICommercialDAO commercialDAO = new CommercialDAO();
        final ICommercialService commercialService = new CommercialService(commercialDAO);

        FXMLLoader loader = new FXMLLoader(clazz.getResource("/view/ajouter_commercial/ajouter_commercial.fxml"));

        Parent root = loader.load();

        ControllerAjouterCommercial controller = loader.getController();
        controller.setCommercialService(commercialService);
        controller.setAppContext(context);

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Ajouter un commercial");
        stage.show();
    }


    /**
     * Affiche une alerte de type WARNING indiquant que tous les champs sont obligatoires.
     */
    public static void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText("Tous les champs sont obligatoires");
        alert.showAndWait();
    }
}



