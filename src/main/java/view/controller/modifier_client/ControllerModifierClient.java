package view.controller.modifier_client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import model.Client;
import model.SegmentClient;
import model.SourceAcquisitionClient;
import model.StatutClient;
import service.IClientService;
import view.helper.UiErrorHandler;
import view.helper.form_validator.ClientForm;

/**
 * Contrôleur pour la fenêtre de modification d'un client.
 * <p>
 * Ce contrôleur permet :
 * <ul>
 *     <li>Initialiser les champs avec les informations existantes d'un client</li>
 *     <li>Valider et enregistrer les modifications via le service IClientService</li>
 *     <li>Annuler la modification et fermer la fenêtre</li>
 *     <li>Afficher les erreurs de saisie et les erreurs globales</li>
 * </ul>
 */
public class ControllerModifierClient {

    @FXML private Button btnEnregistrer;
    @FXML private TextField tfNom,tfPrenom, tfSociete, tfEmail, tfTelephone, tfAdresse, tfVille, tfCodePostal, tfPays;
    @FXML private ComboBox<StatutClient> cbStatut;
    @FXML private ComboBox<SegmentClient> cbSegment;
    @FXML private ComboBox<SourceAcquisitionClient> cbSourceAcquisition;
    @FXML private Label lblErrorNom, lblErrorPrenom, lblErrorEmail, lblErrorSociete, lblErrorStatut, lblErrorTelephone, lblErrorCodePostal, lblGlobalError;

    private Client client;
    @Setter private Stage stage;
    @Setter private IClientService clientService;

    /**
     * Constructeur par défaut.
     * Initialise les références à null.
     */
    public ControllerModifierClient() {
        this.client = null;
        this.clientService = null;
    }

    /**
     * Initialise les composants FXML après le chargement.
     * <ul>
     *     <li>Remplit les ComboBox avec les valeurs des énumérations</li>
     *     <li>Configure la largeur des ComboBox</li>
     *     <li>Initialise la validation des champs via ClientForm</li>
     * </ul>
     */
    @FXML
    public void initialize(){
        cbStatut.getItems().addAll(StatutClient.values());
        cbSegment.getItems().addAll(SegmentClient.values());
        cbSourceAcquisition.getItems().addAll(SourceAcquisitionClient.values());
        cbStatut.setStyle("-fx-pref-width: Infinity;");
        cbSegment.setStyle("-fx-pref-width: Infinity;");
        cbSourceAcquisition.setStyle("-fx-pref-width: Infinity;");
        setupValidation();
    }

    /**
     * Initialise les champs avec les données du client fourni.
     *
     * @param client le client à modifier
     */
    public void initData(Client client){
        this.client = client;

        tfNom.setText(client.getNom());
        tfPrenom.setText(client.getPrenom());
        tfSociete.setText(client.getSociete());
        tfEmail.setText(client.getEmail());
        tfTelephone.setText(client.getTelephone());
        tfAdresse.setText(client.getAdresse());
        tfVille.setText(client.getVille());
        tfCodePostal.setText(client.getCodePostal());
        tfPays.setText(client.getPays());
        cbStatut.setValue(client.getStatut());
        cbSegment.setValue(client.getSegment());
        cbSourceAcquisition.setValue(client.getSourceAcquisition());
    }

    /**
     * Sauvegarde les modifications apportées au client via le service.
     * <p>
     * En cas d'erreur, affiche un message global dans lblGlobalError.
     */
    @FXML
    private void handleUpdateClient() {
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
            clientService.updateClient(client);
            UiErrorHandler.clear(lblGlobalError);
            stage.close();
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }

    }

    /**
     * Ferme la fenêtre de modification sans sauvegarder.
     */
    @FXML
    private void handleCancel() {
        stage.close();
    }

    /**
     * Configure la validation des champs du formulaire client.
     * <p>
     * Active/désactive le bouton d'enregistrement et affiche les erreurs spécifiques.
     */
    private void setupValidation() {
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
