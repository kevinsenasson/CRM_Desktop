package dao;

import model.Devis;
import model.StatutDevis;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DataBaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DevisDAOTest {

    @InjectMocks
    private DevisDAO devisDAO;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private MockedStatic<DataBaseUtil> mockedDatabaseUtil;

    @BeforeEach
    void setup() {
        mockedDatabaseUtil = Mockito.mockStatic(DataBaseUtil.class);
        mockedDatabaseUtil.when(DataBaseUtil::getConnection).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
    }

    @Test
    void testFindAll() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);
        setMockDevisResponse();

        List<Devis> all = devisDAO.findAll();
        assertEquals(1, all.size());
        assertEquals("REF001", all.getFirst().getReference());
    }

    @Test
    void testSaveDevis() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Devis devis = buildDevis();
        assertDoesNotThrow(() -> devisDAO.saveDevis(devis));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteDevisById() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int rows = devisDAO.deleteDevisById(1);
        assertEquals(1, rows);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetDevisByClientId() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);
        setMockDevisResponse();

        List<Devis> list = devisDAO.getDevisByClientId(10);
        assertEquals(1, list.size());
        assertEquals(10, list.getFirst().getIdClient());
    }

    @Test
    void testUpdateDevis() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Devis devis = buildDevis();
        devis.setId(1);
        int rows = devisDAO.updateDevis(devis);
        assertEquals(1, rows);
    }

    @Test
    void testUpdateDevis_noRowUpdated() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        Devis devis = buildDevis();
        devis.setId(999);
        int rows = devisDAO.updateDevis(devis);
        assertEquals(0, rows);
    }

    @Test
    void testUpdateDevis_sqlException() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        Devis devis = buildDevis();
        devis.setId(1);

        assertThrows(RuntimeException.class, () -> devisDAO.updateDevis(devis));
    }

    private void setMockDevisResponse() throws SQLException {
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("id_client")).thenReturn(10);
        when(resultSet.getString("reference")).thenReturn("REF001");
        when(resultSet.getString("date_creation")).thenReturn("2026-02-23");
        when(resultSet.getDouble("montant")).thenReturn(1500.0);
        when(resultSet.getString("statut")).thenReturn(StatutDevis.ENVOYE.name());
        when(resultSet.getString("description")).thenReturn("Description du devis");
    }

    private Devis buildDevis() {
        Devis devis = new Devis();
        devis.setIdClient(10);
        devis.setReference("REF001");
        devis.setDateCreation(LocalDate.now());
        devis.setMontant(1500.0);
        devis.setStatut(StatutDevis.ENVOYE);
        devis.setDescription("Description du devis");
        return devis;
    }
}
