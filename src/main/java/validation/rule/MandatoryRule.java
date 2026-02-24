package validation.rule;

/**
 * Classe utilitaire fournissant une règle de validation pour les champs obligatoires.
 *
 * <p>
 * Cette règle permet de s'assurer qu'une valeur {@link String} n'est ni nulle
 * ni vide (après suppression des espaces en début et fin).
 * </p>
 *
 * <p>
 * Elle est conçue pour être utilisée dans le moteur de validation basé sur {@link ValidationRule}.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class MandatoryRule {

    private MandatoryRule() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Crée une règle de validation indiquant qu'un champ est obligatoire.
     *
     * <p>
     * Si la valeur fournie est {@code null} ou ne contient que des espaces,
     * le message fourni est retourné.
     * Sinon, {@code null} est retourné (valeur valide).
     * </p>
     *
     * @param message le message d'erreur à retourner si la valeur est vide ou nulle
     * @return une {@link ValidationRule} qui valide l'obligation du champ
     */
    public static ValidationRule mandatory(String message) {
        return val -> val == null || val.isBlank() ? message : null;
    }
}
