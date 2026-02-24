package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import model.Commercial;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.ICommercialService;
import view.app.AppContext;
import view.controller.ajouter_commercial.ControllerAjouterCommercial;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ControllerAjouterCommercialTest extends ApplicationTest {
    ControllerAjouterCommercial controller;

    ICommercialService commercialService;
    AppContext appContext;

    @Override
    public void start(Stage stage) throws IOException {
        commercialService= mock(ICommercialService.class);
        appContext = mock(AppContext.class);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ajouter_commercial/ajouter_commercial.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setAppContext(appContext);
        controller.setCommercialService(commercialService);

        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    }

    @Test
    public void testSaveCommercial(){
        clickOn("#tfNom").write("Dupont");
        clickOn("#tfPrenom").write("24 Avenue Saint Jean");
        clickOn("#tfMail").write("jean.dupont@example.com");
        clickOn("#tfTelephone").write("0123456789");
        clickOn("#dpDateEmbauche").write("23/11/2021");
        clickOn("#tfObjCa").write("100000");

        clickOn("#btnValider");

        verify(commercialService).saveCommercial(any());
    }

}
