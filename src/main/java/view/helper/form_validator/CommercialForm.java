package view.helper.form_validator;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import validation.ValidationPattern;

import static validation.rule.AmountRule.isPositiveAmount;
import static validation.rule.LengthRule.*;
import static validation.rule.RegexRule.*;
import static view.helper.form_validator.UtilsForm.*;
import static validation.rule.MandatoryRule.mandatory;

/**
 * Classe utilitaire pour la validation des formulaires de commerciaux.
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
 *     <li>Nom et prénom : obligatoires, longueur minimale et maximale</li>
 *     <li>Email et téléphone : obligatoires, conformes aux regex</li>
 *     <li>Date d'embauche : obligatoire, format valide</li>
 *     <li>Objectif de chiffre d'affaires : obligatoire, montant positif et format valide</li>
 * </ul>
 * </p>
 */
public class CommercialForm {

    /**
     * Crée un {@link BooleanBinding} représentant la validité du formulaire commercial.
     *
     * <p>
     * Le formulaire est considéré valide si :
     * <ul>
     *     <li>Nom et prénom ont au moins 2 caractères et maximum 30</li>
     *     <li>Email et téléphone respectent les regex définies dans {@link ValidationPattern}</li>
     *     <li>Date d'embauche n'est pas nulle et correspond au regex de date</li>
     *     <li>Objectif CA est un montant positif et correspond au regex {@link ValidationPattern#OBJ_CA_REGEX}</li>
     * </ul>
     * </p>
     *
     * @param tfNom          TextField du nom
     * @param tfPrenom       TextField du prénom
     * @param tfEmail        TextField de l'email
     * @param tfTelephone    TextField du téléphone
     * @param dtDateEmbauche DatePicker de la date d'embauche
     * @param tfObjCa        TextField de l'objectif CA
     * @return BooleanBinding reflétant la validité du formulaire (true si valide)
     */
    private static BooleanBinding createFormValidBindingForCommercial(
            TextField tfNom,
            TextField tfPrenom,
            TextField tfEmail,
            TextField tfTelephone,
            DatePicker dtDateEmbauche,
            TextField tfObjCa
    ) {

        return createFormBinding(
                () ->
                        isNotBlankMin(tfNom.getText(), 2) &&
                        isMaxLength(tfNom.getText(), 30) &&

                        isNotBlankMin(tfPrenom.getText(), 2) &&
                        isMaxLength(tfPrenom.getText(), 30) &&

                        matches(tfEmail.getText(), ValidationPattern.EMAIL_REGEX) &&
                        matches(tfTelephone.getText(), ValidationPattern.PHONE_REGEX) &&
                        dtDateEmbauche.getValue() != null &&
                        matches(dtDateEmbauche.getValue().toString(), ValidationPattern.DATE_REGEX) &&

                        isPositiveAmount(tfObjCa.getText(), ValidationPattern.OBJ_CA_REGEX),

                tfNom.textProperty(),
                tfPrenom.textProperty(),
                tfEmail.textProperty(),
                tfTelephone.textProperty(),
                dtDateEmbauche.valueProperty(),
                tfObjCa.textProperty()
        );
    }

    /**
     * Configure la validation complète du formulaire commercial.
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
     * @param tfNom             TextField du nom
     * @param tfPrenom          TextField du prénom
     * @param tfEmail           TextField de l'email
     * @param tfTelephone       TextField du téléphone
     * @param dtDateEmbauche    DatePicker de la date d'embauche
     * @param tfObjCa           TextField de l'objectif CA
     * @param lblErrorNom       Label pour l'erreur du nom
     * @param lblErrorPrenom    Label pour l'erreur du prénom
     * @param lblErrorEmail     Label pour l'erreur de l'email
     * @param lblErrorTelephone Label pour l'erreur du téléphone
     * @param lblErrorDateEmbauche Label pour l'erreur de la date d'embauche
     * @param lblErrorObjCa     Label pour l'erreur de l'objectif CA
     * @param btnEnregistrer    Bouton à activer/désactiver selon la validité du formulaire
     */
    public static void validateCommercialForm(TextField tfNom,
                                       TextField tfPrenom,
                                       TextField tfEmail,
                                       TextField tfTelephone,
                                       DatePicker dtDateEmbauche,
                                       TextField tfObjCa,
                                       Label lblErrorNom,
                                       Label lblErrorPrenom,
                                       Label lblErrorEmail,
                                       Label lblErrorTelephone,
                                       Label lblErrorDateEmbauche,
                                       Label lblErrorObjCa,
                                       Button btnEnregistrer) {

        bindField(tfNom, lblErrorNom, mandatory("Le nom est obligatoire"), length(2, 30));
        bindField(tfPrenom, lblErrorPrenom, mandatory("le prénom est obligatoire"), length(2, 30));
        bindField(tfEmail, lblErrorEmail, mandatory("l'email est obligatoire"),  regex(ValidationPattern.EMAIL_REGEX, "le mail est invalide"));
        bindField(tfTelephone, lblErrorTelephone, mandatory("le téléphone est obligatoire") ,  regex(ValidationPattern.PHONE_REGEX,  "le téléphone est invalide"));
        bindField(tfObjCa, lblErrorObjCa, regex(ValidationPattern.OBJ_CA_REGEX, "le CA est invalide"), mandatory("le CA est obligatoire"));
        bindDatePicker(dtDateEmbauche, lblErrorDateEmbauche, regex(ValidationPattern.DATE_REGEX, "la date d'embauche est invalide"), mandatory("la date d'embauche est obligatoire"));


        btnEnregistrer.disableProperty().bind(createFormValidBindingForCommercial(
                tfNom,
                tfPrenom,
                tfEmail, tfTelephone,
                dtDateEmbauche,
                tfObjCa
                )
                .not()
        );
    }
}
