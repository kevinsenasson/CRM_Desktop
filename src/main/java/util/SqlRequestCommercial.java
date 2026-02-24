package util;

public class SqlRequestCommercial {
    public static final String SAVE_COMMERCIAL = "INSERT INTO commercial(id_enterprise, nom, prenom, mail, telephone, date_embauche, ca, objectif_ca) VALUES(?,?,?,?,?,?,?,?)";

    public static final String GET_COMMERCIAL = "SELECT * FROM commercial LIMIT 1";

    public static final String UPDATE_COMMERCIAL = "UPDATE commercial SET nom = ?, prenom = ?, mail = ?, telephone = ?, date_embauche = ?, ca = ?, objectif_ca = ? WHERE id = ?";
}
