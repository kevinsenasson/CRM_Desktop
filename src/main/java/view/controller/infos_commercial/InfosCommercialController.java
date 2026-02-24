package view.controller.infos_commercial;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Setter;
import model.Commercial;
import service.ICommercialService;
import view.app.AppContext;
import view.controller.menu.ParameterController;
import view.helper.UiErrorHandler;
import view.helper.UiFxUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static view.helper.UiFxUtils.addEditableField;

/**
 * Contrôleur pour l'affichage et l'édition des informations d'un commercial.
 * <p>
 * Permet de visualiser et modifier :
 * <ul>
 *     <li>Nom et prénom</li>
 *     <li>Adresse mail</li>
 *     <li>Téléphone</li>
 *     <li>Date d'embauche</li>
 *     <li>Chiffre d'affaires actuel et objectif</li>
 * </ul>
 * Les modifications sont sauvegardées via {@link ICommercialService}.
 */
public class InfosCommercialController {

    @FXML private VBox fieldsContainer;
    @FXML private Label lblGlobalError;
    @FXML private StackPane contentPane;
    @FXML private Button btnRetour;

    private AppContext appContext;
    private Commercial commercial;
    @Setter private ICommercialService commercialService;

    /**
     * Initialise le contexte de l'application et charge les données du commercial.
     * @param context contexte partagé de l'application
     */
    public void setAppContext(AppContext context) {
        this.appContext = context;
        initData();
    }

    /**
     * Charge et affiche les informations du commercial courant dans des champs éditables.
     * Les champs sont ajoutés dynamiquement via {@link UiFxUtils#addEditableField}.
     */
    public void initData() {
        this.commercial = appContext.getCommercial();

        addEditableField(fieldsContainer, "Nom", commercial.getNom(), v -> commercial.setNom(v), this::save, lblGlobalError);
        addEditableField(fieldsContainer,"Prénom", commercial.getPrenom(), v -> commercial.setPrenom(v) , this::save, lblGlobalError);
        addEditableField(fieldsContainer,"Mail", commercial.getMail(), v -> commercial.setMail(v), this::save, lblGlobalError);
        addEditableField(fieldsContainer,"Téléphone", commercial.getTelephone(), v -> commercial.setTelephone(v), this::save, lblGlobalError);
        addEditableField(fieldsContainer, "Date d'embauche",
                commercial.getDateEmbauche() != null ? commercial.getDateEmbauche().format(DateTimeFormatter.ISO_DATE) : "",
                v -> commercial.setDateEmbauche(LocalDate.parse(v)),
                this::save,
                lblGlobalError
        );
        addField(commercial.getCa() + " €");
        addEditableField(fieldsContainer,"Objectif CA", commercial.getObjectifCa() + " €",
                v -> commercial.setObjectifCa(Integer.parseInt(v)), this::save, lblGlobalError);
    }

    /**
     * Supprime tous les champs de la vue.
     */
    private void clearData(){
        fieldsContainer.getChildren().clear();
    }

    /**
     * Ajoute un champ statique (non éditable) pour afficher le chiffre d'affaires.
     * @param value valeur à afficher
     */
    private void addField(String value){
        HBox row = new HBox(15);
        row.setStyle("-fx-background-color:white; -fx-padding:15; -fx-background-radius:8;");
        row.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label("CA");
        label.setStyle("-fx-font-weight:bold;");

        Label valueLabel = new Label(value == null ? "ND" : value);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);

        row.getChildren().addAll(label, valueLabel);
        fieldsContainer.getChildren().add(row);
    }

    /**
     * Sauvegarde les modifications effectuées sur le commercial courant.
     * <p>
     * En cas d'erreur, le message est affiché et les données sont rechargées depuis le contexte.
     */
    private void save() {
        try {
            commercialService.updateCommercial(appContext.getCommercial());
            lblGlobalError.setText("");
        } catch (Exception e) {
            UiErrorHandler.handle(e, lblGlobalError);
            appContext.refreshData();
            clearData();
            initData();
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