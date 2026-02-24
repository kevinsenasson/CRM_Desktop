package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;
import model.SegmentClient;
import model.SourceAcquisitionClient;
import model.StatutClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.IClientService;
import view.controller.ajouter_client.ControllerAjouterClient;
import static org.mockito.Mockito.*;

public class ControllerAjouterClientTest extends ApplicationTest {

    private ControllerAjouterClient controller;
    private IClientService clientServiceMock;

    @Override
    public void start(Stage stage) throws Exception {
        // Mock du service
        clientServiceMock = mock(IClientService.class);

        // Chargement du FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajouter_client/ajouter_client.fxml"));
        Parent root = loader.load();

        // Récupération du controller
        controller = loader.getController();
        controller.setClientService(clientServiceMock);
        controller.setStage(stage);

        // Affichage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void setUp() {
        // Reset des mocks avant chaque test
        reset(clientServiceMock);
    }

    @Test
    void enregistrerClient_should_call_service_and_close_stage() {
        // Remplissage des champs
        clickOn("#tfNom").write("Dupont");
        clickOn("#tfPrenom").write("Jean");
        clickOn("#tfSociete").write("Acme Corp");
        clickOn("#tfEmail").write("jean.dupont@example.com");
        clickOn("#tfTelephone").write("0123456789");
        clickOn("#tfCodePostal").write("75001");

        // Sélection dans les ComboBox
        clickOn("#cbStatut").clickOn(StatutClient.PROSPECT.name());
        clickOn("#cbSegment").clickOn(SegmentClient.PME.name());
        clickOn("#cbSourceAcquisition").clickOn(SourceAcquisitionClient.ANNONCE.name());

        // Action sur le bouton enregistrer
        clickOn("#btnEnregistrer");

        // Vérification que le service a été appelé
        verify(clientServiceMock, times(1)).saveClient(any(Client.class));
    }
}