package view.controller.menu;


import dao.CommercialDAO;
import dao.EnterpriseDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import lombok.Setter;
import service.CommercialService;
import service.EnterpriseService;
import view.app.AppContext;
import view.controller.infos_commercial.InfosCommercialController;
import view.controller.infos_entreprise.InfosEntrepriseController;
import view.helper.UiFxUtils;

import java.io.IOException;

/**
 * Contrôleur de l'écran de paramètres de l'application.
 * <p>
 * Ce contrôleur permet à l'utilisateur d'accéder aux informations commerciales et aux informations
 * de l'entreprise via des HBox cliquables. Il gère :
 * <ul>
 *     <li>La navigation vers la vue des informations du commercial</li>
 *     <li>La navigation vers la vue des informations de l'entreprise</li>
 *     <li>Le comportement visuel au survol et à la sortie des HBox</li>
 * </ul>
 */
public class ParameterController {

    @Setter AppContext appContext;

    @FXML private HBox boxEntreprise, boxInfos;
    @FXML private StackPane contentPane;

    /**
     * Ouvre la vue des informations du commercial dans le contentPane.
     *
     * @param event événement de clic sur la HBox correspondante
     * @throws IOException en cas d'erreur de chargement du FXML
     */
    @FXML
    private void openInfos(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/infos_commercial/infos_commercial.fxml"));
        Parent root = loader.load();
        InfosCommercialController controller = loader.getController();
        controller.setAppContext(appContext);
        controller.setCommercialService(new CommercialService(new CommercialDAO()));
        UiFxUtils.slideTo(contentPane, root, true);
    }

    /**
     * Ouvre la vue des informations de l'entreprise dans le contentPane.
     *
     * @param event événement de clic sur la HBox correspondante
     * @throws IOException en cas d'erreur de chargement du FXML
     */
    @FXML
    private void openInfosEnterprise(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/infos_entreprise/infos_entreprise.fxml"));
        Parent root = loader.load();
        InfosEntrepriseController controller = loader.getController();
        controller.setAppContext(appContext);
        controller.setEnterpriseService(new EnterpriseService(new EnterpriseDAO()));
        UiFxUtils.slideTo(contentPane, root, true);
    }

    /**
     * Change le style visuel de la HBox lorsqu'elle est survolée par la souris.
     *
     * @param event événement de survol
     */
    @FXML
    private void hoverBox(MouseEvent event) {
        HBox box = (HBox) event.getSource();
        box.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 20; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ddd;");
    }

    /**
     * Réinitialise le style visuel de la HBox lorsqu'elle n'est plus survolée par la souris.
     *
     * @param event événement de sortie
     */
    @FXML
    private void exitBox(MouseEvent event) {
        HBox box = (HBox) event.getSource();
        box.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #ddd;");
    }
}
