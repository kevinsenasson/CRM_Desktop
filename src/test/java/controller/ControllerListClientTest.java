package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import service.IClientService;
import service.ICommentaireService;
import view.app.AppContext;
import view.controller.liste_clients.ControllerListClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerListClientTest extends ApplicationTest {

    private ControllerListClient controller;

    private IClientService clientService;
    private ICommentaireService commentaireService;
    private AppContext appContext;

    @Override
    public void start(Stage stage) throws IOException {
        Client client = new Client();
        client.setId(1);
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

        Client client2 = new Client();
        client2.setId(2);
        client2.setNom("AncienNom2");
        client2.setPrenom("AncienPrenom2");
        client2.setSociete("AncienneSociete2");
        client2.setEmail("email@gmail.com2");
        client2.setTelephone("0606060602");
        client2.setAdresse("2 rue Exemple");
        client2.setCodePostal("75000");
        client2.setVille("Paris");
        client2.setPays("France");
        client2.setDateInscription(LocalDate.now());
        client2.setStatut(StatutClient.PROSPECT);
        client2.setSegment(SegmentClient.PME);
        client2.setSourceAcquisition(SourceAcquisitionClient.RECOMMANDATION);
        clientService = mock(IClientService.class);
        commentaireService = mock(ICommentaireService.class);
        appContext = mock(AppContext.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/liste_clients/liste_client.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setAppContext(appContext);
        controller.setClientService(clientService);
        controller.setCommentaireService(commentaireService);


        when(clientService.findAll()).thenReturn(List.of(client, client2));
        Commercial commercial = new Commercial();
        commercial.setId(1);
        commercial.setNom("Dupont");
        commercial.setPrenom("Jean");

        Enterprise enterprise = new Enterprise();
        enterprise.setId(1);
        enterprise.setNom("Test Entreprise");
        enterprise.setAdresse("123 Rue Test");
        enterprise.setCodePostal("75000");
        enterprise.setVille("Paris");
        enterprise.setTelephone("0102030405");
        when(appContext.getCommercial()).thenReturn(commercial);
        when(appContext.getEnterprise()).thenReturn(enterprise);

        controller.loadClients();
        controller.initData();

        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    }

    @Test
    public void clickOnAjouterClientButton_should_load_ajouter_client_view(){
        clickOn("#btnAjouterClient");

        List<Stage> stages = new ArrayList<>(Stage.getWindows().stream()
                .filter(Window::isShowing)
                .map(window -> (Stage) window)
                .toList());

        assertFalse(stages.isEmpty());

        Stage popupStage = stages.getLast();
        assertTrue(popupStage.getTitle().contains("Ajouter un Client"));
    }

    @Test
    public void clickOnParametresButton_should_load_parametres_view(){
        clickOn("#btnSettings");

        List<Stage> stages = new ArrayList<>(Stage.getWindows().stream()
                .filter(Window::isShowing)
                .map(window -> (Stage) window)
                .toList());

        assertFalse(stages.isEmpty());

        Stage popupStage = stages.getLast();
        assertTrue(popupStage.getTitle().contains("Paramètres"));
    }

}