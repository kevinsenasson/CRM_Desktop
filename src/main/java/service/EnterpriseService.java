package service;

import dao.IEnterpriseDAO;
import exception.EnterpriseNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Enterprise;
import validation.validator.Assertion;
import validation.validator.EnterpriseValidator;

/**
 * Service de gestion des entreprises.
 * <p>
 * Cette classe fournit les opérations CRUD pour l'entité {@link Enterprise} et
 * encapsule la logique métier associée, y compris la validation des données
 * et la gestion des exceptions personnalisées.
 * </p>
 * <p>
 * Elle s'appuie sur un {@link IEnterpriseDAO} pour effectuer les opérations
 * de persistance en base de données.
 * </p>
 * <p>
 * Toutes les méthodes de modification ou d'enregistrement valident les
 * données via {@link EnterpriseValidator} et utilisent {@link Assertion}
 * pour vérifier les arguments avant toute exécution.
 * </p>
 */
@Slf4j
public class EnterpriseService implements IEnterpriseService {

    private final IEnterpriseDAO enterpriseDAO;

    /**
     * Constructeur.
     *
     * @param enterpriseDAO DAO utilisé pour la persistance des entreprises
     */
    public EnterpriseService(IEnterpriseDAO enterpriseDAO){
        this.enterpriseDAO = enterpriseDAO;
    }

    /**
     * Enregistre une nouvelle entreprise.
     *
     * @param enterprise l'entreprise à enregistrer, ne peut pas être null
     * @throws IllegalArgumentException si l'entreprise est null
     */
    @Override
    public void saveEnterprise(Enterprise enterprise){
        Assertion.assertNotNull(enterprise, "l'entreprise ne peut pas être null pour l'enregistrer");
        EnterpriseValidator.validateEnterprise(enterprise);
        log.info("Enregistrement de l'entreprise");
        enterpriseDAO.saveEnterprise(enterprise);
    }

    /**
     * Récupère l'entreprise existante.
     *
     * @return l'entreprise si elle existe, null sinon
     */
    @Override
    public Enterprise getEnterprise(){
        log.info("Chargement de l'entreprise");
        return enterpriseDAO.getEnterprise();
    }

    /**
     * Met à jour les informations d'une entreprise existante.
     *
     * @param enterprise l'entreprise à mettre à jour, doit posséder un ID valide
     * @throws IllegalArgumentException    si l'entreprise est null ou si l'ID est null ou <= 0
     * @throws EnterpriseNotFoundException si aucune entreprise n'a été mise à jour en base
     */
    @Override
    public void updateEnterprise(Enterprise enterprise){
        Assertion.assertNotNull(enterprise, "L'entreprise ne peut pas être null pour l'enregistrer");
        Assertion.assertIntegerPositiveAndNotNull(
                enterprise.getId(),
                "l'id de l'entreprise ne peut pas être null ou inférieur à 0 pour la mise à jour"
        );
        EnterpriseValidator.validateEnterprise(enterprise);
        log.info("Update de l'entreprise avec id : {}", enterprise.getId());
        log.info("Nom de l'entreprise modifier {}", enterprise.getNom());
        int rows = enterpriseDAO.updateEnterprise(enterprise);
        if(rows == 0) {
            log.warn("Aucun Update Effectué pour l'entreprise, l'id : {} n'existe pas.", enterprise.getId());
            throw new EnterpriseNotFoundException("Entreprise avec l'id : " + enterprise.getId() + " n'existe pas");
        }
    }
}
