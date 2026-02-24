package util;

public class SqlRequestDevis {

    public static final String SAVE_DEVIS = "INSERT INTO devis(id_client, reference, date_creation, montant, statut, description) VALUES(?,?,?,?,?,?)";

    public static final String GET_ALL_DEVIS = "SELECT * FROM devis";

    public static final String GET_BY_CLIENT_ID = "SELECT * FROM devis WHERE id_client = ?";

    public static final String DELETE_DEVIS = "DELETE FROM devis WHERE id = ?";

    public static final String UPDATE_DEVIS = "UPDATE devis SET montant = ?, date_creation = ?, reference = ?, statut = ?, description = ? WHERE id = ?";
}
