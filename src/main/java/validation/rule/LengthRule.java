package validation.rule;

/**
 * Classe utilitaire fournissant différentes règles de validation
 * liées à la longueur d'une chaîne de caractères.
 *
 * <p>
 * Cette classe permet :
 * </p>
 * <ul>
 *     <li>De générer dynamiquement des règles de validation retournant un message d'erreur</li>
 *     <li>D'effectuer des vérifications simples de longueur via des méthodes booléennes</li>
 * </ul>
 *
 * <p>
 * Les méthodes retournant une {@link ValidationRule} sont destinées à être
 * utilisées dans un moteur de validation centralisé.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class LengthRule {

    private LengthRule() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Crée une règle de validation vérifiant que la longueur
     * d'une valeur est comprise entre un minimum et un maximum.
     *
     * <p>
     * Si la valeur est {@code null}, aucune erreur n'est retournée.
     * </p>
     *
     * @param min longueur minimale autorisée
     * @param max longueur maximale autorisée
     * @return une règle de validation retournant un message d'erreur
     *         si la longueur est invalide, ou {@code null} si valide
     */
    public static ValidationRule length(int min, int max) {
        return val -> {
            if (val == null) return null;
            if (val.length() < min) return "Doit contenir au moins " + min + " caractères";
            if (val.length() > max) return "Nombre de caractères maximal : " + max;
            return null;
        };
    }

    /**
     * Crée une règle de validation vérifiant que la longueur
     * d'une valeur est comprise entre un minimum et un maximum,
     * en personnalisant le nom du champ dans le message d'erreur.
     *
     * <p>
     * Si la valeur est {@code null}, aucune erreur n'est retournée.
     * </p>
     *
     * @param min  longueur minimale autorisée
     * @param max  longueur maximale autorisée
     * @param name nom du champ utilisé dans les messages d'erreur
     * @return une règle de validation personnalisée
     */
    public static ValidationRule length(int min, int max, String name) {
        return val -> {
            if (val == null) return null;
            if (val.length() < min) return name + " doit contenir au moins " + min + " caractères";
            if (val.length() > max) return name + " ne doit pas dépasser " + max + " caractères";
            return null;
        };
    }

    /**
     * Vérifie qu'une valeur n'est ni nulle ni vide
     * et respecte une longueur minimale.
     *
     * @param value valeur à vérifier
     * @param min   longueur minimale requise
     * @return {@code true} si la valeur est non vide et respecte la longueur minimale
     */
    public static boolean isNotBlankMin(String value, int min) {
        return value != null && !value.isBlank() && value.length() >= min;
    }

    /**
     * Vérifie qu'une valeur ne dépasse pas une longueur maximale.
     *
     * @param value valeur à vérifier
     * @param max   longueur maximale autorisée
     * @return {@code true} si la valeur est non nulle et respecte la longueur maximale
     */
    public static boolean isMaxLength(String value, int max) {
        return value != null && value.length() <= max;
    }
}
