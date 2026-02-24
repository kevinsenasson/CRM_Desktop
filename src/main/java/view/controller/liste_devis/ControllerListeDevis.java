package view.controller.liste_devis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Devis;
import model.StatutDevis;
import service.IDevisService;
import view.controller.ajouter_devis.ControllerAjouterDevis;
import view.helper.UiFxUtils;
import view.controller.modifier_devis.ControllerModifierDevis;

/**
 * Contrôleur pour la liste des devis d'un client.
 * <p>
 * Ce contrôleur permet de :
 * <ul>
 *     <li>Afficher la liste des devis pour un client donné</li>
 *     <li>Ajouter, modifier ou supprimer un devis</li>
 *     <li>Afficher les détails des devis dans un {@link TableView}</li>
 *     <li>Mettre en forme les colonnes, y compris le statut avec couleur et description</li>
 * </ul>
 * Il utilise {@link IDevisService} pour récupérer et manipuler les données.
 */
@Slf4j
public class ControllerListeDevis {

    @Setter private IDevisService devisService;
    @Setter private Integer idClient;
    @Setter private String nomClient;

    @FXML private Label lblTitre, lblAucunDevis;
    @FXML private TableView<Devis> tableDevis;
    @FXML private TableColumn<Devis, String> colReference;
    @FXML private TableColumn<Devis, String> colDate;
    @FXML private TableColumn<Devis, String> colMontant;
    @FXML private TableColumn<Devis, StatutDevis> colStatut;
    @FXML private TableColumn<Devis, String> colDescription;
    @FXML private TableColumn<Devis, Void> colActions;
    @FXML private Button btnAjouterDevis;

    private final ObservableList<Devis> devisList = FXCollections.observableArrayList();

    /**
     * Initialise le {@link TableView} et ajuste les largeurs des colonnes.
     */
    @FXML
    public void initialize() {
        double[] weights = {0.16, 0.16, 0.16, 0.16, 0.16, 0.2};
        UiFxUtils.setWidthTableView(tableDevis, weights);
    }

    /**
     * Charge la liste des devis du client et met à jour l'affichage.
     */
    public void loadDevis() {
        devisList.setAll(devisService.getDevisByClientId(idClient));
        tableDevis.setItems(devisList);

        lblAucunDevis.setVisible(devisList.isEmpty());
        lblAucunDevis.setManaged(devisList.isEmpty());

        lblTitre.setText("Liste des devis : " + nomClient);
        setupColumns();
        addActionsToTable();
    }

    /**
     * Configure les colonnes de la {@link TableView}, incluant :
     * <ul>
     *     <li>Formatage du statut avec couleur et style</li>
     *     <li>Affichage par défaut pour les descriptions vides</li>
     * </ul>
     */
    private void setupColumns() {
        colReference.setCellValueFactory(new PropertyValueFactory<>("reference"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colMontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        colStatut.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(StatutDevis statut, boolean empty) {
                super.updateItem(statut, empty);

                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut.name());
                    setStyle("""
                    -fx-alignment: CENTER;
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                    -fx-background-color: %s;
                """.formatted(getStatutColor(statut)));
                }
            }
        });
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDescription.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String description, boolean empty) {
                super.updateItem(description, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else if (description == null || description.isBlank()) {
                    setText("aucune description");
                    setStyle("-fx-font-style: italic; -fx-text-fill: #888888; -fx-alignment: center;");
                } else {
                    setText(description);
                    setStyle("-fx-alignment: center;");
                }
            }
        });
    }

    /**
     * Retourne la couleur associée à un {@link StatutDevis}.
     * @param statut statut du devis
     * @return couleur hexadécimale
     */
    private String getStatutColor(StatutDevis statut){
        return switch (statut) {
            case ENVOYE -> "#3498db";
            case ACCEPTE -> "#27ae60";
            case REFUSE -> "#e74c3c";
        };
    }

    /**
     * Ajoute les boutons d'action Modifier et Supprimer à la dernière colonne.
     */
    private void addActionsToTable() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnModifier = new Button("Modifier");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox container = new HBox(5, btnModifier, btnSupprimer);

            {
                btnModifier.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 6;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 6;");

                container.setAlignment(Pos.CENTER);

                btnModifier.setOnAction(event -> {
                    Devis devis = getTableView().getItems().get(getIndex());
                    handleModifierDevis(devis);
                });

                btnSupprimer.setOnAction(event -> {
                    Devis devis = getTableView().getItems().get(getIndex());
                    handleSupprimerDevis(devis);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    /**
     * Ouvre la fenêtre d'ajout d'un devis pour le client.
     */
    @FXML
    private void handleAjouterDevis() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajouter_devis/ajouter_devis.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Ajouter un devis");
            stage.initModality(Modality.APPLICATION_MODAL);

            ControllerAjouterDevis controller = loader.getController();
            controller.initData(idClient, devisService, stage);

            stage.showAndWait();

            if(controller.isSaved()){
                loadDevis();
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'ouverture de la fenetre d'ajout de devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Ouvre la fenêtre de modification d'un devis existant.
     * @param devis devis à modifier
     */
    private void handleModifierDevis(Devis devis) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/modifier_devis/modifier_devis.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Modifier le devis");

            ControllerModifierDevis controller = loader.getController();
            controller.initData(devis, devisService, stage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            if(controller.isSaved()){
                loadDevis();
            }

        } catch (Exception e) {
            log.error("Erreur lors de l'ouverture de la fenêtre de modification de devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Supprime un devis après confirmation de l'utilisateur.
     * @param devis devis à supprimer
     */
    private void handleSupprimerDevis(Devis devis) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le Devis");
        confirm.setContentText("Confirmer la suppression du devis : " + devis.getReference() + " ? Cette action est irréversible.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                log.info("Suppression du devis {}", devis.getReference());
                devisService.deleteDevisById(devis.getId());
                loadDevis();
            }
        });
    }
}
