package view.controller.ajouter_devis;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import model.Devis;
import model.StatutDevis;
import service.IDevisService;
import view.helper.UiErrorHandler;
import view.helper.form_validator.DevisForm;

import java.time.LocalDate;

public class ControllerAjouterDevis {

    // =========================
    // FXML ELEMENTS
    // =========================
    @FXML public Label lblErrorReference, lblErrorMontant, lblErrorDateCreation, lblGlobalError;
    @FXML private TextField tfReference, tfMontant;
    @FXML private DatePicker dpDate;
    @FXML private ChoiceBox<StatutDevis> cbStatut;
    @FXML private TextArea taDescription;
    @FXML private Button btnAnnuler, btnEnregistrer;


    // =========================
    // CONTEXTE & SERVICE
    // =========================
    private Stage stage;
    private IDevisService devisService;
    private Integer idClient;

    @Getter private boolean saved = false;

    // =========================
    // INITIALISATION
    // =========================

    /**
     * Initialise le formulaire avec le client, le service et la fenêtre.
     *
     * @param idClient      Identifiant du client associé au devis
     * @param devisService  Service pour sauvegarder le devis
     * @param stage         Fenêtre courante
     */
    public void initData(Integer idClient, IDevisService devisService, Stage stage) {
        this.idClient = idClient;
        this.devisService = devisService;
        this.stage = stage;

        cbStatut.getItems().setAll(StatutDevis.values());
        cbStatut.setValue(StatutDevis.ENVOYE); // valeur par défaut
        dpDate.setValue(LocalDate.now());

        btnAnnuler.setOnAction(e -> stage.close());
        btnEnregistrer.setOnAction(e -> handleEnregistrer());
        setupValidation();
    }

    // =========================
    // ACTION METIER
    // =========================

    /**
     * Récupère les valeurs du formulaire, crée un nouveau devis et le sauvegarde.
     * <p>
     * Gère les champs obligatoires, la conversion du montant, et les erreurs via {@link UiErrorHandler}.
     * </p>
     */
    private void handleEnregistrer() {
        String ref = tfReference.getText();
        LocalDate date = dpDate.getValue();
        String montantStr = tfMontant.getText();
        StatutDevis statut = cbStatut.getValue();
        String description = taDescription.getText();

        if(ref.isBlank() || date == null || montantStr.isBlank() || statut == null){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return;
        }

        double montant;
        try {
            montant = Double.parseDouble(montantStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Le montant doit être un nombre valide.");
            alert.showAndWait();
            return;
        }

        Devis devis = new Devis();
        devis.setIdClient(idClient);
        devis.setReference(ref);
        devis.setDateCreation(date);
        devis.setMontant(montant);
        devis.setStatut(statut);
        devis.setDescription(description);

        try {
            devisService.saveDevis(devis);
            saved = true;
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
     * Configure la validation dynamique du formulaire via {@link DevisForm}.
     */
    private void setupValidation(){
        DevisForm.validateDevisForm(
                tfReference,
                dpDate,
                tfMontant,
                lblErrorReference,
                lblErrorDateCreation,
                lblErrorMontant,
                btnEnregistrer);
    }

}
