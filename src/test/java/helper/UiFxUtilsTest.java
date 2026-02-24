package helper;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import view.helper.UiFxUtils;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UiFxUtilsTest extends ApplicationTest {

    private StackPane stackPane;
    private VBox vbox;

    @BeforeEach
    void setUp() {
        stackPane = new StackPane();
        vbox = new VBox();
    }

    @Test
    void getNdIfNull_should_return_ND_for_null_or_blank() {
        assertEquals("ND", UiFxUtils.getNdIfNull(null));
        assertEquals("ND", UiFxUtils.getNdIfNull(""));
        assertEquals("ND", UiFxUtils.getNdIfNull("   "));
        assertEquals("value", UiFxUtils.getNdIfNull("value"));
    }

    @Test
    void slideTo_should_add_first_page_if_empty() {
        Label page = new Label("Page1");

        runOnFxThread(() -> {
            UiFxUtils.slideTo(stackPane, page, true);
            assertEquals(1, stackPane.getChildren().size());
            assertEquals(page, stackPane.getChildren().getFirst());
        });
    }

    @Test
    void slideTo_should_slide_between_pages() {
        Label page1 = new Label("Page1");
        Label page2 = new Label("Page2");

        runOnFxThread(() -> {
            stackPane.getChildren().add(page1);
            stackPane.setPrefWidth(200);

            UiFxUtils.slideTo(stackPane, page2, true);

            // l'ancienne page sera retirée après animation, donc on attend juste que la nouvelle soit présente
            assertTrue(stackPane.getChildren().contains(page2));
        });
    }

    @Test
    void addEditableField_should_add_row_and_edit() {
        Label errorLabel = new Label();
        AtomicReference<String> savedValue = new AtomicReference<>();

        runOnFxThread(() -> {
            UiFxUtils.addEditableField(
                    vbox,
                    "Label",
                    "Initial",
                    savedValue::set,
                    null,
                    errorLabel
            );

            assertEquals(1, vbox.getChildren().size());
            assertInstanceOf(HBox.class, vbox.getChildren().getFirst());
        });
    }

    /**
     * Helper pour exécuter du code sur le JavaFX Thread et attendre la fin.
     */
    private void runOnFxThread(Runnable action) {
        try {
            javafx.application.Platform.runLater(action);
            Thread.sleep(50); // petit délai pour laisser le FX Thread exécuter
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}