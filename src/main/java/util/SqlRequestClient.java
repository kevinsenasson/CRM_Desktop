package util;

public class SqlRequestClient {
    public static final String SAVE_CLIENT = "INSERT INTO client(societe, nom, prenom, email, telephone, adresse, code_postal, ville, pays, date_inscription, statut, date_derniere_modification, segment, source_acquisition) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    public static final String GET_ALL_CLIENTS = "SELECT * FROM client";
    public static final String GET_CLIENT_BY_ID = "SELECT * FROM client WHERE id = ?";
    public static final String UPDATE_CLIENT = "UPDATE client SET societe = ?, nom = ?, prenom = ?, email = ?, telephone = ?, adresse = ?, code_postal = ?, ville = ?, pays = ?, date_inscription = ?, statut = ?, date_derniere_modification = ?, segment = ?, source_acquisition = ? WHERE id = ?";
    public static final String DELETE_CLIENT = "DELETE FROM client WHERE id = ?";
    public static final String GET_CLIENT_WITH_COMMENTS =
            """
            SELECT c.id,
                   c.societe,
                   c.nom,
                   c.prenom,
                   c.email,
                   c.telephone,
                   c.adresse,
                   c.code_postal,
                   c.ville,
                   c.pays,
                   c.date_inscription,
                   c.date_derniere_modification,
                   c.statut,
                   c.segment,
                   c.source_acquisition,
                   com.id as com_id,
                   com.commentaire,
                   com.date_commentaire as com_date,
                   com.date_derniere_modification as com_date_derniere_modification
            FROM client c
            LEFT JOIN commentaire com ON c.id = com.id_client
            WHERE c.id = ?
            """;

}
