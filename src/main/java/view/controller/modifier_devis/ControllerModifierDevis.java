package view.controller.modifier_devis;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import model.Devis;
import model.StatutDevis;
import service.IDevisService;
import view.helper.UiErrorHandler;
import view.helper.form_validator.DevisForm;

/**
 * Contrôleur pour la fenêtre de modification d'un devis.
 * <p>
 * Ce contrôleur permet :
 * <ul>
 *     <li>Initialiser les champs avec les informations existantes du devis</li>
 *     <li>Valider et sauvegarder les modifications via le service IDevisService</li>
 *     <li>Annuler la modification et fermer la fenêtre</li>
 *     <li>Afficher les erreurs de saisie et les erreurs globales</li>
 * </ul>
 */
public class ControllerModifierDevis {

    @FXML private Label lblErrorReference, lblErrorDateCreation, lblErrorMontant, lblGlobalError;
    @FXML private TextField tfReference;
    @FXML private DatePicker dpDateCreation;
    @FXML private TextField tfMontant;
    @FXML private ComboBox<StatutDevis> cbStatut;
    @FXML private TextArea tfDescription;
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;

    private Stage stage;

    @Setter private IDevisService devisService;
    private Devis devis;
    @Getter private boolean saved = false;

    /**
     * Initialise les champs avec les données du devis et configure les actions.
     *
     * @param devis le devis à modifier
     * @param devisService le service pour gérer les devis
     * @param stage la fenêtre actuelle
     */
    public void initData(Devis devis, IDevisService devisService, Stage stage) {
        this.devis = devis;
        this.devisService = devisService;
        this.stage = stage;

        tfReference.setText(devis.getReference());
        dpDateCreation.setValue(devis.getDateCreation());
        tfMontant.setText(String.format("%.2f", devis.getMontant()));
        cbStatut.getItems().setAll(StatutDevis.values());
        cbStatut.setValue(devis.getStatut());
        tfDescription.setText(devis.getDescription());

        btnAnnuler.setOnAction(e -> stage.close());
        btnModifier.setOnAction(e -> handleModifier());
        setupValidation();
    }

    /**
     * Sauvegarde les modifications apportées au devis via le service.
     * <p>
     * En cas d'erreur, affiche un message global dans lblGlobalError.
     * Si la sauvegarde réussit, ferme la fenêtre et met saved = true.
     */
    private void handleModifier() {
        try {
            devis.setReference(tfReference.getText());
            devis.setDateCreation(dpDateCreation.getValue());
            if(tfMontant.getText().isBlank()) tfMontant.setText("0.00");
            devis.setMontant(Double.parseDouble(tfMontant.getText().replace(",", ".")));
            devis.setStatut(cbStatut.getValue());
            devis.setDescription(tfDescription.getText());

            devisService.updateDevis(devis);
            saved = true;
            UiErrorHandler.clear(lblGlobalError);
            stage.close();

        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }
    }

    /**
     * Configure la validation des champs du formulaire devis.
     * <p>
     * Active/désactive le bouton de modification et affiche les erreurs spécifiques.
     */
    private void setupValidation(){
        DevisForm.validateDevisForm(
                tfReference,
                dpDateCreation,
                tfMontant,
                lblErrorReference,
                lblErrorDateCreation,
                lblErrorMontant,
                btnModifier);
    }
}

