package service;

import dao.ICommercialDAO;
import exception.CommercialNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Commercial;
import validation.validator.Assertion;
import validation.validator.CommercialValidator;

/**
 * Service de gestion des commerciaux.
 * <p>
 * Cette classe implémente les opérations métiers liées aux commerciaux,
 * telles que l'enregistrement, la récupération et la mise à jour.
 * Elle effectue la validation des données avant d'interagir avec le DAO
 * et déclenche des exceptions spécifiques en cas d'erreur.
 * </p>
 *
 * <ul>
 *     <li>{@link #saveCommercial(Commercial)} : enregistre un commercial valide en base.</li>
 *     <li>{@link #getCommercial()} : récupère le commercial existant en base.</li>
 *     <li>{@link #updateCommercial(Commercial)} : met à jour un commercial existant.</li>
 * </ul>
 *
 * <p>
 * La classe utilise {@link ICommercialDAO} pour interagir avec la base de données
 * et {@link CommercialValidator} pour valider les données métiers.
 * </p>
 *
 */
@Slf4j
public class CommercialService implements ICommercialService {

    private final ICommercialDAO commercialDAO;

    /**
     * Constructeur du service avec injection du DAO.
     *
     * @param commercialDAO DAO des commerciaux
     */
    public CommercialService(ICommercialDAO commercialDAO) {
        this.commercialDAO = commercialDAO;
    }

    /**
     * Enregistre un commercial en base de données après validation.
     *
     * @param commercial Commercial à enregistrer (non null)
     * @throws IllegalArgumentException si le commercial est null ou invalide
     */
    @Override
    public void saveCommercial(Commercial commercial) {
        Assertion.assertNotNull(commercial, "le commercial ne peut pas être null pour l'enregistrer");
        CommercialValidator.validateCommercial(commercial);
        log.info("Enregistrement du commercial");
        commercialDAO.saveCommercial(commercial);
    }


    /**
     * Récupère le commercial existant en base.
     *
     * @return Commercial existant ou null si aucun commercial n'est présent
     */
    @Override
    public Commercial getCommercial(){
        log.info("Chargement du commercial");
        Commercial commercial = commercialDAO.getCommercial();
        if(commercial == null) {
            log.warn("Aucun commercial n'existe en base");
        }
        return commercial;
    }

    /**
     * Met à jour un commercial existant en base de données après validation.
     *
     * @param commercial Commercial à mettre à jour (non null)
     * @throws IllegalArgumentException si le commercial est null ou si son ID est invalide
     * @throws CommercialNotFoundException si aucun commercial n'existe avec l'ID fourni
     */
    @Override
    public void updateCommercial(Commercial commercial){
        Assertion.assertNotNull(commercial, "Le commercial ne peut pas être null pour la mise à jour");
        Assertion.assertIntegerPositiveAndNotNull(commercial.getId(), "L'id du commercial ne peut pas être null ou négatif");
        CommercialValidator.validateCommercial(commercial);
        log.info("Mise à jour du commercial avec id {}", commercial.getId());
        int rows = commercialDAO.updateCommercial(commercial);
        if (rows == 0){
            log.warn("L'id du commercial n'existe pas en base : {}", commercial.getId());
            throw new CommercialNotFoundException("L'id du commercial n'existe pas en base");
        }
    }
}
