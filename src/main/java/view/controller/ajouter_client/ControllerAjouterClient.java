package view.controller.ajouter_client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Client;
import model.SegmentClient;
import model.SourceAcquisitionClient;
import model.StatutClient;
import service.IClientService;
import view.helper.UiErrorHandler;
import view.helper.form_validator.ClientForm;

/**
 * Contrôleur pour la fenêtre d'ajout d'un client.
 *
 * <p>
 * Cette classe gère l'interface d'ajout d'un nouveau client dans l'application.
 * Elle permet :
 * <ul>
 *     <li>La saisie des informations du client</li>
 *     <li>La validation des champs via {@link ClientForm}</li>
 *     <li>La création du client via {@link IClientService}</li>
 *     <li>La gestion des erreurs et leur affichage dans les labels correspondants</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ControllerAjouterClient {

    @Setter private IClientService clientService;
    @Setter private Stage stage;

    // =========================
    // FXML ELEMENTS
    // =========================
    @FXML private Label lblErrorNom, lblErrorPrenom, lblErrorEmail, lblErrorSociete, lblErrorStatut, lblErrorTelephone,
            lblErrorCodePostal, lblGlobalError;

    @FXML private TextField tfNom, tfPrenom, tfSociete, tfEmail, tfTelephone, tfAdresse, tfVille, tfCodePostal, tfPays;
    @FXML private ComboBox<StatutClient> cbStatut;
    @FXML private ComboBox<SegmentClient> cbSegment;
    @FXML private ComboBox<SourceAcquisitionClient> cbSourceAcquisition;
    @FXML private Button btnEnregistrer, btnAnnuler;

    // =========================
    // INITIALISATION
    // =========================

    /**
     * Initialisation FXML.
     * <p>
     * Configure les ComboBox avec les enums correspondants,
     * définit les actions des boutons et met en place la validation du formulaire.
     * </p>
     */
    @FXML
    private void initialize() {
        // Remplissage des ComboBox
        cbStatut.getItems().addAll(StatutClient.values());
        cbSegment.getItems().addAll(SegmentClient.values());
        cbSourceAcquisition.getItems().addAll(SourceAcquisitionClient.values());

        // Ajustement de largeur
        cbStatut.setStyle("-fx-pref-width: Infinity;");
        cbSegment.setStyle("-fx-pref-width: Infinity;");
        cbSourceAcquisition.setStyle("-fx-pref-width: Infinity;");

        // Actions boutons
        btnEnregistrer.setOnAction(e -> enregistrerClient());
        btnAnnuler.setOnAction(e -> stage.close());

        // Configuration validation
        setupValidation();
    }

    // =========================
    // ACTIONS METIER
    // =========================

    /**
     * Crée un nouveau client avec les données du formulaire et l'enregistre via le service.
     * <p>
     * En cas d'erreur, le message est affiché dans {@link #lblGlobalError}.
     * </p>
     */
    private void enregistrerClient() {
        Client client = new Client();
        client.setNom(tfNom.getText());
        client.setPrenom(tfPrenom.getText());
        client.setSociete(tfSociete.getText());
        client.setEmail(tfEmail.getText());
        client.setTelephone(tfTelephone.getText());
        client.setAdresse(tfAdresse.getText());
        client.setVille(tfVille.getText());
        client.setCodePostal(tfCodePostal.getText());
        client.setPays(tfPays.getText());
        client.setStatut(cbStatut.getValue());
        client.setSegment(cbSegment.getValue());
        client.setSourceAcquisition(cbSourceAcquisition.getValue());

        try {
            clientService.saveClient(client);
            UiErrorHandler.clear(lblGlobalError);
            stage.close();
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }

    }

    // =========================
    // VALIDATION
    // =========================

    /**
     * Initialise la validation du formulaire via {@link ClientForm}.
     * <p>
     * Lie les champs et labels d'erreur au bouton d'enregistrement.
     * </p>
     */
    private void setupValidation(){
        ClientForm.validateClientForm(
                tfNom,
                tfPrenom,
                tfSociete,
                cbStatut,
                tfEmail,
                tfTelephone,
                tfCodePostal,
                lblErrorNom,
                lblErrorPrenom,
                lblErrorSociete,
                lblErrorEmail,
                lblErrorTelephone,
                lblErrorStatut,
                lblErrorCodePostal,
                btnEnregistrer);
    }


}
