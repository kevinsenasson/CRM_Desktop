package view.helper.form_validator;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.StatutClient;
import validation.ValidationPattern;

import static validation.rule.LengthRule.isNotBlankMin;
import static validation.rule.RegexRule.matches;
import static view.helper.form_validator.UtilsForm.*;
import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.LengthRule.length;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire pour la validation des formulaires clients.
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 *     <li>Lier les champs JavaFX (TextField, ComboBox) à des règles de validation</li>
 *     <li>Afficher dynamiquement les erreurs via des {@link Label}</li>
 *     <li>Contrôler l'activation d'un {@link Button} en fonction de la validité globale du formulaire</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les validations incluent : champs obligatoires, longueurs minimales, format regex pour email, téléphone et code postal.
 * </p>
 */
public class ClientForm {

    /**
     * Crée un {@link BooleanBinding} représentant la validité d'un formulaire client.
     * <p>
     * Le formulaire est considéré valide si :
     * <ul>
     *     <li>Nom, prénom et société ont au moins 2 caractères</li>
     *     <li>Un statut est sélectionné</li>
     *     <li>Email vide ou conforme au regex {@link ValidationPattern#EMAIL_REGEX}</li>
     *     <li>Téléphone vide ou conforme au regex {@link ValidationPattern#PHONE_REGEX}</li>
     *     <li>Code postal vide ou conforme au regex {@link ValidationPattern#CODE_POSTAL_REGEX}</li>
     * </ul>
     * </p>
     *
     * @param tfNom        TextField du nom
     * @param tfPrenom     TextField du prénom
     * @param tfSociete    TextField de la société
     * @param cbStatut     ComboBox du statut client
     * @param tfEmail      TextField de l'email
     * @param tfTelephone  TextField du téléphone
     * @param tfCodePostal TextField du code postal
     * @return BooleanBinding reflétant la validité du formulaire (true si valide)
     */
    private static BooleanBinding createFormValidBindingForClient(
            TextField tfNom,
            TextField tfPrenom,
            TextField tfSociete,
            ComboBox<?> cbStatut,
            TextField tfEmail,
            TextField tfTelephone,
            TextField tfCodePostal
    ) {

        return UtilsForm.createFormBinding(
                () ->
                        isNotBlankMin(tfNom.getText(), 2) &&
                        isNotBlankMin(tfPrenom.getText(), 2) &&
                        isNotBlankMin(tfSociete.getText(), 2) &&
                        cbStatut.getValue() != null &&
                        (tfEmail.getText().isBlank() || matches(tfEmail.getText(), ValidationPattern.EMAIL_REGEX)) &&
                        (tfTelephone.getText().isBlank() || matches(tfTelephone.getText(), ValidationPattern.PHONE_REGEX)) &&
                        (tfCodePostal.getText().isBlank() || matches(tfCodePostal.getText(), ValidationPattern.CODE_POSTAL_REGEX)),

                tfNom.textProperty(),
                tfPrenom.textProperty(),
                tfSociete.textProperty(),
                cbStatut.valueProperty(),
                tfEmail.textProperty(),
                tfTelephone.textProperty(),
                tfCodePostal.textProperty()
        );
    }

    /**
     * Configure la validation complète d'un formulaire client simple.
     * <p>
     * Cette méthode :
     * <ul>
     *     <li>Lie chaque champ à ses règles de validation (mandatory, longueur, regex)</li>
     *     <li>Met à jour les {@link Label} d'erreur en temps réel</li>
     *     <li>Désactive le bouton {@link Button} d'enregistrement tant que le formulaire n'est pas valide</li>
     * </ul>
     * </p>
     *
     * @param tfNom TextField du nom
     * @param tfPrenom TextField du prénom
     * @param tfSociete TextField de la société
     * @param statut ComboBox du statut client
     * @param tfEmail TextField de l'email
     * @param tfTelephone TextField du téléphone
     * @param tfCodePostal TextField du code postal
     * @param lblErrorNom Label pour afficher l'erreur du nom
     * @param lblErrorPrenom Label pour afficher l'erreur du prénom
     * @param lblErrorSociete Label pour afficher l'erreur de la société
     * @param lblErrorEmail Label pour afficher l'erreur de l'email
     * @param lblErrorTelephone Label pour afficher l'erreur du téléphone
     * @param lblErrorStatut Label pour afficher l'erreur du statut
     * @param lblErrorCodePostal Label pour afficher l'erreur du code postal
     * @param btnEnregistrer Bouton à activer/désactiver selon la validité du formulaire
     */
    public static void validateClientForm(TextField tfNom,
                                          TextField tfPrenom,
                                          TextField tfSociete,
                                          ComboBox<StatutClient> statut,
                                          TextField tfEmail,
                                          TextField tfTelephone,
                                          TextField tfCodePostal,
                                          Label lblErrorNom,
                                          Label lblErrorPrenom,
                                          Label lblErrorSociete,
                                          Label lblErrorEmail,
                                          Label lblErrorTelephone,
                                          Label lblErrorStatut,
                                          Label lblErrorCodePostal,
                                          Button btnEnregistrer) {

        bindField(tfNom, lblErrorNom, mandatory("Le nom est obligatoire"), length(2, 25));
        bindField(tfPrenom, lblErrorPrenom, mandatory("Le prénom est obligatoire"), length(2, 30));
        bindField(tfSociete, lblErrorSociete, mandatory("Le nom de société est obligatoire"), length(2, 50));
        bindField(tfEmail, lblErrorEmail, regex(ValidationPattern.EMAIL_REGEX, "Email invalide"));
        bindField(tfTelephone, lblErrorTelephone, regex(ValidationPattern.PHONE_REGEX, "Téléphone invalide"));
        bindField(tfCodePostal, lblErrorCodePostal, regex(ValidationPattern.CODE_POSTAL_REGEX, "Code postal invalide"));
        bindComboBox(statut, lblErrorStatut, mandatory("Le statut est obligatoire"));

        btnEnregistrer.disableProperty().bind(createFormValidBindingForClient(
                tfNom,
                tfPrenom,
                tfSociete,
                statut,
                tfEmail,
                tfTelephone,
                tfCodePostal
                )
                .not()
        );
    }
}
