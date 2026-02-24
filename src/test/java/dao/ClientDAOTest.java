package dao;

import model.*;
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

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientDAOTest {

    @InjectMocks
    private ClientDAO clientDAO;

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
    void testGetClientById_found() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);

        setMockClientResponse();

        Client client = clientDAO.getClientById(1);

        assertNotNull(client);
        assertEquals(1, client.getId());
        assertEquals("Dupont", client.getNom());
        assertEquals("Jean", client.getPrenom());
        assertEquals("ACME", client.getSociete());
    }

    @Test
    void testGetClientById_notFound() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        Client client = clientDAO.getClientById(999);
        assertNull(client);
    }

    @Test
    void testSaveClient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setSociete("ACME");
        client.setEmail("j.dupont@email.com");
        client.setTelephone("0606060606");
        client.setAdresse("1 rue Exemple");
        client.setCodePostal("75000");
        client.setVille("Paris");
        client.setPays("France");
        client.setDateInscription(LocalDate.now());
        client.setStatut(StatutClient.PROSPECT);
        client.setDateDerniereModification(LocalDate.now());

        assertDoesNotThrow(() -> clientDAO.saveClient(client));

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateClient() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Client client = new Client();
        client.setId(1);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setSociete("ACME");
        client.setEmail("j.dupont@email.com");
        client.setTelephone("0606060606");
        client.setAdresse("1 rue Exemple");
        client.setCodePostal("75000");
        client.setVille("Paris");
        client.setPays("France");
        client.setDateInscription(LocalDate.now());
        client.setStatut(StatutClient.PROSPECT);

        int rows = clientDAO.updateClient(client);
        assertEquals(1, rows);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateClient_noRowUpdated() throws Exception {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        Client client = buildClient();

        int rows = clientDAO.updateClient(client);

        assertEquals(0, rows);
    }

    @Test
    void testUpdateClient_sqlException() throws Exception {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        Client client = buildClient();

        assertThrows(RuntimeException.class,
                () -> clientDAO.updateClient(client));
    }

    @Test
    void testDeleteClientById() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int rows = clientDAO.deleteClientById(1);
        assertEquals(1, rows);

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetClientWithCommentsById() throws Exception {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Ligne 1 = client
        when(resultSet.next()).thenReturn(true, true, false); // deux lignes = client + 1 commentaire

        setMockClientResponse();

        // Commentaire
        when(resultSet.getInt("com_id")).thenReturn(0, 100);
        when(resultSet.getString("commentaire")).thenReturn("Premier commentaire");
        when(resultSet.getString("com_date")).thenReturn("2026-02-22");
        when(resultSet.getString("com_date_derniere_modification")).thenReturn("2026-02-23");

        Client client = clientDAO.getClientWithCommentsById(1);

        assertNotNull(client);
        assertEquals(1, client.getId());
        assertEquals(1, client.getCommentaires().size());
        Commentaire c = client.getCommentaires().getFirst();
        assertEquals(100, c.getId());
        assertEquals("Premier commentaire", c.getCommentaire());
    }

    @Test
    void testGetClientWithCommentsById_withoutComments() throws Exception {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, false);

        // Données client
        setMockClientResponse();

        // Pas de commentaire
        when(resultSet.getInt("com_id")).thenReturn(0);

        Client client = clientDAO.getClientWithCommentsById(1);

        assertNotNull(client);
        assertEquals(0, client.getCommentaires().size());
    }

    @Test
    void testGetClientWithCommentsById_multipleComments() throws Exception {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true, true, true, false);

        setMockClientResponse();

        when(resultSet.getInt("com_id"))
                .thenReturn(0)   // ligne 1
                .thenReturn(100) // ligne 2
                .thenReturn(200); // ligne 3
        when(resultSet.getString("commentaire"))
                .thenReturn("Premier", "Deuxième");
        when(resultSet.getString("com_date"))
                .thenReturn("2026-02-20")
                .thenReturn("2026-02-21");
        when(resultSet.getString("com_date_derniere_modification"))
                .thenReturn("2026-02-22")
                .thenReturn("2026-02-23");

        Client client = clientDAO.getClientWithCommentsById(1);

        assertNotNull(client);
        assertEquals(2, client.getCommentaires().size());
    }

    @Test
    void testGetClientWithCommentsById_clientNotFound() throws Exception {

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        Client client = clientDAO.getClientWithCommentsById(99);

        assertNull(client);
    }

    private void setMockClientResponse() throws SQLException {
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("nom")).thenReturn("Dupont");
        when(resultSet.getString("prenom")).thenReturn("Jean");
        when(resultSet.getString("societe")).thenReturn("ACME");
        when(resultSet.getString("email")).thenReturn("mail@test.com");
        when(resultSet.getString("telephone")).thenReturn("0600000000");
        when(resultSet.getString("adresse")).thenReturn("1 rue A");
        when(resultSet.getString("code_postal")).thenReturn("75000");
        when(resultSet.getString("ville")).thenReturn("Paris");
        when(resultSet.getString("pays")).thenReturn("France");
        when(resultSet.getString("date_inscription")).thenReturn("2026-02-23");
        when(resultSet.getString("statut")).thenReturn(StatutClient.PROSPECT.name());
        when(resultSet.getString("date_derniere_modification")).thenReturn("2026-02-23");
        when(resultSet.getString("segment")).thenReturn(null);
        when(resultSet.getString("source_acquisition")).thenReturn(null);
    }

    private Client buildClient() {
        return new Client(
                1,
                "ACME",
                "Dupont",
                "Jean",
                "mail@test.com",
                "0600000000",
                "1 rue A",
                "75000",
                "Paris",
                "France",
                LocalDate.now(),
                StatutClient.PROSPECT,
                LocalDate.now(),
                SegmentClient.PME,
                SourceAcquisitionClient.RECOMMANDATION,
                new ArrayList<>()
        );
    }


}
