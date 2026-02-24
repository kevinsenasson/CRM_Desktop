package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.IClientService;
import service.ICommentaireService;
import service.IDevisService;
import view.controller.modifier_devis.ControllerModifierDevis;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ControllerModifierDevisTest extends ApplicationTest {

    IDevisService devisServiceMock;
    ControllerModifierDevis controller;
    Devis devis;

    @Override
    public void start(Stage stage) throws IOException {

        devisServiceMock = mock(IDevisService.class);

        devis = new Devis();
        devis.setId(1);
        devis.setIdClient(1);
        devis.setReference("REF001");
        devis.setDateCreation(LocalDate.now().minusDays(1));
        devis.setMontant(1000.0);
        devis.setDescription("Test devis");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/modifier_devis/modifier_devis.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.initData(devis, devisServiceMock, stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    @Test
    public void clikedOnModifierDevisButton_should_save_devis(){

        clickOn("#tfReference").eraseText(devis.getReference().length() + 1).write("123456789");

        Stage stage = (Stage) lookup("#btnModifier").query().getScene().getWindow();

        clickOn("#btnModifier");

        devis.setReference("123456789");


        assertTrue(controller.isSaved());
        verify(devisServiceMock, times(1)).updateDevis(devis);
        assertFalse(stage.isShowing());
    }
}
