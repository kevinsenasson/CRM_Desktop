package validation.rule;

/**
 * Représente une règle de validation appliquée à une valeur de type {@link String}.
 *
 * <p>
 * Une implémentation de cette interface doit analyser la valeur fournie
 * et retourner :
 * </p>
 * <ul>
 *     <li>{@code null} si la valeur est considérée comme valide</li>
 *     <li>Un message d'erreur si la validation échoue</li>
 * </ul>
 *
 * <p>
 * Cette interface est annotée {@link FunctionalInterface}, ce qui permet
 * son utilisation avec des expressions lambda.
 * </p>
 *
 * <p>
 * Elle constitue la base du moteur de validation et permet de composer
 * dynamiquement plusieurs règles sur un même champ.
 * </p>
 */
@FunctionalInterface
public interface ValidationRule {
    /**
     * Valide la valeur fournie.
     *
     * @param value la valeur à valider (peut être {@code null})
     * @return {@code null} si la valeur est valide,
     *         sinon un message décrivant l'erreur de validation
     */
    String validate(String value);
}

