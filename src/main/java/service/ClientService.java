package service;

import dao.IClientDAO;
import exception.ClientNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Client;
import model.Commentaire;
import validation.validator.Assertion;
import validation.validator.ClientValidator;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Service pour gérer les opérations métier liées aux clients.
 * <p>
 * Cette classe encapsule la logique métier autour des clients et utilise
 * un DAO pour interagir avec la base de données. Elle gère :
 * <ul>
 *     <li>La création et la validation des clients</li>
 *     <li>La récupération des clients et de leurs commentaires</li>
 *     <li>La mise à jour et la suppression sécurisée des clients</li>
 *     <li>La gestion des exceptions métier (client non trouvé)</li>
 * </ul>
 */
@Slf4j
public class ClientService implements IClientService{
    private final IClientDAO clientDAO;

    /**
     * Constructeur du service avec injection du DAO client.
     *
     * @param clientDAO DAO pour accéder aux clients en base
     */
    public ClientService(IClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }


    /**
     * Enregistre un client après validation.
     * La date d'inscription et la date de dernière modification sont automatiquement définies.
     *
     * @param client le client à enregistrer (non null)
     * @throws IllegalArgumentException si le client est null ou invalide
     */
    @Override
    public void saveClient(Client client) {
        Assertion.assertNotNull(client, "Le client ne peut être null pour l'enregistrer");
        ClientValidator.validateClient(client);
        log.info("Enregistrement du client");
        client.setDateInscription(LocalDate.now());
        client.setDateDerniereModification(LocalDate.now());
        clientDAO.saveClient(client);
    }

    /**
     * Récupère tous les clients existants.
     *
     * @return liste des clients
     */
    @Override
    public List<Client> findAll() {
        log.info("Récupération de tous les clients");
        return clientDAO.findAll();
    }

    /**
     * Récupère un client par son identifiant.
     *
     * @param id identifiant du client (positif et non null)
     * @return le client correspondant
     * @throws ClientNotFoundException si aucun client n'est trouvé
     * @throws IllegalArgumentException si l'id est invalide
     */
    @Override
    public Client getClientById(Integer id) {
        Assertion.assertIntegerPositiveAndNotNull(id,
                "L'id ne peut être null ou négatif ou égale à 0 pour récupérer un client");
        log.info("récupération du client par l'id : {} ", id);

        Client client = clientDAO.getClientById(id);

        if(client == null) {
            throw new ClientNotFoundException("Le client avec l'id : " + id + " n'existe pas");
        }

        return client;
    }

    /**
     * Supprime un client par son identifiant.
     *
     * @param id identifiant du client (positif et non null)
     * @throws ClientNotFoundException si aucun client n'est supprimé
     * @throws IllegalArgumentException si l'id est invalide
     */
    @Override
    public void deleteClientById(Integer id) {
        Assertion.assertIntegerPositiveAndNotNull(id,
                "L'id ne peut être null ou négatif ou égale à 0 pour supprimer un client" );
        log.info("Suppression du client par l'id : {} ", id);
        int rows = clientDAO.deleteClientById(id);

        if(rows == 0) {
            log.warn("Tentative de suppression d'un client inexistant : {}", id);
            throw new ClientNotFoundException("Le client avec l'id : " + id + " n'existe pas");
        }
    }

    /**
     * Met à jour les informations d'un client après validation.
     * La date de dernière modification est automatiquement mise à jour.
     *
     * @param client le client à mettre à jour (non null)
     * @throws ClientNotFoundException si le client n'existe pas
     * @throws IllegalArgumentException si le client ou son id est invalide
     */
    @Override
    public void updateClient(Client client) {
        Assertion.assertNotNull(client, "Le client ne peut être null pour effectuer sa mise à jour");
        Assertion.assertIntegerPositiveAndNotNull(client.getId(),
                "L'id du client ne peut pas être invalide pour la mis à jour");
        ClientValidator.validateClient(client);
        log.info("Modification du client avec l'id : {}", client.getId());
        client.setDateDerniereModification(LocalDate.now());
        int rows = clientDAO.updateClient(client);

        if(rows == 0) {
            log.warn("Tentative de mise à jour d'un client inexistant : {}", client.getId());
            throw new ClientNotFoundException("Le client avec l'id : " + client.getId() + " n'existe pas");
        }
    }

    /**
     * Récupère un client avec tous ses commentaires triés par date croissante.
     *
     * @param id identifiant du client (positif et non null)
     * @return le client avec sa liste de commentaires
     * @throws ClientNotFoundException si le client n'existe pas
     * @throws IllegalArgumentException si l'id est invalide
     */
    @Override
    public Client getClientWithCommentsById(Integer id) {
        Assertion.assertIntegerPositiveAndNotNull(id,
                "L'id ne peut être null ou négatif ou égale à 0 pour récupérer un client et ses commentaires");
        log.info("Récupération du client avec les commentaires par l'id : {}", id);

        Client client = clientDAO.getClientWithCommentsById(id);

        if(client == null) {
            throw new ClientNotFoundException("Le client avec l'id : " + id + " n'existe pas");
        }

        List<Commentaire> sortedCommentaire = client.getCommentaires().stream()
                .sorted(Comparator.comparing(Commentaire::getDate))
                .toList();

        client.setCommentaires(sortedCommentaire);

        return client;
    }
}
