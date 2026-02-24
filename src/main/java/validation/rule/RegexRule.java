package validation.rule;

/**
 * Classe utilitaire fournissant des règles de validation basées sur des expressions régulières.
 *
 * <p>
 * Elle permet de vérifier qu'une chaîne de caractères correspond à un pattern donné.
 * Cette classe propose deux types de validation :
 * </p>
 * <ul>
 *     <li>Une règle {@link ValidationRule} retournant un message d'erreur si la valeur ne correspond pas au pattern</li>
 *     <li>Une vérification simple retournant un {@code boolean}</li>
 * </ul>
 *
 * <p>
 * Les valeurs nulles ou vides sont considérées comme valides par défaut.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class RegexRule {

    private RegexRule() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Crée une règle de validation basée sur une expression régulière.
     *
     * <p>
     * Si la valeur est non nulle et non vide, elle est comparée au pattern.
     * En cas de non-conformité, le message d'erreur fourni est retourné.
     * Sinon, {@code null} est retourné (valeur valide).
     * </p>
     *
     * @param pattern l'expression régulière à respecter
     * @param message le message d'erreur à retourner si la valeur ne correspond pas
     * @return une {@link ValidationRule} appliquant le pattern
     */
    public static ValidationRule regex(String pattern, String message) {
        return val -> {
            if(val != null && !val.isBlank()) {
               return !val.matches(pattern) ? message : null;
            } else return null;
        };
    }


    /**
     * Vérifie si une valeur correspond à un pattern donné.
     *
     * <p>
     * Cette méthode retourne {@code true} si :
     * </p>
     * <ul>
     *     <li>la valeur est {@code null} ou vide</li>
     *     <li>la valeur correspond à l'expression régulière</li>
     * </ul>
     *
     * @param value la valeur à tester
     * @param regex l'expression régulière
     * @return {@code true} si la valeur correspond au pattern ou est vide, {@code false} sinon
     */
    public static boolean matches(String value, String regex) {
        return (value == null || value.isBlank()) || value.matches(regex);
    }
}
