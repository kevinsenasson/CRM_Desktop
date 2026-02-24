package view.app;

import lombok.Getter;
import lombok.Setter;
import model.Commercial;
import model.Enterprise;
import service.ICommercialService;
import service.IEnterpriseService;

/**
 * Contexte global de l'application.
 *
 * <p>
 * Cette classe centralise l'état courant de l'application, notamment :
 * <ul>
 *     <li>L'entreprise courante ({@link Enterprise})</li>
 *     <li>Le commercial courant ({@link Commercial})</li>
 * </ul>
 * Elle fournit également l'accès aux services métiers pour gérer ces entités.
 * </p>
 *
 * <p>
 * Elle est utilisée pour partager l'état entre les différentes vues JavaFX
 * et les controllers.
 * </p>
 */
public class AppContext {

    /** L'entreprise courante dans le contexte */
    @Getter
    @Setter
    private Enterprise enterprise;

    /** Le commercial courant dans le contexte */
    @Getter
    @Setter
    private Commercial commercial;

    /** Service métier pour les commerciaux */
    private final ICommercialService commercialService;
    /** Service métier pour les entreprises */
    private final IEnterpriseService enterpriseService;

    /**
     * Constructeur du contexte avec services injectés.
     *
     * <p>
     * Permet d'utiliser des implémentations personnalisées ou mock
     * pour les tests ou pour des configurations différentes.
     * Initialise les données courantes depuis les services.
     * </p>
     *
     * @param commercialService service métier pour gérer les commerciaux
     * @param enterpriseService service métier pour gérer les entreprises
     */
    public AppContext(ICommercialService commercialService, IEnterpriseService enterpriseService) {
        this.commercialService = commercialService;
        this.enterpriseService = enterpriseService;
        refreshData();
    }

    /**
     * Rafraîchit les données de l'application depuis les services.
     *
     * <p>
     * Met à jour {@link #commercial} et {@link #enterprise} avec les données
     * actuelles de la base via les services.
     * </p>
     */
    public void refreshData(){
        this.commercial = commercialService.getCommercial();
        this.enterprise = enterpriseService.getEnterprise();
    }
}
