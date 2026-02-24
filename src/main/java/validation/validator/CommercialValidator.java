package validation.validator;

import exception.CommercialAssertException;
import model.Commercial;
import validation.ValidationPattern;
import validation.rule.ValidationRule;

import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.LengthRule.length;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire responsable de la validation des objets {@link Commercial}.
 *
 * <p>
 * Cette classe applique un ensemble de règles de validation sur chaque champ
 * d'un commercial et lève une {@link CommercialAssertException} dès qu'une règle échoue.
 * </p>
 *
 * <p>
 * Les règles utilisées incluent :
 * </p>
 * <ul>
 *     <li>Champs obligatoires via {@link validation.rule.MandatoryRule}</li>
 *     <li>Longueurs minimales et maximales via {@link validation.rule.LengthRule}</li>
 *     <li>Format des valeurs via {@link validation.rule.RegexRule}</li>
 * </ul>
 *
 * <p>
 * Cette classe est conçue pour être utilisée avant toute opération de persistance
 * ou de mise à jour d'un commercial.
 * </p>
 */
public final class CommercialValidator {

    private CommercialValidator() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Valide un commercial en appliquant un ensemble de règles sur chaque champ.
     *
     * <p>
     * Les règles vérifient notamment :
     * </p>
     * <ul>
     *     <li>Nom et prénom : obligatoires et longueur minimale/maximale</li>
     *     <li>Email : obligatoire et format correct</li>
     *     <li>Téléphone : obligatoire et format valide</li>
     *     <li>Objectif CA : obligatoire et numérique</li>
     *     <li>Date d'embauche : obligatoire et format YYYY-MM-DD</li>
     * </ul>
     *
     * <p>
     * Dès qu'une règle échoue, une {@link CommercialAssertException} est levée
     * avec un message descriptif.
     * </p>
     *
     * @param commercial l'objet {@link Commercial} à valider
     * @throws CommercialAssertException si une des règles échoue
     */
    public static void validateCommercial(Commercial commercial){
        throwException(commercial.getNom(), mandatory("Le nom est obligatoire"), length(2, 30, "Le nom"));
        throwException(commercial.getPrenom(), mandatory("le prénom est obligatoire"), length(2, 30, "le prénom"));
        throwException(commercial.getMail(), regex(ValidationPattern.EMAIL_REGEX, "le mail est invalide"), mandatory("le mail est obligatoire"));
        throwException(commercial.getTelephone(), mandatory("le téléphone est obligatoire") ,  regex(ValidationPattern.PHONE_REGEX,  "le téléphone est invalide"));
        throwException(commercial.getCa().toString(), regex(ValidationPattern.OBJ_CA_REGEX, "le CA est invalide"));

        String objCa = commercial.getObjectifCa() != null ? commercial.getCa().toString() : null;
        throwException(objCa, regex(ValidationPattern.OBJ_CA_REGEX, "l'objectif du CA est invalide"), mandatory("l'objectif du CA est obligatoire"));

        String dateEmbauche = commercial.getDateEmbauche() != null ? commercial.getDateEmbauche().toString() : null;
        throwException(dateEmbauche, regex(ValidationPattern.DATE_REGEX, "la date d'embauche est invalide"), mandatory("la date d'embauche est obligatoire"));
    }

    /**
     * Applique un tableau de {@link ValidationRule} sur une variable.
     *
     * <p>
     * Parcourt toutes les règles et lève une {@link CommercialAssertException}
     * dès qu'une règle échoue.
     * </p>
     *
     * @param variable la valeur à valider
     * @param rules    les règles de validation à appliquer
     * @throws CommercialAssertException si une règle échoue
     */
    private static void throwException(String variable, ValidationRule... rules){
        for(ValidationRule rule : rules) {
            String msg = rule.validate(variable);
            if(msg != null) throw new CommercialAssertException(msg);
        }
    }
}
