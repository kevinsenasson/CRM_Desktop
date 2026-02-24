package validation.validator;

import exception.DevisAssertException;
import model.Devis;
import validation.ValidationPattern;
import validation.rule.ValidationRule;

import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.LengthRule.length;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire responsable de la validation des objets {@link Devis}.
 *
 * <p>
 * Cette classe applique un ensemble de règles de validation sur chaque champ
 * d'un devis et lève une {@link DevisAssertException} dès qu'une règle échoue.
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
 * ou de mise à jour d'un devis.
 * </p>
 */
public final class DevisValidator {

    private DevisValidator() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Valide un devis en appliquant un ensemble de règles sur chaque champ.
     *
     * <p>
     * Les règles vérifient notamment :
     * </p>
     * <ul>
     *     <li>Référence : obligatoire et longueur maximale de 25 caractères</li>
     *     <li>Montant : obligatoire et numérique (avec 0, 1 ou 2 décimales)</li>
     *     <li>Date de création : obligatoire et au format YYYY-MM-DD</li>
     * </ul>
     *
     * <p>
     * Dès qu'une règle échoue, une {@link DevisAssertException} est levée
     * avec un message descriptif.
     * </p>
     *
     * @param devis l'objet {@link Devis} à valider
     * @throws DevisAssertException si une des règles échoue
     */
    public static void validateDevis(Devis devis){
        throwException(devis.getReference(), mandatory("La référence est obligatoire"), length(1, 25, "La référence"));

        String montant = devis.getMontant() != null ? devis.getMontant().toString() : null;
        throwException(montant, mandatory("Le montant est obligatoire"), regex(ValidationPattern.MONTANT_REGEX, "Montant invalide"));
        throwException(devis.getDateCreation().toString(), mandatory("la date est obligatoire"), regex(ValidationPattern.DATE_REGEX, "Date invalide"));
    }

    /**
     * Applique un tableau de {@link ValidationRule} sur une variable.
     *
     * <p>
     * Parcourt toutes les règles et lève une {@link DevisAssertException}
     * dès qu'une règle échoue.
     * </p>
     *
     * @param variable la valeur à valider
     * @param rules    les règles de validation à appliquer
     * @throws DevisAssertException si une règle échoue
     */
    private static void throwException(String variable, ValidationRule... rules){
        for(ValidationRule rule : rules) {
            String msg = rule.validate(variable);
            if(msg != null) throw new DevisAssertException(msg);
        }
    }
}
