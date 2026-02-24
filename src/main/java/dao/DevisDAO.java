package dao;

import lombok.extern.slf4j.Slf4j;
import model.Devis;
import model.StatutDevis;
import util.DataBaseUtil;
import util.SqlRequestDevis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des devis en base de données.
 * <p>
 * Implémente toutes les opérations CRUD et de lecture spécifiques aux devis.
 * Chaque méthode utilise {@link DataBaseUtil} pour obtenir une connexion.
 * Les exceptions SQL sont capturées et retransformées en {@link RuntimeException}.
 * </p>
 */
@Slf4j
public class DevisDAO implements IDevisDao{

    /** Constructeur par défaut */
    public DevisDAO(){}


    /**
     * Récupère tous les devis présents en base.
     *
     * @return Liste de tous les devis
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public List<Devis> findAll() {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestDevis.GET_ALL_DEVIS);
            ResultSet result = ps.executeQuery()) {

            List<Devis> devis = new ArrayList<>();
            while(result.next()){
                devis.add(createDevis(result));
            }
            log.info("Tout les devis sont récupérés : {} devis", devis.size());
            return devis;

        } catch (SQLException e) {
            log.error("Erreur lors de la récupération de tous les devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Sauvegarde un nouveau devis en base.
     *
     * @param devis Devis à enregistrer, ne doit pas être null
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public void saveDevis(Devis devis){
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestDevis.SAVE_DEVIS)) {

            ps.setInt(1, devis.getIdClient());
            ps.setString(2, devis.getReference());
            ps.setString(3, devis.getDateCreation().toString());
            ps.setDouble(4, devis.getMontant());
            ps.setString(5, devis.getStatut().name());
            ps.setString(6, devis.getDescription());

            ps.executeUpdate();

            log.info("devis sauvegardé");

        } catch (SQLException e) {
            log.error("Erreur lors de l'enregistrement du devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Supprime un devis en base par son identifiant.
     *
     * @param id Identifiant du devis à supprimer
     * @return Nombre de lignes affectées (0 si le devis n'existait pas)
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public int deleteDevisById(Integer id){

        log.info("delete devis");

        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestDevis.DELETE_DEVIS)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            log.info("Suppression du devis avec l'id {} effectuée", id);

            return rows;

        } catch (SQLException e) {
            log.error("Erreur lors de la suppression du devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère tous les devis d'un client spécifique.
     *
     * @param id Identifiant du client
     * @return Liste des devis du client
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public List<Devis> getDevisByClientId(Integer id){
        List<Devis> devis = new ArrayList<>();

        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestDevis.GET_BY_CLIENT_ID)) {

            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    devis.add(createDevis(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Erreur lors du chargement des devis par id du client");
            throw new RuntimeException(e);
        }

        log.info("Récupération des devis effectué avec l'id du client : {}. {} devis récupérés", id, devis.size());
        return devis;
    }

    /**
     * Met à jour un devis existant en base.
     *
     * @param devis Devis contenant les nouvelles valeurs (doit avoir un ID valide)
     * @return Nombre de lignes affectées (0 si le devis n'existait pas)
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public int updateDevis(Devis devis){
        try (Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestDevis.UPDATE_DEVIS)) {

            ps.setDouble(1, devis.getMontant());
            ps.setString(2, devis.getDateCreation().toString());
            ps.setString(3, devis.getReference());
            ps.setString(4, devis.getStatut().name());
            ps.setString(5, devis.getDescription());
            ps.setInt(6, devis.getId());

            int rows = ps.executeUpdate();

            log.info("Update du devis avec l'id {} effectué", devis.getId());

            return rows;
        } catch (SQLException e) {
            log.error("Erreur lors de l'update du devis");
            throw new RuntimeException(e);
        }
    }

    /**
     * Crée un objet Devis à partir d'un ResultSet.
     *
     * @param rs ResultSet positionné sur une ligne valide
     * @return Devis créé
     * @throws SQLException si une erreur SQL survient
     */
    private Devis createDevis(ResultSet rs) throws SQLException {
        Devis devis = new Devis();
        devis.setId(rs.getInt("id"));
        devis.setIdClient(rs.getInt("id_client"));
        devis.setReference(rs.getString("reference"));
        devis.setDateCreation(LocalDate.parse(rs.getString("date_creation")));
        devis.setMontant(rs.getDouble("montant"));
        devis.setStatut(StatutDevis.valueOf(rs.getString("statut")));
        devis.setDescription(rs.getString("description"));
        return devis;
    }


}
