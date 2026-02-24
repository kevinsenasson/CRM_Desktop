package view.helper;

import exception.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import lombok.extern.slf4j.Slf4j;

/**
 * Gestion centralisée des erreurs pour l'interface JavaFX.
 *
 * <p>
 * Fournit des méthodes pour afficher des messages d'erreur soit dans un {@link Label},
 * soit via un {@link Alert} standard. Les erreurs sont loggées avec SLF4J.
 * </p>
 *
 * <p>
 * Cette classe est utilitaire et ne peut pas être instanciée.
 * </p>
 */
@Slf4j
public final class UiErrorHandler {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private UiErrorHandler(){}

    /**
     * Traite une exception et décide comment l'afficher à l'utilisateur.
     *
     * <ul>
     *     <li>Les {@link AppAssertException} sont affichées dans un {@link Label}</li>
     *     <li>Les {@link IllegalArgumentException} déclenchent un {@link AlertType#WARNING}</li>
     *     <li>Toutes les autres exceptions déclenchent un {@link AlertType#ERROR}</li>
     * </ul>
     *
     * @param e     exception à traiter
     * @param label {@link Label} pour afficher les messages d'erreur d'assertion
     */
    public static void handle(Exception e, Label label) {
        if (isAssertException(e)) {
            log.warn("Erreur d'assertion", e);
            showInLabel(e.getMessage(), label);
        } else if (e instanceof IllegalArgumentException) {
            log.warn("Erreur d'argument", e);
            showAlert(AlertType.WARNING, "Erreur d'argument", "Une erreur est survenu", "Merci de vouloir fermer l'application et recommencer.");
        } else {
            log.error("Erreur inattendue", e);
            showAlert(AlertType.ERROR, "Erreur inattendue", "Une erreur critique est survenu", "Merci de vouloir fermer l'application et recommencer. Si le problème persiste, contactez l'administrateur.");
        }
    }


    /**
     * Affiche un message d'erreur dans un {@link Label}.
     *
     * @param message message à afficher
     * @param label   {@link Label} cible
     */
    public static void showInLabel(String message, Label label) {
        label.setText(message);
        label.setVisible(true);
    }

    /**
     * Efface le contenu d'un {@link Label} d'erreur et le rend invisible.
     *
     * @param label {@link Label} cible
     */
    public static void clear(Label label) {
        label.setText("");
        label.setVisible(false);
    }

    /**
     * Vérifie si une exception est une exception d'assertion de l'application.
     *
     * @param e exception à tester
     * @return true si c'est une {@link AppAssertException}, false sinon
     */
    private static boolean isAssertException(Exception e) {
        return e instanceof AppAssertException;
    }

    /**
     * Affiche une boîte de dialogue {@link Alert} avec les informations fournies.
     *
     * @param type    type de l'alerte ({@link AlertType})
     * @param title   titre de la fenêtre
     * @param header  texte d'en-tête
     * @param content contenu du message
     */
    private static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
