package service;

import dao.ICommercialDAO;
import exception.CommercialNotFoundException;
import model.Commercial;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommercialServiceTest {

    @InjectMocks
    private CommercialService commercialService;

    @Mock
    private ICommercialDAO commercialDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveCommercial_shouldCallDao_whenCommercialIsValid() {
        Commercial commercial = new Commercial();
        commercial.setNom("Dupont");
        commercial.setPrenom("Jean");
        commercial.setIdEntreprise(1);
        commercial.setCa(0);
        commercial.setObjectifCa(15000);
        commercial.setMail("email@commercial.com");
        commercial.setTelephone("0606060606");
        commercial.setDateEmbauche(LocalDate.now());

        commercialService.saveCommercial(commercial);

        verify(commercialDAO, times(1)).saveCommercial(commercial);
    }

    @Test
    void saveCommercial_shouldThrowException_whenCommercialIsNull() {
        assertThrows(IllegalArgumentException.class, () -> commercialService.saveCommercial(null));
        verify(commercialDAO, never()).saveCommercial(any());
    }

    @Test
    void getCommercial_shouldReturnCommercial_whenDaoReturnsCommercial() {
        Commercial commercial = new Commercial();
        commercial.setId(1);
        commercial.setNom("Dupont");
        when(commercialDAO.getCommercial()).thenReturn(commercial);

        Commercial result = commercialService.getCommercial();

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(commercialDAO, times(1)).getCommercial();
    }

    @Test
    void getCommercial_shouldReturnNull_whenDaoReturnsNull() {
        when(commercialDAO.getCommercial()).thenReturn(null);

        Commercial result = commercialService.getCommercial();

        assertNull(result);
        verify(commercialDAO, times(1)).getCommercial();
    }

    @Test
    void updateCommercial_shouldCallDao_whenCommercialExists() {
        Commercial commercial = new Commercial();
        commercial.setId(1);
        commercial.setNom("Dupont");
        commercial.setPrenom("Jean");
        commercial.setIdEntreprise(1);
        commercial.setCa(0);
        commercial.setObjectifCa(15000);
        commercial.setMail("email@commercial.com");
        commercial.setTelephone("0606060606");
        commercial.setDateEmbauche(LocalDate.now());

        when(commercialDAO.updateCommercial(commercial)).thenReturn(1);

        commercialService.updateCommercial(commercial);

        verify(commercialDAO, times(1)).updateCommercial(commercial);
    }

    @Test
    void updateCommercial_shouldThrowException_whenCommercialIsNull() {
        assertThrows(IllegalArgumentException.class, () -> commercialService.updateCommercial(null));
        verify(commercialDAO, never()).updateCommercial(any());
    }

    @Test
    void updateCommercial_shouldThrowException_whenIdIsNull() {
        Commercial commercial = new Commercial();
        commercial.setId(null);

        assertThrows(IllegalArgumentException.class, () -> commercialService.updateCommercial(commercial));
        verify(commercialDAO, never()).updateCommercial(any());
    }

    @Test
    void updateCommercial_shouldThrowCommercialNotFoundException_whenDaoReturnsZero() {
        Commercial commercial = new Commercial();
        commercial.setId(1);
        commercial.setNom("Dupont");
        commercial.setPrenom("Jean");
        commercial.setIdEntreprise(1);
        commercial.setCa(0);
        commercial.setObjectifCa(15000);
        commercial.setMail("email@commercial.com");
        commercial.setTelephone("0606060606");
        commercial.setDateEmbauche(LocalDate.now());;

        when(commercialDAO.updateCommercial(commercial)).thenReturn(0);

        assertThrows(CommercialNotFoundException.class, () -> commercialService.updateCommercial(commercial));
        verify(commercialDAO, times(1)).updateCommercial(commercial);
    }
}