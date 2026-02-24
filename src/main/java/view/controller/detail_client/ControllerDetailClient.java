package view.controller.detail_client;

import dao.CommercialDAO;
import dao.DevisDAO;
import dao.ICommercialDAO;
import dao.IDevisDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.Client;
import model.Commentaire;
import service.DevisService;
import service.IClientService;
import service.ICommentaireService;
import service.IDevisService;
import view.controller.liste_devis.ControllerListeDevis;
import view.controller.modifier_client.ControllerModifierClient;
import view.helper.CommentaireCell;

import java.util.List;

import static view.helper.UiFxUtils.getNdIfNull;

/**
 * Contrôleur de la vue détail d'un client.
 * <p>
 * Permet d'afficher les informations du client, gérer les commentaires,
 * et accéder aux devis associés.
 * La ListView de commentaires utilise des cellules personnalisées {@link CommentaireCell}.
 */
@Slf4j
public class ControllerDetailClient {

    @FXML private Label lblNom, lblPrenom, lblSociete, lblEmail, lblTelephone, lblAdresse, lblVille, lblCodePostal,
            lblPays, lblStatut, lblSocieteTitle, lblPrenomAndNomTitle, lblDateInscription, lblDateDerniereModification,
            lblSegment, lblSourceAcquisition;

    @FXML private Button btnVoirDevis, btnAjouterCommentaire, btnRetour, btnModifier;
    @FXML private TextArea tfNouveauCommentaire;
    @FXML private ListView<Commentaire> lvCommentaires;

    @Setter private Stage stage;
    @Setter private IClientService clientService;
    @Setter private ICommentaireService commentaireService;
    private Integer idClient;

    /**
     * Initialise la vue et configure les composants.
     * <p>
     * Notamment :
     * <ul>
     *     <li>TextArea pour nouveau commentaire avec ajustement automatique de hauteur</li>
     *     <li>ListView de commentaires avec {@link CommentaireCell}</li>
     * </ul>
     */
    @FXML
    public void initialize(){
        tfNouveauCommentaire.setWrapText(true);
        tfNouveauCommentaire.textProperty().addListener((obs, oldText, newText) ->
                tfNouveauCommentaire.setPrefHeight(computeTextAreaHeight(tfNouveauCommentaire)
        ));

        // Configurer la ListView avec un cellFactory
        lvCommentaires.setCellFactory(list -> new CommentaireCell(commentaireService, this::refreshClientData));
    }


    /**
     * Callback pour rafraîchir les données du client depuis un autre thread ou composant.
     * @param unused paramètre ignoré
     */
    private void refreshClientData(Void unused) {
        refreshClientData();
    }

    /**
     * Initialise le contrôleur avec l'identifiant du client.
     * @param idClient identifiant du client
     */
    public void initData(Integer idClient){
        this.idClient = idClient;
        refreshClientData();
    }

    /**
     * Rafraîchit l'affichage des informations du client et de ses commentaires.
     */
    private void refreshClientData(){

        Client client = clientService.getClientWithCommentsById(idClient);
        if (client == null) {
            return;
        }

        lblNom.setText(client.getNom());
        lblPrenom.setText(client.getPrenom());
        lblSociete.setText(client.getSociete());
        lblEmail.setText(getNdIfNull(client.getEmail()));
        lblTelephone.setText(getNdIfNull(client.getTelephone()));
        lblAdresse.setText(getNdIfNull(client.getAdresse()));
        lblVille.setText(getNdIfNull(client.getVille()));
        lblCodePostal.setText(getNdIfNull(client.getCodePostal()));
        lblPays.setText(getNdIfNull(client.getPays()));
        lblStatut.setText(client.getStatut().name());
        lblSegment.setText(client.getSegment() == null ? "ND" : client.getSegment().name());
        lblSourceAcquisition.setText(client.getSourceAcquisition() == null ? "ND" : client.getSourceAcquisition().name());
        lblPrenomAndNomTitle.setText(client.getPrenom() + " " + client.getNom());
        lblSocieteTitle.setText(client.getSociete());

        refreshCommentaires(client.getCommentaires());
    }

    /**
     * Met à jour la ListView des commentaires.
     * <p>
     * Si la liste est vide, un placeholder est affiché.
     * @param commentaires liste de commentaires
     */
    private void refreshCommentaires(List<Commentaire> commentaires){
        if(commentaires == null || commentaires.isEmpty()){
            lvCommentaires.setPlaceholder(createEmptyPlaceholder());
            lvCommentaires.getItems().clear();
        } else {
            lvCommentaires.setPlaceholder(null);
            lvCommentaires.getItems().setAll(commentaires);
        }
    }

    /**
     * Crée un placeholder affiché lorsque le client n'a aucun commentaire.
     * @return VBox contenant le message
     */
    private VBox createEmptyPlaceholder(){
        Label lbl = new Label("Aucun commentaire pour ce client.");
        lbl.getStyleClass().add("commentaire-empty");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);

        VBox box = new VBox(lbl);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Calcule dynamiquement la hauteur adaptée pour le TextArea du nouveau commentaire.
     * @param textArea le TextArea à mesurer
     * @return hauteur optimale
     */
    private double computeTextAreaHeight(TextArea textArea){
        Text text = new Text(textArea.getText());
        text.setFont(textArea.getFont());
        text.setWrappingWidth(textArea.getWidth() - 10);
        return text.getLayoutBounds().getHeight() + 20;
    }

    /**
     * Ajoute un nouveau commentaire au client.
     * Déclenché par le bouton "Ajouter".
     */
    @FXML
    private void handleAjouterCommentaire() {
        String text = tfNouveauCommentaire.getText();
        if(text != null && !text.isEmpty()){
            Commentaire c = new Commentaire();
            c.setCommentaire(text);
            c.setIdClient(idClient);
            commentaireService.save(c);

            tfNouveauCommentaire.clear();
            refreshClientData();
        }
    }

    /**
     * Ouvre la fenêtre de modification du client.
     */
    @FXML
    private void handleModifierClient() {
        Client client = clientService.getClientById(idClient);

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/modifier_client/modifier_client.fxml")
            );

            Parent root = loader.load();

            ControllerModifierClient controller = loader.getController();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier un client");
            stage.initModality(Modality.APPLICATION_MODAL);

            controller.setClientService(this.clientService);
            controller.setStage(stage);
            controller.initData(client);

            stage.showAndWait();

            refreshClientData();

        } catch (Exception e) {
            log.error("Erreur lors de l'ouverture de la fenetre de modification d'un client");
        }
    }

    /**
     * Ferme la fenêtre actuelle.
     */
    @FXML
    private void handleRetour() {
        stage.close();
    }

    /**
     * Ouvre la fenêtre listant les devis du client.
     * @param actionEvent événement déclencheur
     */
    @FXML
    private void handleVoirDevis(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/liste_devis/liste_devis.fxml"));
            Parent root = loader.load();

            final IDevisDao devisDao = new DevisDAO();
            final ICommercialDAO commercialDao = new CommercialDAO();
            final IDevisService devisService = new DevisService(devisDao, commercialDao);

            ControllerListeDevis controller = loader.getController();
            controller.setDevisService(devisService);
            controller.setIdClient(idClient);
            controller.setNomClient(lblNom.getText() + " " + lblPrenom.getText());
            controller.loadDevis();
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Liste des devis du client : " + lblNom.getText() + " " + lblPrenom.getText());
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (Exception e) {
            log.error("Erreur lors de l'ouverture de la fenetre de liste des devis du client");
        }
    }
}
