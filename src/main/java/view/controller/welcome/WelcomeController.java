package view.controller.welcome;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.Setter;
import model.Enterprise;
import service.*;
import view.app.AppContext;
import view.helper.UiErrorHandler;
import view.helper.form_validator.EnterpriseForm;
import view.app.AppLauncher;

/**
 * Contrôleur pour la page d'accueil et création de l'entreprise.
 * <p>
 * Ce contrôleur gère la saisie des informations de l'entreprise et leur
 * validation avant de les sauvegarder via le service IEnterpriseService.
 * Il permet ensuite de passer à la scène suivante pour ajouter les commerciaux.
 * </p>
 */
public class WelcomeController {

    @FXML private TextField tfNom, tfAdresse, tfCodePostal, tfVille, tfTelephone, tfEmail, tfSiret;
    @FXML private Button btnValider;
    @FXML private Label lblErrorNom, lblErrorAdresse, lblErrorCodePostal, lblErrorVille, lblErrorEmail, lblErrorTelephone, lblErrorSiret, lblGlobalError;

    @Setter private AppContext appContext;
    @Setter private IEnterpriseService enterpriseService;

    /**
     * Initialise le contrôleur et configure le bouton de validation et la validation du formulaire.
     */
    @FXML
    private void initialize() {
        btnValider.setOnAction(e -> handleSubmit());
        setupValidation();
    }

    /**
     * Récupère les données saisies par l'utilisateur, les valide et les sauvegarde.
     * <p>
     * En cas de succès, stocke l'entreprise dans le contexte et charge la page suivante.
     * En cas d'erreur, affiche un message global.
     */
    private void handleSubmit() {
        if (!isValid()) {
            AppLauncher.showAlert();
            return;
        }

        Enterprise enterprise = new Enterprise(
                null,
                tfNom.getText().trim(),
                tfAdresse.getText().trim(),
                tfCodePostal.getText().trim(),
                tfVille.getText().trim(),
                tfTelephone.getText().trim(),
                tfEmail.getText().trim(),
                tfSiret.getText().trim()
        );

        try {
            enterpriseService.saveEnterprise(enterprise);
            appContext.setEnterprise(enterprise);
            goToNextScene(appContext);
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }
    }

    /**
     * Vérifie que tous les champs obligatoires sont remplis.
     *
     * @return true si tous les champs sont non vides, false sinon
     */
    private boolean isValid() {
        return !tfNom.getText().isBlank()
                && !tfAdresse.getText().isBlank()
                && !tfCodePostal.getText().isBlank()
                && !tfVille.getText().isBlank()
                && !tfTelephone.getText().isBlank()
                && !tfEmail.getText().isBlank()
                && !tfSiret.getText().isBlank();
    }

    /**
     * Charge la scène suivante après la création de l'entreprise.
     *
     * @param appContext le contexte de l'application
     */
    private void goToNextScene(AppContext appContext) {
        try {
            Stage stage = (Stage) btnValider.getScene().getWindow();
            AppLauncher.loadAddCommercialPage(stage, getClass(), appContext);
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
        }
    }

    /**
     * Configure la validation des champs du formulaire entreprise
     * en utilisant EnterpriseForm.
     */
    private void setupValidation(){
        EnterpriseForm.validateEnterpriseForm(
                tfNom,
                tfAdresse,
                tfCodePostal,
                tfVille,
                tfEmail,
                tfTelephone,
                tfSiret,
                lblErrorNom,
                lblErrorAdresse,
                lblErrorCodePostal,
                lblErrorVille,
                lblErrorEmail,
                lblErrorTelephone,
                lblErrorSiret,
                btnValider
        );
    }
}
