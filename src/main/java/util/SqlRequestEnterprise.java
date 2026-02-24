package util;

public class SqlRequestEnterprise {

    private SqlRequestEnterprise(){}

    public static final String SAVE = """
        INSERT INTO enterprise
        (nom, adresse, code_postal, ville, telephone, email, siret)
        VALUES (?, ?, ?, ?, ?, ?, ?);
        """;

    public static final String FIND_ONE = """
        SELECT *
        FROM enterprise
        LIMIT 1;
        """;

    public static final String UPDATE = """
        UPDATE enterprise
        SET nom = ?,
            adresse = ?,
            code_postal = ?,
            ville = ?,
            telephone = ?,
            email = ?,
            siret = ?
        WHERE id = ?;
        """;

    public static final String DELETE = """
        DELETE FROM enterprise
        WHERE id = ?;
        """;
}
