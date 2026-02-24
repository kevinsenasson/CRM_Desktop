package service;

import dao.ICommercialDAO;
import dao.IDevisDao;
import exception.DevisNotFoundException;
import model.Commercial;
import model.Devis;
import model.StatutDevis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DevisServiceTest {

    @Mock
    private IDevisDao devisDao;

    @Mock
    private ICommercialDAO commercialDao;

    @InjectMocks
    private DevisService devisService;

    private Devis devis1;
    private Devis devis2;
    private Commercial commercial;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        devis1 = new Devis();
        devis1.setId(1);
        devis1.setIdClient(1);
        devis1.setReference("REF001");
        devis1.setDateCreation(LocalDate.now().minusDays(1));
        devis1.setMontant(1000.0);
        devis1.setStatut(StatutDevis.ACCEPTE);

        devis2 = new Devis();
        devis2.setId(2);
        devis2.setIdClient(1);
        devis2.setReference("REF002");
        devis2.setDateCreation(LocalDate.now());
        devis2.setMontant(500.0);
        devis2.setStatut(StatutDevis.ENVOYE);

        commercial = new Commercial();
        commercial.setId(1);
        commercial.setCa(0);
    }

    @Test
    void testSaveDevis_success() {
        doNothing().when(devisDao).saveDevis(devis1);
        when(devisDao.findAll()).thenReturn(List.of(devis1));
        when(commercialDao.getCommercial()).thenReturn(commercial);
        when(commercialDao.updateCommercial(any(Commercial.class))).thenReturn(1);

        assertDoesNotThrow(() -> devisService.saveDevis(devis1));

        verify(devisDao, times(1)).saveDevis(devis1);
        assertEquals(1000, commercial.getCa());
    }

    @Test
    void testDeleteDevisById_success() {
        when(devisDao.deleteDevisById(1)).thenReturn(1);

        assertDoesNotThrow(() -> devisService.deleteDevisById(1));

        verify(devisDao, times(1)).deleteDevisById(1);
    }

    @Test
    void testDeleteDevisById_notFound() {
        when(devisDao.deleteDevisById(99)).thenReturn(0);

        assertThrows(DevisNotFoundException.class, () -> devisService.deleteDevisById(99));
    }

    @Test
    void testGetDevisByClientId_sorted() {
        when(devisDao.getDevisByClientId(1)).thenReturn(List.of(devis2, devis1));

        List<Devis> result = devisService.getDevisByClientId(1);

        assertEquals(2, result.size());
        assertEquals(devis1, result.get(0)); // tri par date croissante
        assertEquals(devis2, result.get(1));
    }

    @Test
    void testUpdateDevis_success() {
        when(devisDao.updateDevis(devis1)).thenReturn(1);
        when(devisDao.findAll()).thenReturn(List.of(devis1, devis2));
        when(commercialDao.getCommercial()).thenReturn(commercial);
        when(commercialDao.updateCommercial(any(Commercial.class))).thenReturn(1);

        assertDoesNotThrow(() -> devisService.updateDevis(devis1));

        verify(devisDao, times(1)).updateDevis(devis1);
        assertEquals(1000, commercial.getCa()); // CA recalculé uniquement sur ACCEPTE
    }

    @Test
    void testUpdateDevis_notFound() {
        when(devisDao.updateDevis(devis1)).thenReturn(0);

        assertThrows(DevisNotFoundException.class, () -> devisService.updateDevis(devis1));
    }
}