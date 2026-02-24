package dao;

import model.Enterprise;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnterpriseDAOTest {

    @InjectMocks
    private EnterpriseDAO enterpriseDAO;

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
    void testSaveEnterprise() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Enterprise enterprise = buildEnterprise();

        assertDoesNotThrow(() -> enterpriseDAO.saveEnterprise(enterprise));

        verify(preparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testUpdateEnterprise_success() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        Enterprise enterprise = buildEnterprise();
        enterprise.setId(1);

        int rows = enterpriseDAO.updateEnterprise(enterprise);
        assertEquals(1, rows);
    }

    @Test
    void testUpdateEnterprise_noRowsUpdated() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        Enterprise enterprise = buildEnterprise();
        enterprise.setId(1);

        int rows = enterpriseDAO.updateEnterprise(enterprise);
        assertEquals(0, rows);
    }

    @Test
    void testUpdateEnterprise_sqlException() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException());

        Enterprise enterprise = buildEnterprise();
        enterprise.setId(1);

        assertThrows(RuntimeException.class, () -> enterpriseDAO.updateEnterprise(enterprise));
    }

    @Test
    void testGetEnterprise_found() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(true);
        setMockEnterpriseResponse();

        Enterprise enterprise = enterpriseDAO.getEnterprise();

        assertNotNull(enterprise);
        assertEquals(1, enterprise.getId());
        assertEquals("ACME Corp", enterprise.getNom());
    }

    @Test
    void testGetEnterprise_notFound() throws SQLException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);

        when(resultSet.next()).thenReturn(false);

        Enterprise enterprise = enterpriseDAO.getEnterprise();

        assertNull(enterprise);
    }

    private Enterprise buildEnterprise() {
        return new Enterprise(
                0,
                "ACME Corp",
                "1 rue Exemple",
                "75000",
                "Paris",
                "0606060606",
                "contact@acme.com",
                "12345678901234"
        );
    }

    private void setMockEnterpriseResponse() throws SQLException {
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("nom")).thenReturn("ACME Corp");
        when(resultSet.getString("adresse")).thenReturn("1 rue Exemple");
        when(resultSet.getString("code_postal")).thenReturn("75000");
        when(resultSet.getString("ville")).thenReturn("Paris");
        when(resultSet.getString("telephone")).thenReturn("0606060606");
        when(resultSet.getString("email")).thenReturn("contact@acme.com");
        when(resultSet.getString("Siret")).thenReturn("12345678901234");
    }
}