package service;

import dao.IClientDAO;
import exception.ClientNotFoundException;
import model.Client;
import model.Commentaire;
import model.SegmentClient;
import model.SourceAcquisitionClient;
import model.StatutClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private IClientDAO clientDAO;

    @InjectMocks
    private ClientService clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = new Client(
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

    @Test
    void testSaveClient_success() {
        doNothing().when(clientDAO).saveClient(any(Client.class));

        assertDoesNotThrow(() -> clientService.saveClient(client));

        assertNotNull(client.getDateInscription());
        assertNotNull(client.getDateDerniereModification());

        verify(clientDAO, times(1)).saveClient(client);
    }

    @Test
    void testFindAll_returnsClients() {
        List<Client> clients = List.of(client);
        when(clientDAO.findAll()).thenReturn(clients);

        List<Client> result = clientService.findAll();

        assertEquals(1, result.size());
        assertEquals(client, result.getFirst());
    }

    @Test
    void testGetClientById_found() {
        when(clientDAO.getClientById(1)).thenReturn(client);

        Client result = clientService.getClientById(1);

        assertEquals(client, result);
    }

    @Test
    void testGetClientById_notFound() {
        when(clientDAO.getClientById(99)).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.getClientById(99));
    }

    @Test
    void testDeleteClientById_success() {
        when(clientDAO.deleteClientById(1)).thenReturn(1);

        assertDoesNotThrow(() -> clientService.deleteClientById(1));

        verify(clientDAO, times(1)).deleteClientById(1);
    }

    @Test
    void testDeleteClientById_notFound() {
        when(clientDAO.deleteClientById(99)).thenReturn(0);

        assertThrows(ClientNotFoundException.class, () -> clientService.deleteClientById(99));
    }

    @Test
    void testUpdateClient_success() {
        when(clientDAO.updateClient(client)).thenReturn(1);

        assertDoesNotThrow(() -> clientService.updateClient(client));

        assertNotNull(client.getDateDerniereModification());
        verify(clientDAO, times(1)).updateClient(client);
    }

    @Test
    void testUpdateClient_notFound() {
        when(clientDAO.updateClient(client)).thenReturn(0);

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(client));
    }

    @Test
    void testGetClientWithCommentsById_found() {
        Commentaire c1 = new Commentaire(1, 1, "Premier", LocalDate.now().minusDays(1), LocalDate.now());
        Commentaire c2 = new Commentaire(2, 1, "Deuxième", LocalDate.now(), LocalDate.now());
        client.getCommentaires().add(c2);
        client.getCommentaires().add(c1); // désordre volontaire

        when(clientDAO.getClientWithCommentsById(1)).thenReturn(client);

        Client result = clientService.getClientWithCommentsById(1);

        assertEquals(2, result.getCommentaires().size());
        assertEquals(c1, result.getCommentaires().get(0)); // trié par date croissante
        assertEquals(c2, result.getCommentaires().get(1));
    }

    @Test
    void testGetClientWithCommentsById_notFound() {
        when(clientDAO.getClientWithCommentsById(99)).thenReturn(null);

        assertThrows(ClientNotFoundException.class, () -> clientService.getClientWithCommentsById(99));
    }
}