package service;

import dao.IEnterpriseDAO;
import exception.EnterpriseNotFoundException;
import model.Enterprise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnterpriseServiceTest {

    @InjectMocks
    private EnterpriseService enterpriseService;

    @Mock
    private IEnterpriseDAO enterpriseDAO;

    private Enterprise enterprise;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        enterprise = new Enterprise();
        enterprise.setId(1);
        enterprise.setNom("Test Entreprise");
        enterprise.setAdresse("123 Rue Test");
        enterprise.setCodePostal("75000");
        enterprise.setVille("Paris");
        enterprise.setTelephone("0102030405");
        enterprise.setEmail("test@entreprise.com");
        enterprise.setSiret("73282932000074");
    }

    @Test
    void saveEnterprise_ShouldCallDao() {
        enterpriseService.saveEnterprise(enterprise);
        verify(enterpriseDAO, times(1)).saveEnterprise(enterprise);
    }

    @Test
    void getEnterprise_ShouldReturnEnterprise() {
        when(enterpriseDAO.getEnterprise()).thenReturn(enterprise);

        Enterprise result = enterpriseService.getEnterprise();

        assertNotNull(result);
        assertEquals("Test Entreprise", result.getNom());
        verify(enterpriseDAO, times(1)).getEnterprise();
    }

    @Test
    void updateEnterprise_ShouldCallDao_WhenValid() {
        when(enterpriseDAO.updateEnterprise(enterprise)).thenReturn(1);

        assertDoesNotThrow(() -> enterpriseService.updateEnterprise(enterprise));
        verify(enterpriseDAO, times(1)).updateEnterprise(enterprise);
    }

    @Test
    void updateEnterprise_ShouldThrowException_WhenNoRowsUpdated() {
        when(enterpriseDAO.updateEnterprise(enterprise)).thenReturn(0);

        EnterpriseNotFoundException exception = assertThrows(
                EnterpriseNotFoundException.class,
                () -> enterpriseService.updateEnterprise(enterprise)
        );

        assertTrue(exception.getMessage().contains("n'existe pas"));
        verify(enterpriseDAO, times(1)).updateEnterprise(enterprise);
    }

    @Test
    void saveEnterprise_ShouldThrowException_WhenEnterpriseIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> enterpriseService.saveEnterprise(null)
        );

        assertTrue(exception.getMessage().contains("ne peut pas être null"));
        verifyNoInteractions(enterpriseDAO);
    }

    @Test
    void updateEnterprise_ShouldThrowException_WhenEnterpriseIsNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> enterpriseService.updateEnterprise(null)
        );

        assertTrue(exception.getMessage().contains("ne peut pas être null"));
        verifyNoInteractions(enterpriseDAO);
    }

    @Test
    void updateEnterprise_ShouldThrowException_WhenIdInvalid() {
        enterprise.setId(0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> enterpriseService.updateEnterprise(enterprise)
        );

        assertTrue(exception.getMessage().contains("ne peut pas être null ou inférieur à 0"));
        verifyNoInteractions(enterpriseDAO);
    }
}