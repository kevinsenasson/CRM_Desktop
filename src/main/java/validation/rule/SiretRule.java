package validation.rule;

import static validation.rule.LuhnValidator.isSiretValidForLuhn;

/**
 * Classe utilitaire fournissant une règle de validation pour les numéros SIRET.
 *
 * <p>
 * Cette règle combine plusieurs vérifications :
 * </p>
 * <ul>
 *     <li>La valeur est non nulle et non vide (sinon considérée comme valide)</li>
 *     <li>Le SIRET contient exactement 14 chiffres</li>
 *     <li>Le SIRET est valide selon l'algorithme de Luhn</li>
 * </ul>
 *
 * <p>
 * Elle retourne un message d'erreur descriptif pour la première règle qui échoue.
 * </p>
 *
 * <p>
 * Conçue pour être utilisée avec le moteur de validation basé sur {@link ValidationRule}.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
public final class SiretRule {


    private SiretRule() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Crée une règle de validation pour un numéro SIRET.
     *
     * <p>
     * La validation suit cet ordre :
     * </p>
     * <ol>
     *     <li>Valeur {@code null} ou vide → considéré valide</li>
     *     <li>Vérification du format : exactement 14 chiffres</li>
     *     <li>Vérification de l'algorithme de Luhn</li>
     * </ol>
     *
     * @return une {@link ValidationRule} pour valider un SIRET
     */

    public static ValidationRule siret() {
        return val -> {
            if (val == null || val.isBlank()) return null;
            if (!val.matches("\\d{14}")) return "Le SIRET doit contenir 14 chiffres";
            if (!isSiretValidForLuhn(val)) return "Le SIRET est invalide";
            return null;
        };
    }
}
