package dao;

import model.Commentaire;
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
public class CommentaireDAOTest {

    @InjectMocks
    private CommentaireDAO commentaireDAO;

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
    void testSaveCommentaire() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Commentaire c = new Commentaire();
        c.setIdClient(1);
        c.setCommentaire("Test commentaire");
        c.setDate(LocalDate.now());

        assertDoesNotThrow(() -> commentaireDAO.saveCommentaire(c));
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetCommentairesByClientId() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Mock ResultSet
        when(resultSet.next()).thenReturn(true, true, false); // deux commentaires
        when(resultSet.getInt("id")).thenReturn(101, 102);
        when(resultSet.getInt("id_client")).thenReturn(1, 1);
        when(resultSet.getString("commentaire")).thenReturn("Premier", "Deuxième");
        when(resultSet.getDate("date_commentaire")).thenReturn(java.sql.Date.valueOf("2026-02-20"),
                java.sql.Date.valueOf("2026-02-21"));
        when(resultSet.getString("date_derniere_modification")).thenReturn("2026-02-22", "2026-02-23");

        List<Commentaire> commentaires = commentaireDAO.getCommentairesByClientId(1);

        assertEquals(2, commentaires.size());
        assertEquals(101, commentaires.getFirst().getId());
        assertEquals("Premier", commentaires.getFirst().getCommentaire());
    }

    @Test
    void testDeleteCommentaireById() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int rows = commentaireDAO.deleteCommentaireById(101);
        assertEquals(1, rows);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateCommentaire() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Commentaire c = new Commentaire();
        c.setId(101);
        c.setCommentaire("Modifié");
        c.setDateDerniereModification(LocalDate.now());

        int rows = commentaireDAO.updateCommentaire(c);
        assertEquals(1, rows);
        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testSaveCommentaire_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        Commentaire c = new Commentaire();
        assertThrows(RuntimeException.class, () -> commentaireDAO.saveCommentaire(c));
    }

    @Test
    void testGetCommentairesByClientId_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        assertThrows(RuntimeException.class, () -> commentaireDAO.getCommentairesByClientId(1));
    }

    @Test
    void testDeleteCommentaireById_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        assertThrows(RuntimeException.class, () -> commentaireDAO.deleteCommentaireById(1));
    }

    @Test
    void testUpdateCommentaire_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException());
        Commentaire c = new Commentaire();
        assertThrows(RuntimeException.class, () -> commentaireDAO.updateCommentaire(c));
    }
}