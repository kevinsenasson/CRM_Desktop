package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.*;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.IClientService;
import service.ICommentaireService;
import view.controller.detail_client.ControllerDetailClient;
import view.controller.modifier_client.ControllerModifierClient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerDetailClientTest extends ApplicationTest {

    ControllerDetailClient controller;
    IClientService clientServiceMock;
    ICommentaireService commentaireServiceMock;

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


        Commentaire commentaire = new Commentaire();
        commentaire.setIdClient(1);
        commentaire.setCommentaire("Test commentaire");
        commentaire.setDate(LocalDate.now());
        client.getCommentaires().add(commentaire);
        Commentaire commentaire2 = new Commentaire();
        commentaire2.setIdClient(1);
        commentaire2.setCommentaire("Test commentaire 2");
        commentaire2.setDate(LocalDate.now().minusDays(1));
        client.getCommentaires().add(commentaire2);

        clientServiceMock = mock(IClientService.class);
        commentaireServiceMock = mock(ICommentaireService.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detail_client/detail_client.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setClientService(clientServiceMock);
        controller.setCommentaireService(commentaireServiceMock);
        controller.setStage(stage);
        when(clientServiceMock.getClientWithCommentsById(1)).thenReturn(client);
        controller.initData(1);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void clikedOnModifierClientButton_should_load_modifier_client_view(){
        clickOn("#btnModifier");

        verify(clientServiceMock).getClientById(1);
    }

    @Test
    public void clikedOnVoirLesDevisButton_should_load_voir_les_devis_view(){
        clickOn("#btnVoirDevis");

        // récupérer tous les stages
        List<Stage> stages = new ArrayList<>(Stage.getWindows().stream()
                .filter(Window::isShowing)
                .map(window -> (Stage) window)
                .toList());

        assertFalse(stages.isEmpty());

        Stage popupStage = stages.getLast();
        assertTrue(popupStage.getTitle().contains("Liste des devis du client : AncienNom AncienPrenom"));
    }

    @Test
    public void clikedSaveLesCommentairesButton(){
        Commentaire commentaire = new Commentaire();
        commentaire.setIdClient(1);
        commentaire.setCommentaire("Test commentaire 3");

        clickOn("#tfNouveauCommentaire").write(commentaire.getCommentaire());

        clickOn("#btnAjouterCommentaire");

        verify(commentaireServiceMock).save(commentaire);
    }

    @Test
    public void clikedOnRetourButton_should_close_stage() {

        Stage stage = (Stage) lookup("#btnRetour").query().getScene().getWindow();

        clickOn("#btnRetour");

        assertFalse(stage.isShowing());
    }


}
