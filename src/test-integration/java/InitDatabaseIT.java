import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DataBaseUtil;
import util.InitDB;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class InitDatabaseIT {

    private static final String URL = "jdbc:sqlite:file:memdb1?mode=memory&cache=shared";

    private Connection keepAliveConnection;

    @BeforeEach
    void setUp() throws SQLException {
        System.setProperty("test", "true");

        keepAliveConnection = DataBaseUtil.getConnection();

        try (Statement stmt = keepAliveConnection.createStatement()) {

            stmt.executeUpdate("DROP TABLE IF EXISTS commercial");
            stmt.executeUpdate("DROP TABLE IF EXISTS devis");
            stmt.executeUpdate("DROP TABLE IF EXISTS commentaire");
            stmt.executeUpdate("DROP TABLE IF EXISTS client");
            stmt.executeUpdate("DROP TABLE IF EXISTS enterprise");
        }
    }


    @AfterEach
    void tearDown() throws SQLException {
        if (keepAliveConnection != null && !keepAliveConnection.isClosed()) {
            keepAliveConnection.close();
        }
        System.clearProperty("test");
    }

    @Test
    void testConnection() {
        try(Connection connection = DataBaseUtil.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());

            connection.close();

            assertTrue(connection.isClosed());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInitDb() throws Exception {
        InitDB.createTable();

        try (Connection connection = DataBaseUtil.getConnection()) {

            assertTrue(tableExists(connection, "enterprise"));
            assertTrue(tableExists(connection, "client"));
            assertTrue(tableExists(connection, "devis"));
            assertTrue(tableExists(connection, "commentaire"));
            assertTrue(tableExists(connection, "commercial"));
        }

    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {

        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tableName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


}
