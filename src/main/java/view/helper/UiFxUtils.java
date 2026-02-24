package view.helper;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Classe utilitaire pour gérer des composants JavaFX et interactions UI.
 *
 * <p>
 * Fournit des méthodes statiques pour :
 * </p>
 * <ul>
 *     <li>Gestion de valeurs nulles ou vides</li>
 *     <li>Ajustement automatique des colonnes d'un {@link TableView}</li>
 *     <li>Animations de slide entre pages dans un {@link StackPane}</li>
 *     <li>Création de champs éditables dynamiques dans un {@link VBox}</li>
 * </ul>
 *
 * <p>
 * Cette classe est final et ne peut pas être instanciée.
 * </p>
 */
@Slf4j
public final class UiFxUtils {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private UiFxUtils() {}

    /**
     * Retourne "ND" si la valeur est nulle ou vide.
     *
     * @param value la chaîne à vérifier
     * @return "ND" si null ou vide, sinon la valeur
     */
    public static String getNdIfNull(String value) {
        return (value == null || value.isBlank()) ? "ND" : value;
    }

    /**
     * Ajuste automatiquement la largeur des colonnes d'un {@link TableView} en fonction d'un pourcentage.
     *
     * @param table {@link TableView} à configurer
     * @param width tableau des pourcentages de largeur (doit correspondre au nombre de colonnes)
     */
    public static void setWidthTableView(TableView<?> table, double[] width) {
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        table.widthProperty().addListener((obs, oldVal, newVal) -> {
            double totalWidth = newVal.doubleValue() - 2; // ajustement bordure
            for (int i = 0; i < table.getColumns().size(); i++) {
                TableColumn<?, ?> col = table.getColumns().get(i);
                col.setPrefWidth(totalWidth * width[i]);
                col.setStyle("-fx-alignment: CENTER;");
            }
        });
    }


    /**
     * Anime le passage d'une page à une autre dans un {@link StackPane} en sliding horizontal.
     *
     * @param container   {@link StackPane} contenant les pages
     * @param newPage     nouvelle page à afficher
     * @param slideFromRight si true, la nouvelle page vient de la droite, sinon de la gauche
     */
    public static void slideTo(
            StackPane container,
            Parent newPage,
            boolean slideFromRight
    ) {

        if (container.getChildren().isEmpty()) {
            container.getChildren().add(newPage);
            return;
        }

        Parent currentPage = (Parent) container.getChildren().getFirst();

        double width = container.getWidth();

        // Positionne la nouvelle page hors écran
        newPage.setTranslateX(slideFromRight ? width : -width);
        container.getChildren().add(newPage);

        // Animation page actuelle vers l'extérieur
        TranslateTransition currentSlide = new TranslateTransition(Duration.millis(300), currentPage);
        currentSlide.setToX(slideFromRight ? -width : width);

        // Animation nouvelle page vers le centre
        TranslateTransition newSlide = new TranslateTransition(Duration.millis(300), newPage);
        newSlide.setToX(0);

        ParallelTransition animation = new ParallelTransition(currentSlide, newSlide);

        animation.setOnFinished(e -> container.getChildren().remove(currentPage));

        animation.play();
    }

    /**
     * Ajoute dynamiquement un champ éditable dans un {@link VBox}.
     *
     * <p>
     * Le champ affiche un label et la valeur actuelle. Un bouton permet
     * de passer en mode édition avec un {@link TextField} et un bouton de sauvegarde.
     * </p>
     *
     * @param container  {@link VBox} où ajouter le champ
     * @param labelText  texte du label du champ
     * @param value      valeur initiale du champ
     * @param setter     fonction consommant la nouvelle valeur pour mise à jour
     * @param saveAction action optionnelle à exécuter après validation
     * @param errorLabel {@link Label} pour afficher un message d'erreur
     */
    public static void addEditableField(VBox container,
                                        String labelText,
                                        String value,
                                        Consumer<String> setter,
                                        Runnable saveAction,
                                        Label errorLabel) {

        // Création de la Hbox pour le champ
        HBox row = new HBox(15);
        row.setStyle("-fx-background-color:white; -fx-padding:15; -fx-background-radius:8;");
        row.setAlignment(Pos.CENTER_LEFT);

        // Création des composants du champ
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight:bold;");

        // label
        Label valueLabel = new Label(value == null ? "ND" : value);
        HBox.setHgrow(valueLabel, Priority.ALWAYS);

        // Icon d'édition
        ImageView editIcon = new ImageView(
                new Image(Objects.requireNonNull(UiFxUtils.class.getResourceAsStream("/view/assets/icons/edit.png")))
        );
        editIcon.setFitWidth(16);
        editIcon.setFitHeight(16);

        // Button d'édition
        Button editBtn = new Button();
        editBtn.setGraphic(editIcon);
        editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

        // Ajout des composants au Hbox
        row.getChildren().addAll(label, valueLabel, editBtn);
        container.getChildren().add(row);

        // Animation du passage en mode édition
        editBtn.setOnAction(e -> {
            String valueForField = valueLabel.getText().replace("€", "");
            TextField tf = new TextField(valueForField);
            Button saveBtn = new Button("✔");
            saveBtn.setStyle("-fx-background-color:#2ecc71; -fx-text-fill:white;");

            row.getChildren().clear();
            row.getChildren().addAll(label, tf, saveBtn);
            HBox.setHgrow(tf, Priority.ALWAYS);

            // Action de sauvegarde et passage en mode lecture
            saveBtn.setOnAction(ev -> {
                String newValue = tf.getText();
                try {
                    setter.accept(newValue);
                } catch (Exception ex) {
                    log.warn("Erreur de saisie pour {}", labelText, ex);
                    errorLabel.setVisible(true);
                    errorLabel.setText("Valeur invalide pour " + labelText);
                    row.getChildren().clear();
                    row.getChildren().addAll(label, valueLabel, editBtn);
                    return;
                }
                if (saveAction != null) saveAction.run();
                valueLabel.setText(newValue + (labelText.toLowerCase().contains("ca") ? " €" : ""));
                row.getChildren().clear();
                row.getChildren().addAll(label, valueLabel, editBtn);
            });
        });
    }
}
