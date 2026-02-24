package validation;

/**
 * Classe utilitaire contenant les expressions régulières standard
 * utilisées pour la validation des champs dans tout le projet.
 *
 * <p>
 * Cette classe est final et ne peut pas être instanciée.
 * Toutes les constantes sont publiques et statiques pour un accès direct.
 * </p>
 *
 * <p>
 * Exemple d'utilisation :
 * <pre>
 *     String email = "test@example.com";
 *     boolean valid = email.matches(ValidationPattern.EMAIL_REGEX);
 * </pre>
 * </p>
 */
public final class ValidationPattern {

    /**
     * Constructeur privé pour empêcher l'instanciation.
     */
    private ValidationPattern(){}

    /** Valider un email (ex: test@exemple.com). */
    public static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";

    /** Valider un numéro de téléphone (10 à 15 chiffres, +, espaces ou tirets autorisés). */
    public static final String PHONE_REGEX = "^[0-9+\\- ]{10,15}$";

    /** Valider un code postal français à CINQ chiffres. */
    public static final String CODE_POSTAL_REGEX = "^\\d{5}$";

    /** Valider un objectif CA numérique (entier positif ou nul). */
    public static final String OBJ_CA_REGEX = "\\d*";

    /** Valider une date au format strict YYYY-MM-DD. */
    public static final String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    /** Valider un montant avec 0, 1 ou 2 décimales. */
    public static final String MONTANT_REGEX = "^\\d+([.,]\\d{1,2})?$";
}
