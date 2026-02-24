package validation.validator;

import exception.ClientAssertException;
import model.Client;
import validation.ValidationPattern;
import validation.rule.ValidationRule;

import static validation.rule.MandatoryRule.mandatory;
import static validation.rule.LengthRule.length;
import static validation.rule.RegexRule.regex;

/**
 * Classe utilitaire responsable de la validation des objets {@link Client}.
 *
 * <p>
 * Cette classe applique un ensemble de règles de validation sur chaque champ
 * d'un client et lève une {@link ClientAssertException} dès qu'une règle échoue.
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
 * ou de mise à jour d'un client.
 * </p>
 */
public final class ClientValidator {

    private ClientValidator() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Valide un client en appliquant un ensemble de règles sur chaque champ.
     *
     * <p>
     * Les règles vérifient notamment :
     * </p>
     * <ul>
     *     <li>Nom et prénom : obligatoires et longueur minimale/maximale</li>
     *     <li>Société : obligatoire et longueur</li>
     *     <li>Email : format correct</li>
     *     <li>Code postal : obligatoire, longueur exacte et format numérique</li>
     *     <li>Téléphone : format numérique ou avec + / espace / tiret</li>
     *     <li>Statut : obligatoire</li>
     * </ul>
     *
     * <p>
     * Dès qu'une règle échoue, une {@link ClientAssertException} est levée
     * avec un message descriptif.
     * </p>
     *
     * @param client l'objet {@link Client} à valider
     * @throws ClientAssertException si une des règles échoue
     */
    public static void validateClient(Client client){
        throwException(client.getNom(), length(2, 25, "Le nom"), mandatory("Le nom du client est obligatoire"));
        throwException(client.getPrenom(), mandatory("Le prénom est obligatoire"), length(2, 30, "Le prénom"));
        throwException(client.getSociete(), mandatory("Le nom de société est obligatoire"), length(2, 50, "La société"));
        throwException(client.getEmail(), regex(ValidationPattern.EMAIL_REGEX, "Email invalide"));
        throwException(client.getCodePostal(), mandatory("Le code postal est obligatoire"), length(5, 5, "Le code postal"),
                regex(ValidationPattern.CODE_POSTAL_REGEX, "Le code postal n'est pas au bon format")
        );
        throwException(client.getTelephone(), regex(ValidationPattern.PHONE_REGEX, "Téléphone invalide"));
        String statue = client.getStatut() != null ? client.getStatut().name() : null;
        throwException(statue, mandatory("Le statut est obligatoire"));
    }

    /**
     * Applique un tableau de {@link ValidationRule} sur une variable.
     *
     * <p>
     * Parcourt toutes les règles et lève une {@link ClientAssertException}
     * dès qu'une règle échoue.
     * </p>
     *
     * @param variable la valeur à valider
     * @param rules    les règles de validation à appliquer
     * @throws ClientAssertException si une règle échoue
     */
    private static void throwException(String variable, ValidationRule... rules){
        for(ValidationRule rule : rules) {
            String msg = rule.validate(variable);
            if(msg != null) throw new ClientAssertException(msg);
        }
    }
}
