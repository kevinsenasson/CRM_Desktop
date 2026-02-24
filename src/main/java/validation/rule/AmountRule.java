package validation.rule;

/**
 * Classe utilitaire contenant les règles de validation liées aux montants.
 *
 * <p>
 * Cette classe fournit des méthodes statiques permettant de vérifier
 * qu'une valeur saisie représente un montant numérique strictement positif.
 * </p>
 *
 * <p>
 * Elle est conçue pour être utilisée comme couche intermédiaire de validation
 * avant les opérations de sauvegarde ou de mise à jour des objets métier.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class AmountRule {

    private AmountRule() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Vérifie qu'une chaîne de caractères représente un montant strictement positif
     * et conforme à une expression régulière donnée.
     *
     * <p>
     * La validation se déroule en trois étapes :
     * </p>
     * <ul>
     *     <li>Vérification que la valeur n'est ni nulle ni vide</li>
     *     <li>Validation du format via l'expression régulière fournie</li>
     *     <li>Conversion en {@code double} et vérification que le montant est strictement supérieur à 0</li>
     * </ul>
     *
     * <p>
     * Les virgules sont automatiquement remplacées par des points afin
     * de supporter les formats décimaux français.
     * </p>
     *
     * @param value la valeur saisie à valider
     * @param regex l'expression régulière définissant le format numérique autorisé
     * @return {@code true} si la valeur est un montant valide strictement positif,
     *         {@code false} sinon
     */
    public static boolean isPositiveAmount(String value, String regex) {
        // Vérification basique de présence et conformité au format attendu
        if (value == null || value.isBlank() || !value.matches(regex)) return false;
        try {
            return Double.parseDouble(value.replace(",", ".")) > 0;
        } catch (Exception e) {
            // Sécurise le cas où la conversion échoue malgré la regex
            return false;
        }
    }
}
