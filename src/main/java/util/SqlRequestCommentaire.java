package util;

public class SqlRequestCommentaire {

    public static final String SAVE_COMMENT =
            "INSERT INTO commentaire(id_client, commentaire, date_commentaire) VALUES(?,?,?)";

    public static final String GET_BY_CLIENT_ID =
            "SELECT * FROM commentaire WHERE id_client = ?";

    public static final String DELETE_COMMENT =
            "DELETE FROM commentaire WHERE id = ?";

    public static final String UPDATE_COMMENT =
            "UPDATE commentaire SET commentaire = ?, date_derniere_modification = ?  WHERE id = ?";
}
