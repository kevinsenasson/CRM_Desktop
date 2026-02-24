package view.helper;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Commentaire;
import service.ICommentaireService;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;


/**
 * Cellule personnalisée pour l'affichage et l'édition d'un {@link Commentaire} dans une {@link ListView}.
 * <p>
 * Chaque cellule affiche :
 * <ul>
 *     <li>Le texte du commentaire</li>
 *     <li>La date de création (et éventuellement la date de modification)</li>
 *     <li>Les boutons "Modifier", "Supprimer" et "Annuler"</li>
 * </ul>
 * La cellule gère également l'état édition/lecture et déclenche la mise à jour via le service associé.
 */
public class CommentaireCell extends ListCell<Commentaire> {

    private final ICommentaireService service;
    private final Consumer<Void> refreshCallback;

    private VBox vboxContainer;
    private Label lblCommentaire;
    private Label lblDate;
    private TextArea tfEdit;
    private Button btnModifier, btnSupprimer, btnAnnuler;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructeur de la cellule.
     *
     * @param service         le service de gestion des commentaires pour effectuer les mises à jour/suppressions
     * @param refreshCallback callback à appeler pour rafraîchir la ListView après modification
     */
    public CommentaireCell(ICommentaireService service, Consumer<Void> refreshCallback){
        this.service = service;
        this.refreshCallback = refreshCallback;
    }

    /**
     * Met à jour l'affichage de la cellule en fonction de l'élément fourni.
     *
     * @param c     le commentaire à afficher
     * @param empty true si la cellule est vide
     */
    @Override
    protected void updateItem(Commentaire c, boolean empty){
        super.updateItem(c, empty);
        if(empty || c == null){
            setGraphic(null);
            setStyle("");
            return;
        }

        if(vboxContainer == null){
            createCellGraphic();
        }

        lblCommentaire.setText(c.getCommentaire());
        lblDate.setText(formatCommentDate(c));

        btnModifier.setOnAction(e -> toggleEdit(c));
        btnSupprimer.setOnAction(e -> {
            service.deleteCommentaireById(c.getId());
            refreshCallback.accept(null);
        });
        btnAnnuler.setOnAction(e -> cancelEdit(c));

        setGraphic(vboxContainer);
    }

    /**
     * Formate la date du commentaire avec la date de modification si elle existe.
     *
     * @param c le commentaire
     * @return chaîne formatée pour affichage
     */
    private String formatCommentDate(Commentaire c){
        String modification = c.getDateDerniereModification() == null ? "" :
                " (Modifié le " + c.getDateDerniereModification().format(formatter) + ")";
        return c.getDate().format(formatter) + modification;
    }

    /**
     * Crée le contenu graphique de la cellule, initialisant labels, textArea et boutons.
     */
    private void createCellGraphic(){
        // --- Label commentaire ---
        lblCommentaire = new Label();
        lblCommentaire.getStyleClass().add("commentaire-label");
        lblCommentaire.setWrapText(true);
        lblCommentaire.setMaxWidth(Double.MAX_VALUE);
        lblCommentaire.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(lblCommentaire, Priority.ALWAYS);

        // --- Label date ---
        lblDate = new Label();
        lblDate.getStyleClass().add("commentaire-date");
        lblDate.setWrapText(true);
        lblDate.setPrefWidth(0);

        // --- TextArea édition ---
        tfEdit = new TextArea();
        tfEdit.setPrefRowCount(3);
        tfEdit.setWrapText(true);
        tfEdit.setMaxWidth(Double.MAX_VALUE);
        tfEdit.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(tfEdit, Priority.ALWAYS);
        tfEdit.setStyle("-fx-font-size: 14px; -fx-padding: 0;");
        tfEdit.setEditable(false);
        tfEdit.setVisible(false);
        tfEdit.setManaged(false);

        // --- Buttons ---
        btnModifier = new Button("Modifier");
        btnModifier.getStyleClass().add("commentaire-btn-modifier");

        btnSupprimer = new Button("Supprimer");
        btnSupprimer.getStyleClass().add("commentaire-btn-supprimer");

        btnAnnuler = new Button("Annuler");
        btnAnnuler.getStyleClass().add("commentaire-btn-annuler");
        btnAnnuler.setVisible(false);
        btnAnnuler.setManaged(false);

        // --- HBox actions ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox hboxActions = new HBox(10, lblDate, spacer, btnSupprimer, btnModifier, btnAnnuler);
        hboxActions.setAlignment(Pos.CENTER_LEFT);
        hboxActions.setMaxWidth(Double.MAX_VALUE);

        // --- VBox container ---
        vboxContainer = new VBox(5, lblCommentaire, tfEdit, hboxActions);
        vboxContainer.getStyleClass().add("commentaire-container");
        vboxContainer.setMaxWidth(Double.MAX_VALUE);
        vboxContainer.setMaxHeight(Double.MAX_VALUE);
    }

    /**
     * Active ou désactive le mode édition du commentaire.
     *
     * @param c le commentaire à éditer
     */
    private void toggleEdit(Commentaire c){
        if(btnModifier.getText().equals("Modifier")){
            tfEdit.setText(c.getCommentaire());
            tfEdit.setEditable(true);
            tfEdit.setVisible(true);
            tfEdit.setManaged(true);

            lblCommentaire.setVisible(false);

            btnAnnuler.setVisible(true);
            btnAnnuler.setManaged(true);

            btnModifier.setText("Sauvegarder");
            btnModifier.getStyleClass().remove("commentaire-btn-modifier");
            if(!btnModifier.getStyleClass().contains("commentaire-btn-sauvegarder")){
                btnModifier.getStyleClass().add("commentaire-btn-sauvegarder");
            }

            btnSupprimer.setDisable(true);
        } else {
            String newText = tfEdit.getText();
            if(!newText.equals(c.getCommentaire())){
                c.setCommentaire(newText);
                service.updateCommentaire(c);
            }
            cancel();
        }
    }

    /**
     * Annule le mode édition et restaure le texte original.
     *
     * @param c le commentaire
     */
    private void cancelEdit(Commentaire c){
        tfEdit.setText(c.getCommentaire());
        cancel();

    }

    // Annule mode édition
    private void cancel() {
        tfEdit.setEditable(false);
        tfEdit.setVisible(false);
        tfEdit.setManaged(false);

        lblCommentaire.setVisible(true);

        btnAnnuler.setVisible(false);
        btnAnnuler.setManaged(false);

        btnModifier.setText("Modifier");
        btnModifier.getStyleClass().remove("commentaire-btn-sauvegarder");
        if(!btnModifier.getStyleClass().contains("commentaire-btn-modifier")){
            btnModifier.getStyleClass().add("commentaire-btn-modifier");
        }

        btnSupprimer.setDisable(false);
    }
}