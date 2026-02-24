package validation.validator;

import lombok.extern.slf4j.Slf4j;

/**
 * Classe utilitaire fournissant des assertions simples pour valider
 * des objets et des entiers.
 *
 * <p>
 * Cette classe permet de vérifier des conditions préalables (préconditions)
 * et de lever une {@link IllegalArgumentException} avec un message clair
 * en cas d'échec.
 * </p>
 *
 * <p>
 * Les assertions loggent un avertissement via SLF4J avant de lancer l'exception.
 * </p>
 *
 * <p>
 * Cette classe ne doit pas être instanciée.
 * </p>
 */
@Slf4j
public final class Assertion {

    private Assertion() {
        // Empêche l'instanciation (classe utilitaire)
    }

    /**
     * Vérifie qu'un objet n'est pas {@code null}.
     *
     * @param obj     l'objet à tester
     * @param message le message à inclure dans l'exception si l'objet est {@code null}
     * @throws IllegalArgumentException si l'objet est {@code null}
     */
    public static void assertNotNull(Object obj, String message) {
        if (obj == null) {
            log.warn("l'objet ne peut pas être null");
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * Vérifie qu'un entier n'est pas {@code null} et strictement positif.
     *
     * @param integer l'entier à tester
     * @param message le message à inclure dans l'exception si la condition échoue
     * @throws IllegalArgumentException si l'entier est {@code null} ou <= 0
     */
    public static void assertIntegerPositiveAndNotNull(Integer integer, String message) {
        if(integer == null || integer <= 0) {
            log.warn("L'id ne peut pas être nulle ou négatif");
            throw new IllegalArgumentException(message);
        }
    }
}
