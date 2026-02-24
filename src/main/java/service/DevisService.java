package service;

import dao.ICommercialDAO;
import dao.IDevisDao;
import exception.DevisNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Commercial;
import model.Devis;
import model.StatutDevis;
import validation.validator.Assertion;
import validation.validator.DevisValidator;

import java.util.Comparator;
import java.util.List;

/**
 * Service métier pour la gestion des devis.
 * <p>
 * Cette classe assure :
 * <ul>
 *     <li>La validation des devis avant persistance.</li>
 *     <li>La gestion des opérations CRUD (création, lecture, mise à jour, suppression).</li>
 *     <li>Le recalcul du chiffre d'affaires du commercial pour les devis acceptés.</li>
 * </ul>
 * <p>
 * Elle utilise les DAO {@link IDevisDao} et {@link ICommercialDAO} pour l'accès aux données.
 **/
@Slf4j
public class DevisService implements IDevisService {

    private final IDevisDao devisDao;
    private final ICommercialDAO commercialDao;

    /**
     * Constructeur de DevisService.
     *
     * @param devisDao DAO pour la gestion des devis
     * @param commercialDao DAO pour la gestion du commercial
     */
    public DevisService(IDevisDao devisDao, ICommercialDAO commercialDao){
        this.devisDao = devisDao;
        this.commercialDao = commercialDao;
    }

    /**
     * Enregistre un nouveau devis.
     * <p>
     * Valide le devis, le persiste via le DAO et met à jour le chiffre d'affaires du commercial.
     *
     * @param devis le devis à enregistrer (ne peut pas être null)
     * @throws IllegalArgumentException si le devis est null ou invalide
     */
    @Override
    public void saveDevis(Devis devis){
        Assertion.assertNotNull(devis, "Le devis ne peut pas être null pour l'enregistrer");
        DevisValidator.validateDevis(devis);
        log.info("Enregistrement du devis");
        devisDao.saveDevis(devis);
        updateCaOfCommercial();
    }

    /**
     * Supprime un devis existant par son identifiant.
     *
     * @param id l'identifiant du devis à supprimer (doit être positif et non null)
     * @throws DevisNotFoundException si le devis n'existe pas
     */
    @Override
    public void deleteDevisById(Integer id){
        Assertion.assertIntegerPositiveAndNotNull(id, "L'id ne peut pas être null ou négatif");
        log.info("Suppression du devis par l'id : {}", id);
        int rows = devisDao.deleteDevisById(id);

        if(rows == 0){
            log.warn("Tentative de suppression d'un devis inexistant : {}", id);
            throw new DevisNotFoundException("Le devis avec l'id : " + id + " n'existe pas");
        }
    }

    /**
     * Récupère tous les devis d'un client donné, triés par date de création croissante.
     *
     * @param id l'identifiant du client (doit être positif et non null)
     * @return liste triée de devis du client
     * @throws IllegalArgumentException si l'identifiant est null ou négatif
     */
    @Override
    public List<Devis> getDevisByClientId(Integer id){
        Assertion.assertIntegerPositiveAndNotNull(id, "L'id ne peut pas être null ou négatif");
        log.info("Chargement des devis par l'id du client : {}", id);

        List<Devis> devis = devisDao.getDevisByClientId(id);

        return devis.stream()
                .sorted(Comparator.comparing(Devis::getDateCreation))
                .toList();
    }

    /**
     * Met à jour un devis existant.
     * <p>
     * Valide le devis, applique la mise à jour via le DAO et recalcul le CA du commercial.
     *
     * @param devis le devis à mettre à jour (ne peut pas être null)
     * @throws DevisNotFoundException si le devis n'existe pas
     * @throws IllegalArgumentException si le devis est null ou invalide
     */
    @Override
    public void updateDevis(Devis devis){
        Assertion.assertNotNull(devis, "Le devis ne peut pas être null");
        DevisValidator.validateDevis(devis);
        log.info("Modification du devis avec l'id : {}", devis.getId());
         int rows = devisDao.updateDevis(devis);

         if(rows == 0) {
             log.warn("Tentative d'update d'un devis inexistant : {}", devis.getId());
             throw new DevisNotFoundException("Le devis avec l'id : " + devis.getId() + " n'existe pas");
         }

         updateCaOfCommercial();
    }


    /**
     * Recalcule le chiffre d'affaires du commercial en fonction des devis acceptés.
     * <p>
     * Somme des montants des devis ayant le statut {@link StatutDevis#ACCEPTE}.
     */
    private void updateCaOfCommercial() {
        int montant = devisDao.findAll().stream()
                .filter(d -> d.getStatut() == StatutDevis.ACCEPTE)
                .mapToInt(d -> d.getMontant().intValue())
                .sum();

        Commercial commercial = commercialDao.getCommercial();
        commercial.setCa(montant);
        commercialDao.updateCommercial(commercial);

        log.debug("CA recalculé : {}", montant);
    }
}
