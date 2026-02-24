package view.controller.ajouter_commercial;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Commercial;
import service.ICommercialService;
import view.app.AppContext;
import view.app.AppLauncher;
import view.helper.UiErrorHandler;
import view.helper.form_validator.CommercialForm;

import java.time.LocalDate;

/**
 * Contrôleur pour la fenêtre d'ajout d'un commercial.
 *
 * <p>
 * Gère l'interface pour la création d'un nouveau commercial.
 * Permet :
 * <ul>
 *     <li>La saisie des informations du commercial</li>
 *     <li>La validation des champs via {@link CommercialForm}</li>
 *     <li>L'enregistrement du commercial via {@link ICommercialService}</li>
 *     <li>La gestion des erreurs et leur affichage dans {@link #lblGlobalError}</li>
 *     <li>Le rafraîchissement du dashboard principal après ajout</li>
 * </ul>
 * </p>
 */
@Slf4j
public class ControllerAjouterCommercial {

    @Setter private AppContext appContext;
    @Setter private ICommercialService commercialService;

    // =========================
    // FXML ELEMENTS
    // =========================

    @FXML private TextField tfNom, tfPrenom, tfMail, tfTelephone, tfObjCa;
    @FXML private DatePicker dpDateEmbauche;
    @FXML private Label lblErrorNom, lblErrorPrenom, lblErrorDateEmbauche, lblErrorObjCa, lblErrorTelephone, lblErrorEmail, lblGlobalError;
    @FXML private Button btnValider;


    // =========================
    // INITIALISATION
    // =========================

    /**
     * Initialisation FXML.
     * <p>
     * Définit l'action du bouton de validation et configure la validation des champs.
     * </p>
     */
    @FXML
    private void initialize(){
        btnValider.setOnAction(e -> {
            try {
                handleSubmit();
            } catch (Exception ex) {
                log.error("Erreur lors de l'envoi du formulaire", ex);
                throw new RuntimeException(ex);
            }
        });

        setupValidation();
    }

    // =========================
    // ACTION METIER
    // =========================

    /**
     * Récupère les données du formulaire et crée un nouveau commercial.
     * <p>
     * Vérifie la validité des champs, gère les erreurs et met à jour le dashboard principal.
     * </p>
     */
    private void handleSubmit() {
        if (!isValid()) {
            AppLauncher.showAlert();
            return;
        }

        Commercial commercial = new Commercial();
        commercial.setIdEntreprise(1); // TODO : remplacer par ID réel si nécessaire
        commercial.setNom(tfNom.getText());
        commercial.setPrenom(tfPrenom.getText());
        commercial.setMail(tfMail.getText());
        commercial.setTelephone(tfTelephone.getText());
        commercial.setDateEmbauche(dpDateEmbauche.getValue() != null
                ? dpDateEmbauche.getValue()
                : LocalDate.now());
        commercial.setCa(0);

        try {
            commercial.setObjectifCa(Integer.parseInt(tfObjCa.getText()));
        } catch (NumberFormatException e) {
            log.error("Objectif CA invalide");
            UiErrorHandler.showInLabel("Objectif CA invalide", lblGlobalError);
            return;
        }

        try {
            commercialService.saveCommercial(commercial);
            UiErrorHandler.clear(lblGlobalError);
            Stage stage = (Stage) btnValider.getScene().getWindow();
            appContext.refreshData();
            AppLauncher.loadMainDashboard(stage, getClass(), appContext);
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }
    }

    // =========================
    // VALIDATION
    // =========================

    /**
     * Vérifie que tous les champs obligatoires sont remplis.
     *
     * @return true si tous les champs obligatoires contiennent une valeur
     */
    private boolean isValid() {
        return !tfNom.getText().isBlank()
                && !tfPrenom.getText().isBlank()
                && !dpDateEmbauche.getValue().toString().isBlank()
                && !tfObjCa.getText().isBlank()
                && !tfTelephone.getText().isBlank()
                && !tfMail.getText().isBlank();
    }

    /**
     * Configure la validation dynamique du formulaire via {@link CommercialForm}.
     */
    private void setupValidation() {
        CommercialForm.validateCommercialForm(
                tfNom,
                tfPrenom,
                tfMail,
                tfTelephone,
                dpDateEmbauche,
                tfObjCa,
                lblErrorNom,
                lblErrorPrenom,
                lblErrorEmail,
                lblErrorTelephone,
                lblErrorDateEmbauche,
                lblErrorObjCa,
                btnValider
        );
    }
}
