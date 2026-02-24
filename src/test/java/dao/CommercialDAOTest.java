package dao;

import model.Commercial;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommercialDAOTest {

    @InjectMocks
    private CommercialDAO commercialDAO;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private MockedStatic<DataBaseUtil> mockedDatabaseUtil;

    @BeforeEach
    void setup()  {
        mockedDatabaseUtil = Mockito.mockStatic(DataBaseUtil.class);
        mockedDatabaseUtil.when(DataBaseUtil::getConnection).thenReturn(connection);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
    }

    @Test
    void testSaveCommercial() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Commercial commercial = buildCommercial();

        assertDoesNotThrow(() -> commercialDAO.saveCommercial(commercial));

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetCommercial_found() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        // Simuler un commercial trouvé
        when(resultSet.next()).thenReturn(true);
        setMockCommercialResponse();

        Commercial commercial = commercialDAO.getCommercial();

        assertNotNull(commercial);
        assertEquals(1, commercial.getId());
        assertEquals("Dupont", commercial.getNom());
    }

    @Test
    void testGetCommercial_notFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        Commercial commercial = commercialDAO.getCommercial();

        assertNull(commercial);
    }

    @Test
    void testUpdateCommercial_success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Commercial commercial = buildCommercial();

        int rows = commercialDAO.updateCommercial(commercial);
        assertEquals(1, rows);
    }

    @Test
    void testUpdateCommercial_noRowsUpdated() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        Commercial commercial = buildCommercial();

        int rows = commercialDAO.updateCommercial(commercial);
        assertEquals(0, rows);
    }

    @Test
    void testUpdateCommercial_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        Commercial commercial = buildCommercial();

        assertThrows(RuntimeException.class, () -> commercialDAO.updateCommercial(commercial));
    }

    private Commercial buildCommercial() {
        return new Commercial(
                1,
                1,
                "Dupont",
                "Jean",
                "j.dupont@email.com",
                "0606060606",
                LocalDate.now(),
                10000,
                15000
        );
    }

    private void setMockCommercialResponse() throws SQLException {
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getInt("id_enterprise")).thenReturn(1);
        when(resultSet.getString("nom")).thenReturn("Dupont");
        when(resultSet.getString("prenom")).thenReturn("Jean");
        when(resultSet.getString("mail")).thenReturn("j.dupont@email.com");
        when(resultSet.getString("telephone")).thenReturn("0606060606");
        when(resultSet.getString("date_embauche")).thenReturn(LocalDate.now().toString());
        when(resultSet.getInt("ca")).thenReturn(10000);
        when(resultSet.getInt("objectif_ca")).thenReturn(15000);
    }
}