package helper;

import exception.AppAssertException;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.testfx.framework.junit5.ApplicationTest;
import view.helper.UiErrorHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UiErrorHandlerTest extends ApplicationTest {

    private Label label;

    @BeforeEach
    void setUp() {
        label = new Label();
        UiErrorHandler.clear(label);
    }

    @Test
    void showInLabel_should_set_text_and_visible() {
        UiErrorHandler.showInLabel("Erreur test", label);
        assertEquals("Erreur test", label.getText());
        assertTrue(label.isVisible());
    }

    @Test
    void clear_should_reset_label() {
        label.setText("xx");
        label.setVisible(true);

        UiErrorHandler.clear(label);

        assertEquals("", label.getText());
        assertFalse(label.isVisible());
    }

    @Test
    void handle_should_show_label_for_assert_exception() {
        AppAssertException ex = new AppAssertException("assert fail");
        UiErrorHandler.handle(ex, label);

        assertEquals("assert fail", label.getText());
        assertTrue(label.isVisible());
    }

    @Test
    void handle_should_show_warning_alert_for_illegal_argument() {
        IllegalArgumentException ex = new IllegalArgumentException("bad arg");

        try (MockedConstruction<Alert> mocked = mockConstruction(Alert.class)) {
            UiErrorHandler.handle(ex, label);

            // Vérifie qu'un Alert a été construit
            assertEquals(1, mocked.constructed().size());
        }
    }

    @Test
    void handle_should_show_error_alert_for_other_exception() {
        RuntimeException ex = new RuntimeException("boom");

        try (MockedConstruction<Alert> mocked = mockConstruction(Alert.class)) {
            UiErrorHandler.handle(ex, label);

            // Vérifie qu'un Alert a été construit
            assertEquals(1, mocked.constructed().size());
        }
    }
}