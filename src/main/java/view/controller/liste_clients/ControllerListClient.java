package view.controller.liste_clients;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Client;
import service.IClientService;
import service.ICommentaireService;
import view.controller.ajouter_client.ControllerAjouterClient;
import view.controller.detail_client.ControllerDetailClient;
import view.app.AppContext;
import view.controller.menu.ParameterController;
import view.helper.UiFxUtils;

import java.io.IOException;
import java.util.List;

/**
 * Contrôleur pour la vue de la liste des clients.
 *
 * <p>
 * Cette classe gère l'affichage et les interactions avec la table des clients.
 * Elle permet :
 * <ul>
 *     <li>Le filtrage des clients via la barre de recherche.</li>
 *     <li>L'affichage des détails d'un client.</li>
 *     <li>L'ajout ou la suppression de clients.</li>
 *     <li>L'accès aux paramètres via la fenêtre de settings.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Dépendances :
 * <ul>
 *     <li>{@link AppContext} pour récupérer l'utilisateur courant</li>
 *     <li>{@link IClientService} pour les opérations CRUD sur les clients</li>
 *     <li>{@link ICommentaireService} pour gérer les commentaires des clients</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ControllerListClient {


    // =========================
    // Services & Contexte
    // =========================

    @Setter private AppContext appContext;
    @Setter private IClientService clientService;
    @Setter private ICommentaireService commentaireService;
    @Getter private final ObservableList<Client> clients = FXCollections.observableArrayList();

    /** Liste filtrée utilisée pour la recherche dynamique */
    private FilteredList<Client> filteredClients;

    // =========================
    // FXML Elements
    // =========================
    @FXML private Label lblTitre;
    @FXML private TextField tfRecherche;
    @FXML private Button btnAjouterClient;
    @FXML private Button btnSettings;
    @FXML private ImageView imgSettings;
    @FXML private TableView<Client> tableClients;
    @FXML private TableColumn<Client, String> colSociete;
    @FXML private TableColumn<Client, String> colNom;
    @FXML private TableColumn<Client, String> colPrenom;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colTelephone;
    @FXML private TableColumn<Client, String> colAdresse;
    @FXML private TableColumn<Client, String> colCodePostal;
    @FXML private TableColumn<Client, String> colVille;
    @FXML private TableColumn<Client, String> colPays;
    @FXML private TableColumn<Client, String> colDateInscription;
    @FXML private TableColumn<Client, String> colStatut;
    @FXML private TableColumn<Client, Void> colActions;

    /**
     * Constructeur vide.
     * <p>
     * Les services doivent être injectés via les setters après l'instanciation.
     * </p>
     */
    public ControllerListClient() {
        this.clientService = null;
        this.commentaireService = null;
    }


    /**
     * Initialisation du controller FXML.
     * <p>
     * Configure les colonnes, la largeur, le filtrage, le placeholder et les boutons.
     * </p>
     */
    @FXML
    public void initialize() {
        configureColumns();
        configureTableWidth();
        configureFiltering();
        configurePlaceHolder();
        configureButton();
    }

    // =========================
    // BUSINESS ACTIONS
    // =========================

    /**
     * Charge tous les clients depuis le service et met à jour la table.
     */
    public void loadClients() {
        if(clientService != null) {
            List<Client> clientsOfService = clientService.findAll();
            clients.setAll(clientsOfService);
        }
    }

    /**
     * Initialise les données de l'interface, notamment le label de bienvenue.
     */
    public void initData() {
        if (appContext == null || appContext.getCommercial() == null) {
            log.warn("AppContext ou commercial non défini");
            return;
        }
        lblTitre.setText(
                "Bienvenue " +
                appContext.getCommercial().getPrenom() +
                " " +
                appContext.getCommercial().getNom()
        );
    }

    // =========================
    // CONFIGURATION DE LA TABLE
    // =========================

    /** Configure les colonnes du TableView avec les propriétés des clients */
    private void configureColumns() {
        colSociete.setCellValueFactory(c -> property(c.getValue().getSociete()));
        colNom.setCellValueFactory(c -> property(c.getValue().getNom()));
        colPrenom.setCellValueFactory(c -> property(c.getValue().getPrenom()));
        colEmail.setCellValueFactory(c -> property(c.getValue().getEmail()));
        colTelephone.setCellValueFactory(c -> property(c.getValue().getTelephone()));
        colAdresse.setCellValueFactory(c -> property(c.getValue().getAdresse()));
        colCodePostal.setCellValueFactory(c -> property(c.getValue().getCodePostal()));
        colVille.setCellValueFactory(c -> property(c.getValue().getVille()));
        colPays.setCellValueFactory(c -> property(c.getValue().getPays()));
        colDateInscription.setCellValueFactory(c ->
                property(c.getValue().getDateInscription() != null
                        ? c.getValue().getDateInscription().toString()
                        : null));
        colStatut.setCellValueFactory(c ->
                property(c.getValue().getStatut() != null
                        ? c.getValue().getStatut().name()
                        : null));
    }

    /** Retourne une propriété de chaîne pour la cellule, ou "N/D" si null ou vide */
    private SimpleStringProperty property(String value) {
        return new SimpleStringProperty(
                value == null || value.isBlank() ? "N/D" : value
        );
    }

    /** Configure la largeur des colonnes selon un ratio défini */
    private void configureTableWidth() {
        double[] weights = {0.07, 0.07, 0.07, 0.137, 0.07, 0.14, 0.05, 0.09, 0.04, 0.05, 0.07, 0.14};
        UiFxUtils.setWidthTableView(tableClients, weights);
    }

    /** Configure les boutons d'action dans la colonne dédiée */
    private void configureButton() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetail = new Button("Détails");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnDetail, btnSupprimer);

            {
                btnDetail.setMaxWidth(Double.MAX_VALUE);
                btnSupprimer.setMaxWidth(Double.MAX_VALUE);
                btnDetail.getStyleClass().add("btn-detail");
                btnSupprimer.getStyleClass().add("btn-supprimer");

                HBox.setHgrow(btnDetail, Priority.ALWAYS);
                HBox.setHgrow(btnSupprimer, Priority.ALWAYS);

                pane.setAlignment(Pos.CENTER);
            }

            {
                // Actions des boutons
                btnDetail.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    try {
                        showDetailClient(client);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                btnSupprimer.setOnAction(event -> {
                    Client client = getTableView().getItems().get(getIndex());
                    supprimerClient(client);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    /** Configure le filtrage dynamique via la TextField de recherche */
    private void configureFiltering() {
        filteredClients = new FilteredList<>(clients, p -> true);
        tableClients.setItems(filteredClients);

        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredClients.setPredicate(client -> {
                if (filter.isBlank()) return true;
                return contains(client.getNom(), filter)
                        || contains(client.getPrenom(), filter)
                        || contains(client.getSociete(), filter)
                        || contains(client.getEmail(), filter)
                        || contains(client.getTelephone(), filter)
                        || contains(client.getAdresse(), filter)
                        || contains(client.getCodePostal(), filter)
                        || contains(client.getVille(), filter)
                        || contains(client.getPays(), filter);
            });
        });
    }

    private boolean contains(String value, String filter) {
        return value != null && value.toLowerCase().contains(filter);
    }

    /** Définit un placeholder lorsque aucun client n'est présent */
    private void configurePlaceHolder() {
        Label placeholder = new Label("Aucun client trouvé");
        placeholder.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-font-size: 14px;");
        placeholder.setAlignment(Pos.CENTER);
        tableClients.setPlaceholder(placeholder);
    }

    // =========================
    // BUSINESS ACTIONS BUTTONS
    // =========================

    /**
     * Affiche la fenêtre de détails d'un client.
     *
     * @param client client à afficher
     * @throws IOException si le chargement du FXML échoue
     */
    @FXML
    private void showDetailClient(Client client) throws IOException {
        log.info("Voir détails client {}", client.getNom());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detail_client/detail_client.fxml"));
        Parent root = loader.load();

        ControllerDetailClient controller = loader.getController();
        controller.setClientService(this.clientService);
        controller.setCommentaireService(this.commentaireService);
        controller.initData(client.getId());

        Stage stage = new Stage();

        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.setTitle("Détails du client : " + client.getNom() + " " + client.getPrenom());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        // Rafraîchit le contexte et la table après fermeture
        appContext.refreshData();
        loadClients();
    }

    /**
     * Supprime un client après confirmation.
     *
     * @param client client à supprimer
     */
    @FXML
    private void supprimerClient(Client client) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le client");
        confirm.setContentText("Confirmer la suppression de " + client.getNom() + " ? Cette action est irréversible et supprimeras les devis liés au client.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                clientService.deleteClientById(client.getId());
                clients.remove(client);
                log.info("Client supprimé {}", client.getNom());
            }
        });
    }

    /**
     * Affiche la fenêtre d'ajout d'un client.
     *
     * @param event événement déclencheur
     * @throws IOException si le chargement du FXML échoue
     */
    @FXML
    private void handleAjouterClient(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajouter_client/ajouter_client.fxml"));
        Parent root = loader.load();

        ControllerAjouterClient controller = loader.getController();
        controller.setClientService(this.clientService);

        Stage stage = new Stage();
        controller.setStage(stage);

        stage.setScene(new Scene(root));
        stage.setTitle("Ajouter un Client");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        loadClients();
    }

    /**
     * Affiche la fenêtre des paramètres.
     *
     * @param event événement déclencheur
     * @throws IOException si le chargement du FXML échoue
     */
    @FXML
    private void handleSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/parameter/parameter.fxml"));
        Parent root = loader.load();
        ParameterController controller = loader.getController();
        controller.setAppContext(this.appContext);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Paramètres");
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        appContext.refreshData();
        initData();
    }
}