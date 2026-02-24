package dao;

import lombok.extern.slf4j.Slf4j;
import model.*;
import util.DataBaseUtil;
import util.SqlRequestClient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour gérer les opérations CRUD sur les clients.
 * <p>
 * Implémente {@link IClientDAO} et utilise JDBC pour accéder à la base de données.
 * Les méthodes sont log avec SLF4J.
 * </p>
 */
@Slf4j
public class ClientDAO implements IClientDAO{

    /**
     * Sauvegarde un client dans la base de données.
     *
     * @param client le client à enregistrer, ne doit pas être null
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public void saveClient(Client client) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestClient.SAVE_CLIENT)){

            prepareExecution(ps, client);
            ps.executeUpdate();

            log.info("Enregistrement du client {} {} effectué.", client.getPrenom(), client.getNom());

        } catch (SQLException e) {
            log.error("Erreur lors de l'enregistrement du client");
            throw new RuntimeException(e);        }
    }


    /**
     * Récupère tous les clients de la base.
     *
     * @return la liste de tous les clients
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(SqlRequestClient.GET_ALL_CLIENTS)){

            while (result.next()) {
                clients.add(createClient(result));
            }
        } catch (SQLException e) {
            log.error("Erreur lors de la requête SQL récupération de tous les clients");
            throw new RuntimeException(e);
        }
        log.info("Récuperation de tout les clients effectué : {} clients", clients.size());
        return clients;
    }

    /**
     * Récupère un client par son ID.
     *
     * @param id l'identifiant du client
     * @return le client si trouvé, sinon null
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public Client getClientById(Integer id) {
        Client client = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlRequestClient.GET_CLIENT_BY_ID)){

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    client = createClient(result);
                }
            }

            log.info("recupération du client avec l'id : {} réussie", id);

        } catch (SQLException e) {
            log.error("Erreur lors de la requête SQL récupération du client par id : {}", id);
            throw new RuntimeException(e);
        }
        return client;
    }

    /**
     * Supprime un client par son ID.
     *
     * @param id l'identifiant du client à supprimer
     * @return le nombre de lignes affectées (Zéro si le client n'existe pas.)
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public int deleteClientById(Integer id) {
        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestClient.DELETE_CLIENT)){

            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            log.info("suppression effectué du client par id : {} ", id);

            return rows;
        } catch (SQLException e) {
            log.error("Erreur lors de la suppression du client par id");
            throw new RuntimeException(e);
        }

    }

    /**
     * Met à jour un client existant.
     *
     * @param client le client à mettre à jour
     * @return le nombre de lignes affectées (Zéro si le client n'existe pas.)
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public int updateClient(Client client) {

        try (Connection connection = getConnection();
             PreparedStatement ps = connection.prepareStatement(SqlRequestClient.UPDATE_CLIENT)) {

            client.setDateDerniereModification(LocalDate.now());

            prepareExecution(ps, client);
            ps.setInt(15, client.getId());
            int rows = ps.executeUpdate();

            log.info("Update du client avec l'id {} effectué", client.getId());

            return rows;

        } catch (SQLException e){
            log.error("Une erreur est survenu lors de l'update du client avec l'id : {} ", client.getId());
            throw new RuntimeException(e);
        }

    }

    /**
     * Récupère un client avec tous ses commentaires.
     *
     * @param id l'identifiant du client
     * @return le client avec sa liste de commentaires (liste vide si aucun commentaire)
     * @throws RuntimeException en cas d'erreur SQL
     */
    @Override
    public Client getClientWithCommentsById(Integer id) {
        Client client = null;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlRequestClient.GET_CLIENT_WITH_COMMENTS)){

            statement.setInt(1, id);

            try(ResultSet result = statement.executeQuery()) {
                while (result.next()) {

                    if(client == null){
                        client = createClient(result);
                    }

                    int idCommentaire = result.getInt("com_id");

                    if(idCommentaire != 0) {

                        String dateLastModification = result.getString("com_date_derniere_modification");
                        System.out.println("LA DATE EST : " + dateLastModification);
                        LocalDate dateLM = (dateLastModification == null || dateLastModification.isBlank()) ? null : LocalDate.parse(dateLastModification);
                        Commentaire commentaire = new Commentaire(
                                idCommentaire,
                                client.getId(),
                                result.getString("commentaire"),
                                LocalDate.parse(result.getString("com_date")),
                                dateLM
                        );
                        System.out.println(commentaire);
                        client.getCommentaires().add(commentaire);
                    }
                }
            }

            log.info("Récupération du client et des commentaire effectué avec l'id : {}", id);

        } catch (SQLException e) {
            log.error("une erreur est survenue lors de la récupération du client avec les commentaires par id : {}", id);
            throw new RuntimeException(e);
        }

        return client;
    }

    /**
     * Récupère une connexion à la base.
     *
     * @return la connexion JDBC
     * @throws SQLException si la connexion échoue
     */
    private Connection getConnection() throws SQLException {
        return DataBaseUtil.getConnection();
    }

    /**
     * Convertit un String en StatutClient.
     *
     * @param statut le nom du statut
     * @return l'énumération correspondante
     */
    private StatutClient getStatutClient(String statut) {
        return StatutClient.valueOf(statut);
    }

    /**
     * Prépare les paramètres d'un PreparedStatement pour un client.
     *
     * @param ps le PreparedStatement
     * @param client le client
     * @throws SQLException en cas d'erreur SQL
     */
    private void prepareExecution(PreparedStatement ps, Client client) throws SQLException {
        ps.setString(1, client.getSociete());
        ps.setString(2, client.getNom());
        ps.setString(3, client.getPrenom());
        ps.setString(4, client.getEmail());
        ps.setString(5, client.getTelephone());
        ps.setString(6, client.getAdresse());
        ps.setString(7, client.getCodePostal());
        ps.setString(8, client.getVille());
        ps.setString(9, client.getPays());
        ps.setString(10, client.getDateInscription().toString());
        ps.setString(11, client.getStatut().name());
        ps.setString(12, client.getDateDerniereModification().toString());
        ps.setString(13, client.getSegment() == null ? null : client.getSegment().name());
        ps.setString(14, client.getSourceAcquisition() == null ? null : client.getSourceAcquisition().name());
    }

    /**
     * Crée un objet Client à partir d'un ResultSet.
     *
     * @param result le ResultSet positionné sur la ligne du client
     * @return le client créé
     * @throws SQLException en cas d'erreur SQL
     */
    private Client createClient(ResultSet result) throws SQLException {
        String segment = result.getString("segment");
        String sourceAcquisition = result.getString("source_acquisition");

        SegmentClient segmentClient = segment == null ? null : SegmentClient.valueOf(segment);
        SourceAcquisitionClient sourceAcquisitionClient = sourceAcquisition == null ? null : SourceAcquisitionClient.valueOf(sourceAcquisition);
        return new Client(
                result.getInt("id"),
                result.getString("societe"),
                result.getString("nom"),
                result.getString("prenom"),
                result.getString("email"),
                result.getString("telephone"),
                result.getString("adresse"),
                result.getString("code_postal"),
                result.getString("ville"),
                result.getString("pays"),
                LocalDate.parse(result.getString("date_inscription")),
                getStatutClient(result.getString("statut")),
                LocalDate.parse(result.getString("date_derniere_modification")),
                segmentClient,
                sourceAcquisitionClient,
                new ArrayList<>()
        );
    }
}
