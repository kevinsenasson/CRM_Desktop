package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Devis;
import model.StatutDevis;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.testfx.framework.junit5.ApplicationTest;
import service.IDevisService;
import view.controller.ajouter_devis.ControllerAjouterDevis;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class ControllerAjouterDevisTest extends ApplicationTest {

    IDevisService devisServiceMock;
    ControllerAjouterDevis controller;

    @Override
    public void start(Stage stage) throws IOException {
        Integer idClient = 1;
        devisServiceMock = mock(IDevisService.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajouter_devis/ajouter_devis.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.initData(idClient, devisServiceMock, stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    void clikedOnAjouterDevisButton_should_save_devis(){
        clickOn("#tfReference").write("123456789");
        clickOn("#tfMontant").write("1000");
        clickOn("#taDescription").write("Test devis");
        clickOn("#cbStatut").write(StatutDevis.ENVOYE.name());

        Stage stage = (Stage) lookup("#btnEnregistrer").query().getScene().getWindow();

        clickOn("#btnEnregistrer");

        Devis devis = new Devis();
        devis.setIdClient(1);
        devis.setReference("123456789");
        devis.setMontant(1000.0);
        devis.setDescription("Test devis");
        devis.setStatut(StatutDevis.ENVOYE);
        devis.setDateCreation(LocalDate.now());

        verify(devisServiceMock, times(1)).saveDevis(devis);
        assertFalse(stage.isShowing());
    }

    @Test
    void clikedOnAnnulerButton_should_close_stage(){
        Stage stage = (Stage) lookup("#btnAnnuler").query().getScene().getWindow();

        clickOn("#btnAnnuler");

        assertFalse(stage.isShowing());
    }


    @Test
    void clikedOnAjouterDevisButton_throw_alert(){
        clickOn("#tfReference").write("123456789");
        clickOn("#tfMontant").write("abcd");
        clickOn("#taDescription").write("Test devis");
        clickOn("#cbStatut").write(StatutDevis.ENVOYE.name());

        Stage stage = (Stage) lookup("#btnEnregistrer").query().getScene().getWindow();


        assertEquals("Montant invalide", controller.lblErrorMontant.getText());
        verify(devisServiceMock, times(0)).saveDevis(any());
    }
}
