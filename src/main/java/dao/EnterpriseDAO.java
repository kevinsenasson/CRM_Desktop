package dao;

import lombok.extern.slf4j.Slf4j;
import model.Enterprise;
import util.DataBaseUtil;
import util.SqlRequestEnterprise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO pour gérer les opérations CRUD sur l'entité {@link Enterprise}.
 * <p>
 * Fournit des méthodes pour sauvegarder, mettre à jour et récupérer les informations
 * d'une entreprise. Utilise {@link DataBaseUtil} pour obtenir les connexions JDBC
 * et {@link SqlRequestEnterprise} pour les requêtes SQL.
 * <p>
 * Toutes les méthodes gèrent les exceptions SQL et les transforment en
 * {@link RuntimeException} avec un log approprié.
 */
@Slf4j
public class EnterpriseDAO implements IEnterpriseDAO{

    /**
     * Constructeur par défaut.
     */
    public EnterpriseDAO(){}

    /**
     * Sauvegarde une entreprise dans la base de données.
     *
     * @param enterprise l'entreprise à enregistrer
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public void saveEnterprise(Enterprise enterprise){

        log.info("save Enterprise");

        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestEnterprise.SAVE)){

                prepareStatement(ps, enterprise);
                ps.executeUpdate();

                log.info("Save Enterprise Finished");

        }catch (Exception e){
            log.error("Erreur lors de l'enregistrement de l'entreprise");
            throw new RuntimeException(e);
        }
    }

    /**
     * Met à jour les informations d'une entreprise existante.
     *
     * @param enterprise l'entreprise avec ses nouvelles données (doit avoir un ID valide)
     * @return le nombre de lignes affectées (0 si aucune modification)
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public int updateEnterprise(Enterprise enterprise){
        log.info("Update Enterprise");

        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement ps = connection.prepareStatement(SqlRequestEnterprise.UPDATE)){

            prepareStatement(ps, enterprise);
            ps.setInt(8, enterprise.getId());
            int rows = ps.executeUpdate();

            log.info("Update Enterprise Finished");

            return rows;
        }catch (Exception e){
            log.error("Erreur lors de la sauvegarde de l'entreprise");
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère une entreprise depuis la base de données.
     *
     * @return l'entreprise trouvée ou {@code null} si aucune n'existe
     * @throws RuntimeException si une erreur SQL survient
     */
    @Override
    public Enterprise getEnterprise(){
        log.info("Get Enterprise");

        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestEnterprise.FIND_ONE);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapEnterprise(rs);
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'entreprise", e);
        }
    }

    /**
     * Prépare un {@link PreparedStatement} avec les données d'une entreprise.
     *
     * @param ps         le PreparedStatement à remplir
     * @param enterprise l'entreprise contenant les données
     * @throws SQLException si une erreur survient lors de la configuration du statement
     */
    private void prepareStatement(PreparedStatement ps, Enterprise enterprise) throws SQLException {
        ps.setString(1, enterprise.getNom());
        ps.setString(2, enterprise.getAdresse());
        ps.setString(3, enterprise.getCodePostal());
        ps.setString(4, enterprise.getVille());
        ps.setString(5, enterprise.getTelephone());
        ps.setString(6, enterprise.getEmail());
        ps.setString(7, enterprise.getSiret());
    }

    /**
     * Convertit un {@link ResultSet} en objet {@link Enterprise}.
     *
     * @param rs le ResultSet contenant les données de l'entreprise
     * @return l'objet Enterprise correspondant
     * @throws SQLException si une erreur survient lors de la lecture du ResultSet
     */
    private Enterprise mapEnterprise(ResultSet rs) throws SQLException {
        return new Enterprise(
                rs.getInt("id"),
                rs.getString("nom"),
                rs.getString("adresse"),
                rs.getString("code_postal"),
                rs.getString("ville"),
                rs.getString("telephone"),
                rs.getString("email"),
                rs.getString("Siret")
        );
    }
}
