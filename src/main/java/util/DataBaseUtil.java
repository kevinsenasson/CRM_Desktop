package util;

import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DataBaseUtil {


    private static final String URL;

    static {
        try (InputStream in = DataBaseUtil.class.getClassLoader().getResourceAsStream(
                System.getProperty("test") != null ? "test.properties" : "application.properties")) {
            Properties props = new Properties();
            props.load(in);
            URL = props.getProperty("db.url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);

        try(Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}
