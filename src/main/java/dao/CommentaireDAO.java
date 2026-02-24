package dao;

import lombok.extern.slf4j.Slf4j;
import model.Commentaire;
import util.DataBaseUtil;
import util.SqlRequestCommentaire;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des commentaires liés aux clients.
 * <p>
 * Cette classe fournit les méthodes CRUD pour l'entité {@link Commentaire} :
 * Toutes les interactions avec la base de données passent par {@link DataBaseUtil}.
 */
@Slf4j
public class CommentaireDAO implements ICommentaireDAO {

    /**
     * Constructeur vide.
     */
    public CommentaireDAO() {}


    /**
     * Enregistre un nouveau commentaire pour un client.
     *
     * @param commentaire l'objet {@link Commentaire} à sauvegarder
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public void saveCommentaire(Commentaire commentaire) {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestCommentaire.SAVE_COMMENT)){
                ps.setInt(1, commentaire.getIdClient());
                ps.setString(2, commentaire.getCommentaire());
                ps.setString(3, commentaire.getDate().toString());
                ps.executeUpdate();

                log.info("Enregistrement du commentaire terminé");

        } catch (SQLException e) {
            log.error("Erreur lors de l'enregistrement du commentaire");
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère tous les commentaires associés à un client donné.
     *
     * @param idClient l'ID du client
     * @return une liste de {@link Commentaire} récupérés
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public List<Commentaire> getCommentairesByClientId(Integer idClient) {
        List<Commentaire> commentaires = new ArrayList<>();

        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestCommentaire.GET_BY_CLIENT_ID)) {

            ps.setInt(1, idClient);

            try(ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    commentaires.add(new Commentaire(result.getInt("id"),
                            result.getInt("id_client"),
                            result.getString("commentaire"),
                            result.getDate("date_commentaire").toLocalDate(),
                            LocalDate.parse(result.getString("date_derniere_modification"))
                    ));
                }
            }

            log.info("Liste des commentaires du client avec l'id : {} récupérée. Nombre de commentaire : {}",
                    idClient, commentaires.size());

        } catch (SQLException e) {
            log.error("Erreur lors du chargement des commentaires par l'id du client : {}", idClient);
            throw new RuntimeException(e);
        }

        return commentaires;
    }


    /**
     * Supprime un commentaire en fonction de son ID.
     *
     * @param id l'ID du commentaire à supprimer
     * @return le nombre de lignes affectées (0 si aucun commentaire supprimé)
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public int deleteCommentaireById(Integer id) {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestCommentaire.DELETE_COMMENT)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            log.info("Suppression du commentaire avec l'id : {} effectué", id);

            return rows;

        } catch (SQLException e) {
            log.error("Erreur lors de la suppression du commentaire par l'id : {}", id);
            throw new RuntimeException(e);
        }

    }

    /**
     * Met à jour un commentaire existant.
     *
     * @param commentaire l'objet {@link Commentaire} à mettre à jour
     * @return le nombre de lignes affectées (0 si aucun commentaire mis à jour)
     * @throws RuntimeException si une erreur SQL survient.
     */
    @Override
    public int updateCommentaire(Commentaire commentaire) {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestCommentaire.UPDATE_COMMENT)) {

            ps.setString(1, commentaire.getCommentaire());
            ps.setString(2, commentaire.getDateDerniereModification().toString());
            ps.setInt(3, commentaire.getId());

            int rows = ps.executeUpdate();

            log.info("Modification du commentaire avec l'id {} réussie", commentaire.getId());

            return rows;
        } catch (SQLException e) {
            log.error("Erreur lors de la modification du commentaire par l'id : {}", commentaire.getId());
            throw new RuntimeException(e);
        }
    }

}
