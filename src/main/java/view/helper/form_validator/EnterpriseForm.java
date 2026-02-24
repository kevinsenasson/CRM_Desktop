package view.helper.form_validator;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import validation.ValidationPattern;

import static validation.rule.LengthRule.*;
import static validation.rule.LuhnValidator.isSiretValidForLuhn;
import static validation.rule.RegexRule.matches;
import static validation.rule.SiretRule.siret;
import static view.helper.form_validator.UtilsForm.*;
import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire pour la validation des formulaires d'entreprise.
 *
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 *     <li>Lier les champs JavaFX (TextField) à des règles de validation</li>
 *     <li>Afficher dynamiquement les messages d'erreur dans des {@link Label}</li>
 *     <li>Désactiver le {@link Button} d'enregistrement tant que le formulaire est invalide</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les validations incluent :
 * <ul>
 *     <li>Nom : obligatoire, longueur minimale 2 et maximale 40</li>
 *     <li>Adresse : obligatoire, longueur minimale 2 et maximale 50</li>
 *     <li>Code postal : obligatoire, exactement 5 chiffres</li>
 *     <li>Ville : obligatoire, longueur minimale 2 et maximale 50</li>
 *     <li>Email : format valide selon {@link ValidationPattern#EMAIL_REGEX}</li>
 *     <li>Téléphone : format valide selon {@link ValidationPattern#PHONE_REGEX}</li>
 *     <li>SIRET : 14 chiffres valides selon l'algorithme de Luhn</li>
 * </ul>
 * </p>
 */
public class EnterpriseForm {

    /**
     * Crée un {@link BooleanBinding} représentant la validité du formulaire d'entreprise.
     *
     * <p>
     * Le formulaire est considéré valide si tous les champs obligatoires sont remplis
     * et respectent leurs règles de format et de longueur, et si le SIRET est valide.
     * </p>
     *
     * @param tfNom TextField du nom de l'entreprise
     * @param tfAdresse TextField de l'adresse
     * @param tfCodePostal TextField du code postal
     * @param tfVille TextField de la ville
     * @param tfEmail TextField de l'email
     * @param tfTelephone TextField du téléphone
     * @param tfSiret TextField du SIRET
     * @return BooleanBinding reflétant la validité du formulaire (true si valide)
     */
    private static BooleanBinding createFormValidForEnterprise(
            TextField tfNom,
            TextField tfAdresse,
            TextField tfCodePostal,
            TextField tfVille,
            TextField tfEmail,
            TextField tfTelephone,
            TextField tfSiret
    ) {

        return createFormBinding(
                () ->
                        isNotBlankMin(tfNom.getText(), 2) &&
                        isMaxLength(tfNom.getText(), 40) &&
                        isNotBlankMin(tfAdresse.getText(), 2) &&
                        isMaxLength(tfAdresse.getText(), 50) &&
                        isNotBlankMin(tfCodePostal.getText(), 5) &&
                        isMaxLength(tfCodePostal.getText(), 5) &&
                        matches(tfCodePostal.getText(), ValidationPattern.CODE_POSTAL_REGEX) &&
                        isNotBlankMin(tfVille.getText(), 2) &&
                        isMaxLength(tfVille.getText(), 50) &&
                        isNotBlankMin(tfEmail.getText(), 5) &&
                        isMaxLength(tfEmail.getText(), 100) &&
                        matches(tfEmail.getText(), ValidationPattern.EMAIL_REGEX) &&
                        isNotBlankMin(tfTelephone.getText(), 10) &&
                        isMaxLength(tfTelephone.getText(), 10) &&
                        matches(tfTelephone.getText(), ValidationPattern.PHONE_REGEX) &&
                        isNotBlankMin(tfSiret.getText(), 14) &&
                        isMaxLength(tfSiret.getText(), 14) &&
                        isSiretValidForLuhn(tfSiret.getText()),

                tfNom.textProperty(),
                tfAdresse.textProperty(),
                tfVille.textProperty(),
                tfCodePostal.textProperty(),
                tfEmail.textProperty(),
                tfTelephone.textProperty(),
                tfSiret.textProperty()
        );
    }

    /**
     * Configure la validation complète du formulaire d'entreprise.
     *
     * <p>
     * Cette méthode :
     * <ul>
     *     <li>Lie chaque champ à ses règles de validation (obligatoire, longueur, regex, SIRET valide)</li>
     *     <li>Met à jour les {@link Label} d'erreur en temps réel</li>
     *     <li>Désactive le {@link Button} d'enregistrement tant que le formulaire est invalide</li>
     * </ul>
     * </p>
     *
     * @param tfNom TextField du nom de l'entreprise
     * @param tfAdresse TextField de l'adresse
     * @param tfCodePostal TextField du code postal
     * @param tfVille TextField de la ville
     * @param tfEmail TextField de l'email
     * @param tfTelephone TextField du téléphone
     * @param tfSiret TextField du SIRET
     * @param lblErrorNom Label pour l'erreur du nom
     * @param lblErrorAdresse Label pour l'erreur de l'adresse
     * @param lblErrorCodePostal Label pour l'erreur du code postal
     * @param lblErrorVille Label pour l'erreur de la ville
     * @param lblErrorEmail Label pour l'erreur de l'email
     * @param lblErrorTelephone Label pour l'erreur du téléphone
     * @param lblErrorSiret Label pour l'erreur du SIRET
     * @param btnEnregistrer Bouton à activer/désactiver selon la validité du formulaire
     */
    public static void validateEnterpriseForm(TextField tfNom,
                                              TextField tfAdresse,
                                              TextField tfCodePostal,
                                              TextField tfVille,
                                              TextField tfEmail,
                                              TextField tfTelephone,
                                              TextField tfSiret,
                                              Label lblErrorNom,
                                              Label lblErrorAdresse,
                                              Label lblErrorCodePostal,
                                              Label lblErrorVille,
                                              Label lblErrorEmail,
                                              Label lblErrorTelephone,
                                              Label lblErrorSiret,
                                              Button btnEnregistrer) {

        bindField(tfNom, lblErrorNom, mandatory("Le nom est obligatoire"), length(2, 40));
        bindField(tfAdresse, lblErrorAdresse, mandatory("L'adresse est obligatoire"), length(2, 50));
        bindField(tfCodePostal, lblErrorCodePostal, mandatory("Le code postal est obligatoire"), length(5, 5), regex(ValidationPattern.CODE_POSTAL_REGEX, "Code postal invalide"));
        bindField(tfVille, lblErrorVille, mandatory("La ville est obligatoire"), length(2, 50));
        bindField(tfEmail, lblErrorEmail, regex(ValidationPattern.EMAIL_REGEX, "Email invalide"), length(5, 100));
        bindField(tfTelephone, lblErrorTelephone, regex(ValidationPattern.PHONE_REGEX, "Téléphone invalide"), length(10, 10));
        bindField(tfSiret, lblErrorSiret, siret());

        btnEnregistrer.disableProperty().bind(createFormValidForEnterprise(tfNom,
                tfAdresse,
                tfCodePostal,
                tfVille,
                tfEmail,
                tfTelephone,
                tfSiret
                )
                .not());
    }
}
