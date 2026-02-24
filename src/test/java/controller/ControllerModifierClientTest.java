package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Client;
import model.SegmentClient;
import model.SourceAcquisitionClient;
import model.StatutClient;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.IClientService;
import view.controller.modifier_client.ControllerModifierClient;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class ControllerModifierClientTest extends ApplicationTest {

    private ControllerModifierClient controller;
    private IClientService clientService;
    private Client client;

    @Override
    public void start(Stage stage) throws Exception {
        // Mock du service
        clientService = mock(IClientService.class);

        // Client de test
        client = new Client();
        client.setNom("AncienNom");
        client.setPrenom("AncienPrenom");
        client.setSociete("AncienneSociete");
        client.setEmail("email@gmail.com");
        client.setTelephone("0606060606");
        client.setAdresse("1 rue Exemple");
        client.setCodePostal("75000");
        client.setVille("Paris");
        client.setPays("France");
        client.setDateInscription(LocalDate.now());
        client.setStatut(StatutClient.PROSPECT);
        client.setSegment(SegmentClient.PME);
        client.setSourceAcquisition(SourceAcquisitionClient.RECOMMANDATION);

        // Chargement du FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/modifier_client/modifier_client.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setClientService(clientService);
        controller.setStage(stage);
        controller.initData(client);

        // Affichage
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void handleUpdateClient_should_call_updateClient_and_clearUiError() throws Exception {
        clickOn("#tfNom").eraseText(client.getNom().length() + 1);
        clickOn("#tfNom").write("NouveauNom");

        clickOn("#btnEnregistrer");

        client.setNom("NouveauNom");

        verify(clientService, times(1)).updateClient(client);
    }
}