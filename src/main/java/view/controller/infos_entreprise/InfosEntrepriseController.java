package view.controller.infos_entreprise;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import model.Enterprise;
import service.IEnterpriseService;
import view.app.AppContext;
import view.controller.menu.ParameterController;
import view.helper.UiErrorHandler;
import view.helper.UiFxUtils;

import java.io.IOException;

import static view.helper.UiFxUtils.addEditableField;

/**
 * Contrôleur pour l'affichage et l'édition des informations d'une entreprise.
 * <p>
 * Permet de visualiser et modifier les champs suivants :
 * <ul>
 *     <li>Nom</li>
 *     <li>Adresse</li>
 *     <li>Code postal</li>
 *     <li>Ville</li>
 *     <li>Téléphone</li>
 *     <li>Email</li>
 *     <li>SIRET</li>
 * </ul>
 * Les modifications sont sauvegardées via {@link IEnterpriseService}.
 */
public class InfosEntrepriseController {

    @FXML private VBox containerFields;
    @FXML private Label lblGlobalError;
    @FXML private StackPane contentPane;

    private AppContext appContext;
    @Setter private IEnterpriseService enterpriseService;

    /**
     * Initialise le contexte de l'application et charge les informations de l'entreprise.
     * @param context contexte partagé de l'application
     */
    public void setAppContext(AppContext context) {
        this.appContext = context;
        loadFields();
    }

    /**
     * Charge et affiche les champs de l'entreprise dans des champs éditables.
     * Chaque champ est créé via {@link UiFxUtils#addEditableField}.
     */
    private void loadFields() {
        Enterprise enterprise = appContext.getEnterprise();

        addEditableField(containerFields, "Nom", enterprise.getNom(), enterprise::setNom, this::save, lblGlobalError);
        addEditableField(containerFields,"Adresse", enterprise.getAdresse(), enterprise::setAdresse, this::save, lblGlobalError);
        addEditableField(containerFields,"Code postal", enterprise.getCodePostal(), enterprise::setCodePostal, this::save, lblGlobalError);
        addEditableField(containerFields,"Ville", enterprise.getVille(), enterprise::setVille, this::save, lblGlobalError);
        addEditableField(containerFields,"Téléphone", enterprise.getTelephone(), enterprise::setTelephone, this::save, lblGlobalError);
        addEditableField(containerFields,"Email", enterprise.getEmail(), enterprise::setEmail, this::save, lblGlobalError);
        addEditableField(containerFields,"SIRET", enterprise.getSiret(), enterprise::setSiret, this::save, lblGlobalError);
    }

    /**
     * Supprime tous les champs de l'interface graphique.
     */
    private void clearData(){
        containerFields.getChildren().clear();
    }

    /**
     * Sauvegarde les modifications effectuées sur l'entreprise.
     * <p>
     * En cas d'erreur, un message est affiché et les données sont rechargées depuis le contexte.
     */
    private void save(){
        try {
            enterpriseService.updateEnterprise(appContext.getEnterprise());
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
            appContext.refreshData();
            clearData();
            loadFields();
        }
    }

    /**
     * Retourne à l'écran de paramètres.
     * <p>
     * Effectue une transition animée avec {@link UiFxUtils#slideTo}.
     * @throws IOException si le FXML ne peut pas être chargé
     */
    @FXML
    private void handleReturn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/parameter/parameter.fxml"));
        Parent root = loader.load();
        ParameterController controller = loader.getController();
        appContext.refreshData();
        controller.setAppContext(appContext);
        UiFxUtils.slideTo(contentPane, root, false);
    }
}
