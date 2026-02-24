package service;

import dao.ICommentaireDAO;
import exception.CommentaireNotFoundException;
import model.Client;
import model.Commentaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentaireServiceTest {

    @InjectMocks
    private CommentaireService commentaireService;

    @Mock
    private ICommentaireDAO commentaireDAO;

    @Mock
    private IClientService clientService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCommentaire_success() {
        Commentaire commentaire = new Commentaire();
        commentaire.setIdClient(1);
        commentaire.setCommentaire("Test commentaire");

        Client client = new Client();
        client.setId(1);

        when(clientService.getClientById(1)).thenReturn(client);

        commentaireService.save(commentaire);

        assertNotNull(commentaire.getDate()); // date should be set
        verify(commentaireDAO, times(1)).saveCommentaire(commentaire);
        verify(clientService, times(1)).getClientById(1);
    }

    @Test
    void testGetCommentaireByClientId_success_sorted() {
        Commentaire c1 = new Commentaire();
        c1.setId(1);
        c1.setDate(LocalDate.of(2025,1,1));
        Commentaire c2 = new Commentaire();
        c2.setId(2);
        c2.setDate(LocalDate.of(2025,2,1));

        when(commentaireDAO.getCommentairesByClientId(1)).thenReturn(Arrays.asList(c2, c1));

        List<Commentaire> result = commentaireService.getCommentaireByClientId(1);

        assertEquals(2, result.size());
        assertEquals(c1, result.get(0)); // should be sorted by date
        assertEquals(c2, result.get(1));
        verify(commentaireDAO, times(1)).getCommentairesByClientId(1);
    }

    @Test
    void testGetCommentaireByClientId_empty() {
        when(commentaireDAO.getCommentairesByClientId(1)).thenReturn(Collections.emptyList());

        List<Commentaire> result = commentaireService.getCommentaireByClientId(1);

        assertTrue(result.isEmpty());
        verify(commentaireDAO, times(1)).getCommentairesByClientId(1);
    }

    @Test
    void testDeleteCommentaireById_success() {
        when(commentaireDAO.deleteCommentaireById(1)).thenReturn(1);

        assertDoesNotThrow(() -> commentaireService.deleteCommentaireById(1));
        verify(commentaireDAO, times(1)).deleteCommentaireById(1);
    }

    @Test
    void testDeleteCommentaireById_notFound() {
        when(commentaireDAO.deleteCommentaireById(1)).thenReturn(0);

        CommentaireNotFoundException exception = assertThrows(CommentaireNotFoundException.class,
                () -> commentaireService.deleteCommentaireById(1));

        assertEquals("Le commentaire avec l'id : 1 n'existe pas", exception.getMessage());
        verify(commentaireDAO, times(1)).deleteCommentaireById(1);
    }

    @Test
    void testUpdateCommentaire_success() {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(1);
        commentaire.setCommentaire("Modifié");

        when(commentaireDAO.updateCommentaire(commentaire)).thenReturn(1);

        commentaireService.updateCommentaire(commentaire);

        assertNotNull(commentaire.getDateDerniereModification());
        verify(commentaireDAO, times(1)).updateCommentaire(commentaire);
    }

    @Test
    void testUpdateCommentaire_notFound() {
        Commentaire commentaire = new Commentaire();
        commentaire.setId(1);
        commentaire.setCommentaire("Modifié");

        when(commentaireDAO.updateCommentaire(commentaire)).thenReturn(0);

        CommentaireNotFoundException exception = assertThrows(CommentaireNotFoundException.class,
                () -> commentaireService.updateCommentaire(commentaire));

        assertEquals("Le commentaire avec l'id : 1 n'existe pas", exception.getMessage());
        verify(commentaireDAO, times(1)).updateCommentaire(commentaire);
    }
}