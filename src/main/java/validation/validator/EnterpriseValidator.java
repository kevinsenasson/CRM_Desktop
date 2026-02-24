package validation.validator;

import exception.EnterpriseAssertException;
import model.Enterprise;
import validation.ValidationPattern;
import validation.rule.ValidationRule;

import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.LengthRule.length;
import static validation.rule.RegexRule.regex;
import static validation.rule.SiretRule.siret;

/**
 * Classe utilitaire responsable de la validation des objets {@link Enterprise}.
 *
 * <p>
 * Cette classe applique un ensemble de règles de validation sur chaque champ
 * d'une entreprise et lève une {@link EnterpriseAssertException} dès qu'une règle échoue.
 * </p>
 *
 * <p>
 * Les règles utilisées incluent :
 * </p>
 * <ul>
 *     <li>Champs obligatoires via {@link validation.rule.MandatoryRule}</li>
 *     <li>Longueurs minimales et maximales via {@link validation.rule.LengthRule}</li>
 *     <li>Format des valeurs via {@link validation.rule.RegexRule}</li>
 *     <li>Numéro SIRET via {@link validation.rule.SiretRule}</li>
 * </ul>
 *
 * <p>
 * Cette classe est conçue pour être utilisée avant toute opération de persistance
 * ou de mise à jour d'une entreprise.
 * </p>
 */
public final class EnterpriseValidator {

    private EnterpriseValidator() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Valide une entreprise en appliquant un ensemble de règles sur chaque champ.
     *
     * <p>
     * Les règles vérifient notamment :
     * </p>
     * <ul>
     *     <li>Nom : obligatoire et longueur 2-40 caractères</li>
     *     <li>Code postal : obligatoire, longueur exacte 5 et format numérique</li>
     *     <li>Adresse et ville : obligatoires et longueur 2-50 caractères</li>
     *     <li>Email : format correct si présent</li>
     *     <li>Téléphone : format correct si présent</li>
     *     <li>SIRET : format 14 chiffres et validité Luhn</li>
     * </ul>
     *
     * <p>
     * Dès qu'une règle échoue, une {@link EnterpriseAssertException} est levée
     * avec un message descriptif.
     * </p>
     *
     * @param enterprise l'objet {@link Enterprise} à valider
     * @throws EnterpriseAssertException si une des règles échoue
     */
    public static void validateEnterprise(Enterprise enterprise){
        throwException(enterprise.getNom(), mandatory("Le nom est obligatoire"), length(2, 40, "Le nom"));
        throwException(enterprise.getCodePostal(), mandatory("Le code postal est obligatoire"), length(5, 5, "Le code postal"),
                regex(ValidationPattern.CODE_POSTAL_REGEX, "Le code postal n'est pas au bon format")
        );
        throwException(enterprise.getAdresse(), mandatory("L'adresse est obligatoire"), length(2, 50, "L'adresse"));
        throwException(enterprise.getVille(), mandatory("La ville est obligatoire"), length(2, 50, "La ville"));
        throwException(enterprise.getEmail(), regex(ValidationPattern.EMAIL_REGEX, "L'email n'est pas au bon format"));
        throwException(enterprise.getTelephone(), regex(ValidationPattern.PHONE_REGEX, "le téléphone n'est pas au bon format"));
        throwException(enterprise.getSiret(), siret(), mandatory("Le siret est obligatoire"));
    }

    /**
     * Applique un tableau de {@link ValidationRule} sur une variable.
     *
     * <p>
     * Parcourt toutes les règles et lève une {@link EnterpriseAssertException}
     * dès qu'une règle échoue.
     * </p>
     *
     * @param variable la valeur à valider
     * @param rules    les règles de validation à appliquer
     * @throws EnterpriseAssertException si une règle échoue
     */
    private static void throwException(String variable, ValidationRule... rules){
        for(ValidationRule rule : rules) {
            String msg = rule.validate(variable);
            if(msg != null) throw new EnterpriseAssertException(msg);
        }
    }
}
