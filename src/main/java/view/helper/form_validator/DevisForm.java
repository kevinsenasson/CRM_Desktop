package view.helper.form_validator;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import validation.ValidationPattern;

import static validation.rule.AmountRule.isPositiveAmount;
import static validation.rule.LengthRule.*;
import static validation.rule.RegexRule.matches;
import static view.helper.form_validator.UtilsForm.*;
import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire pour la validation des formulaires de devis.
 *
 * <p>
 * Fournit des méthodes pour :
 * <ul>
 *     <li>Lier les champs JavaFX (TextField, DatePicker) à des règles de validation</li>
 *     <li>Afficher dynamiquement les messages d'erreur dans des {@link Label}</li>
 *     <li>Désactiver le {@link Button} d'enregistrement tant que le formulaire est invalide</li>
 * </ul>
 * </p>
 *
 * <p>
 * Les validations incluent :
 * <ul>
 *     <li>Référence : obligatoire, longueur minimale 1 et maximale 25</li>
 *     <li>Date de création : obligatoire, format valide selon {@link ValidationPattern#DATE_REGEX}</li>
 *     <li>Montant : obligatoire, montant positif et conforme au regex {@link ValidationPattern#MONTANT_REGEX}</li>
 * </ul>
 * </p>
 */
public class DevisForm {

    /**
     * Crée un {@link BooleanBinding} représentant la validité du formulaire de devis.
     *
     * <p>
     * Le formulaire est considéré valide si :
     * <ul>
     *     <li>Référence non vide et longueur ≤ 25</li>
     *     <li>Date de création non nulle et valide</li>
     *     <li>Montant positif et conforme au format défini</li>
     * </ul>
     * </p>
     *
     * @param reference   TextField de la référence du devis
     * @param dateCreation DatePicker de la date de création
     * @param montant     TextField du montant du devis
     * @return BooleanBinding reflétant la validité du formulaire (true si valide)
     */
    private static BooleanBinding createFormValidBindingForDevis(
            TextField reference,
            DatePicker dateCreation,
            TextField montant
    ) {

        return createFormBinding(
                () ->
                        isNotBlankMin(reference.getText(), 1) &&
                        isMaxLength(reference.getText(), 25) &&
                        dateCreation.getValue() != null &&
                        matches(dateCreation.getValue().toString(), ValidationPattern.DATE_REGEX) &&
                        isPositiveAmount(montant.getText(), ValidationPattern.MONTANT_REGEX),

                reference.textProperty(),
                dateCreation.valueProperty(),
                montant.textProperty()
        );
    }

    /**
     * Configure la validation complète du formulaire de devis.
     *
     * <p>
     * Cette méthode :
     * <ul>
     *     <li>Lie chaque champ à ses règles de validation (obligatoire, longueur, regex, montant positif)</li>
     *     <li>Met à jour les {@link Label} d'erreur en temps réel</li>
     *     <li>Désactive le {@link Button} d'enregistrement tant que le formulaire est invalide</li>
     * </ul>
     * </p>
     *
     * @param reference         TextField de la référence du devis
     * @param dateCreation      DatePicker de la date de création
     * @param montant           TextField du montant du devis
     * @param lblErrorReference Label pour l'erreur de la référence
     * @param lblErrorDateCreation Label pour l'erreur de la date de création
     * @param lblErrorMontant   Label pour l'erreur du montant
     * @param btnEnregistrer    Bouton à activer/désactiver selon la validité du formulaire
     */
    public static void validateDevisForm(TextField reference,
                                         DatePicker dateCreation,
                                         TextField montant,
                                         Label lblErrorReference,
                                         Label lblErrorDateCreation,
                                         Label lblErrorMontant,
                                         Button btnEnregistrer) {

        bindField(reference, lblErrorReference, mandatory("La référence est obligatoire"), length(1, 25));
        bindField(montant, lblErrorMontant, mandatory("Le montant est obligatoire"), regex(ValidationPattern.MONTANT_REGEX, "Montant invalide"));
        bindDatePicker(dateCreation, lblErrorDateCreation, mandatory("La date est obligatoire"), regex(ValidationPattern.DATE_REGEX, "Date invalide"));

        btnEnregistrer.disableProperty().bind(createFormValidBindingForDevis(
                reference,
                dateCreation,
                montant
                )
                .not()
        );
    }
}
