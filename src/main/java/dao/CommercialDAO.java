package dao;

import lombok.extern.slf4j.Slf4j;
import model.Commercial;
import util.DataBaseUtil;
import util.SqlRequestCommercial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * DAO pour la gestion des commerciaux.
 * <p>
 * Fournit les méthodes CRUD pour l'entité {@link Commercial}
 * Toutes les interactions avec la base de données passent par {@link DataBaseUtil}.
 */
@Slf4j
public class CommercialDAO implements ICommercialDAO {

    /**
     * Constructeur vide.
     */
    public CommercialDAO() {}

    /**
     * Enregistre un nouveau commercial en base.
     *
     * @param commercial l'objet {@link Commercial} à sauvegarder
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public void saveCommercial(Commercial commercial) {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestCommercial.SAVE_COMMERCIAL)) {

            ps.setInt(1, commercial.getIdEntreprise());
            ps.setString(2, commercial.getNom());
            ps.setString(3, commercial.getPrenom());
            ps.setString(4, commercial.getMail());
            ps.setString(5, commercial.getTelephone());
            ps.setString(6, commercial.getDateEmbauche().toString());
            ps.setInt(7, commercial.getCa());
            ps.setInt(8, commercial.getObjectifCa());

            ps.executeUpdate();

            log.info("Enregistrement du commercial réussi");

        } catch (SQLException e) {
            log.error("Erreur lors de l'enregistrement du commercial");
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère un commercial depuis la base.
     *
     * @return le {@link Commercial} récupéré, ou null si aucun commercial trouvé
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public Commercial getCommercial() {
        Commercial commercial = null;

        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestCommercial.GET_COMMERCIAL);
             ResultSet result = ps.executeQuery()) {

            if(result.next()) {
                commercial = new Commercial(result.getInt("id"),
                        result.getInt("id_enterprise"),
                        result.getString("nom"),
                        result.getString("prenom"),
                        result.getString("mail"),
                        result.getString("telephone"),
                        LocalDate.parse(result.getString("date_embauche")),
                        result.getInt("ca"),
                        result.getInt("objectif_ca")
                );
            }
        } catch (SQLException e) {
            log.error("Le commercial n'as pas pu être récupéré");
            throw new RuntimeException(e);
        }

        return commercial;
    }

    /**
     * Met à jour un commercial existant en base.
     *
     * @param commercial l'objet {@link Commercial} à mettre à jour
     * @return le nombre de lignes affectées (Zéro si aucun commercial mis à jour.)
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public int updateCommercial(Commercial commercial) {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestCommercial.UPDATE_COMMERCIAL)) {

            ps.setString(1, commercial.getNom());
            ps.setString(2, commercial.getPrenom());
            ps.setString(3, commercial.getMail());
            ps.setString(4, commercial.getTelephone());
            ps.setString(5, commercial.getDateEmbauche().toString());
            ps.setInt(6, commercial.getCa());
            ps.setInt(7, commercial.getObjectifCa());
            ps.setInt(8, commercial.getId());

            int rows = ps.executeUpdate();

            log.info("Update du commercial réussi");

            return rows;

        } catch (SQLException e) {
            log.error("Erreur lors de l'update du commercial");
            throw new RuntimeException(e);
        }
    }
}
