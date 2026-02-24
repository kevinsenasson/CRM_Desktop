import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import util.DataBaseUtil;
import util.InitDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitDBTest {


    @Test
    void testCreateTable() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        try (MockedStatic<DataBaseUtil> mockedStatic = mockStatic(DataBaseUtil.class)) {
            mockedStatic.when(DataBaseUtil::getConnection).thenReturn(mockConnection);

            InitDB.createTable();

            verify(mockStatement, times(5)).executeUpdate(anyString());

            verify(mockConnection).close();
        }
    }

    @Test
    void testCreateTableThrowsSQLException() throws Exception {
        Connection mockConnection = mock(Connection.class);
        Statement mockStatement = mock(Statement.class);
        when(mockConnection.createStatement()).thenReturn(mockStatement);

        doThrow(new SQLException("fail")).when(mockStatement).executeUpdate(anyString());

        try (MockedStatic<DataBaseUtil> mockedStatic = mockStatic(DataBaseUtil.class)) {
            mockedStatic.when(DataBaseUtil::getConnection).thenReturn(mockConnection);

            try {
                InitDB.createTable();
            } catch (SQLException e) {
                assert e.getMessage().contains("erreur lors de la création des tables");
            }
        }
    }
}
