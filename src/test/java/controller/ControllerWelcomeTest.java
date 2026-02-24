package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Commercial;
import model.Enterprise;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import service.IEnterpriseService;
import view.app.AppContext;
import view.controller.welcome.WelcomeController;

import static org.mockito.Mockito.*;

public class ControllerWelcomeTest extends ApplicationTest {
    WelcomeController controller;

    AppContext appContext;
    IEnterpriseService mockEnterpriseService;

    @Override
    public void start(Stage stage) throws Exception {

        appContext = mock(AppContext.class);
        mockEnterpriseService = mock(IEnterpriseService.class);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/welcome/welcome.fxml"));
        Parent root = loader.load();

        controller = loader.getController();
        controller.setEnterpriseService(mockEnterpriseService);
        controller.setAppContext(appContext);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testSaveEnterprise() {
        clickOn("#tfNom").write("Dupont");
        clickOn("#tfAdresse").write("24 Avenue Saint Jean");
        clickOn("#tfCodePostal").write("06523");
        clickOn("#tfVille").write("jean.dupont@example.com");
        clickOn("#tfTelephone").write("0123456789");
        clickOn("#tfEmail").write("dupont@gmail.com");
        clickOn("#tfSiret").write("73282932000074");

        clickOn("#btnValider");

        verify(mockEnterpriseService).saveEnterprise(any(Enterprise.class));
    }
}
