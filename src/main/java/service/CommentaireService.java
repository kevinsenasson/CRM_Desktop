package service;

import dao.ICommentaireDAO;
import exception.CommentaireNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Commentaire;
import org.slf4j.Logger;
import validation.validator.Assertion;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/**
 * Service pour la gestion des commentaires liés aux clients.
 * <p>
 * Ce service s'occupe de la logique métier pour :
 * <ul>
 *     <li>Enregistrer un nouveau commentaire</li>
 *     <li>Récupérer tous les commentaires d'un client triés par date</li>
 *     <li>Mettre à jour un commentaire existant</li>
 *     <li>Supprimer un commentaire</li>
 * </ul>
 * <p>
 * Il valide les entrées, s'assure que le client existe et déclenche des exceptions personnalisées
 * si les opérations ne peuvent pas être effectuées (par exemple, commentaire inexistant).
 */
@Slf4j
public class CommentaireService implements ICommentaireService{
    private final ICommentaireDAO commentaireDAO;
    private final IClientService clientService;

    /**
     * Constructeur du service Commentaire.
     *
     * @param commentaireDAO DAO pour gérer les opérations sur les commentaires
     * @param clientService Service pour vérifier l'existence des clients
     */
    public CommentaireService(ICommentaireDAO commentaireDAO, IClientService clientService) {
        this.commentaireDAO = commentaireDAO;
        this.clientService = clientService;
    }

    /**
     * Enregistre un commentaire pour un client existant.
     * <p>
     * Définit automatiquement la date de création du commentaire à la date actuelle.
     * Vérifie que le commentaire et le client existent.
     *
     * @param commentaire le commentaire à enregistrer
     * @throws IllegalArgumentException si le commentaire est null
     */
    @Override
    public void save(Commentaire commentaire) {
        log.info("Enregistrement du commentaire");
        commentaire.setDate(LocalDate.now());
        Assertion.assertNotNull(commentaire, "Le commentaire ne peut pas être null pour l'enregistrer");
        clientService.getClientById(commentaire.getIdClient());
        commentaireDAO.saveCommentaire(commentaire);
    }

    /**
     * Récupère tous les commentaires d'un client triés par date.
     *
     * @param id l'identifiant du client
     * @return la liste des commentaires triés par date
     * @throws IllegalArgumentException si l'id est null, négatif ou égal à 0
     */
    @Override
    public List<Commentaire> getCommentaireByClientId(Integer id) {
        Assertion.assertIntegerPositiveAndNotNull(id, "l'id ne peut pas être null ou négatif ou égale à 0");
        log.info("Chargement des commentaires par l'id du client : {}", id);
        List<Commentaire> commentaires = commentaireDAO.getCommentairesByClientId(id);
        return commentaires.stream()
                .sorted(Comparator.comparing(Commentaire::getDate))
                .toList();
    }

    /**
     * Supprime un commentaire existant par son identifiant.
     *
     * @param id l'identifiant du commentaire à supprimer
     * @throws IllegalArgumentException si l'id est null, négatif ou égal à 0
     * @throws CommentaireNotFoundException si le commentaire n'existe pas
     */
    @Override
    public void deleteCommentaireById(Integer id) {
        Assertion.assertIntegerPositiveAndNotNull(id, "l'id ne peut pas être null ou négatif ou égale à 0");

        log.info("Suppression du commentaire par l'id : {}", id);

        int rows = commentaireDAO.deleteCommentaireById(id);
        if(rows == 0) {
            log.warn("Tentative de suppression d'un commentaire inexistant : {}", id);
            throw new CommentaireNotFoundException(
                    "Le commentaire avec l'id : " + id + " n'existe pas"
            );
        }
    }

    /**
     * Met à jour un commentaire existant.
     * <p>
     * Définit automatiquement la date de dernière modification à la date actuelle.
     *
     * @param commentaire le commentaire à mettre à jour
     * @throws IllegalArgumentException si le commentaire est null ou si l'id est invalide
     * @throws CommentaireNotFoundException si le commentaire n'existe pas
     */
    @Override
    public void updateCommentaire(Commentaire commentaire) {
        Assertion.assertNotNull(commentaire, "Le commentaire ne peut pas être null pour le mettre à jour");
        Assertion.assertIntegerPositiveAndNotNull(
                commentaire.getId(), "L'id du commentaire est invalide pour la mise à jour"
        );

        commentaire.setDateDerniereModification(LocalDate.now());

        log.info("Modification du commentaire par l'id : {}", commentaire.getId());

        int rows = commentaireDAO.updateCommentaire(commentaire);

        if(rows == 0) {
            log.warn("Tentative de mise à jour d'un commentaire inexistant : {}", commentaire.getId());
            throw new CommentaireNotFoundException(
                    "Le commentaire avec l'id : " + commentaire.getId() + " n'existe pas"
            );
        }
    }
}
