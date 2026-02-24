package validation.rule;

/**
 * Classe utilitaire permettant de valider un numéro SIRET
 * à l'aide de l'algorithme de Luhn.
 *
 * <p>
 * L'algorithme de Luhn est un algorithme de contrôle utilisé pour vérifier
 * la validité de certains numéros d'identification (cartes bancaires,
 * numéros SIRET, etc.).
 * </p>
 *
 * <p>
 * Cette classe ne vérifie que la validité mathématique du numéro.
 * Elle ne contrôle pas :
 * </p>
 * <ul>
 *     <li>La présence exclusive de caractères numériques</li>
 * </ul>
 *
 * <p>
 * Ces vérifications doivent être effectuées en amont.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class LuhnValidator {

    private LuhnValidator() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Vérifie la validité d'un numéro SIRET via l'algorithme de Luhn.
     *
     * @param siret numéro SIRET à vérifier (doit contenir uniquement des chiffres)
     * @return {@code true} si le numéro est valide selon l'algorithme de Luhn,
     *         {@code false} sinon
     */
    public static boolean isSiretValidForLuhn(String siret) {

        int sum = 0;
        boolean shouldDouble = false;

        for (int i = siret.length() - 1; i >= 0; i--) {
            int digit = siret.charAt(i) - '0';
            if (shouldDouble) {
                digit *= 2;

                // Si le résultat dépasse 9,
                // on soustrait 9 (équivalent à additionner les deux chiffres)
                if (digit > 9) digit -= 9;
            }
            sum += digit;

            // Alterne le doublement à chaque itération
            shouldDouble = !shouldDouble;
        }

        // Un numéro est valide si la somme est un multiple de 10
        return sum % 10 == 0;
    }
}
