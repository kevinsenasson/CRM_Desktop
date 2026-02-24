package util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class InitDB {

    public static void createTable() throws Exception {

        final String sqlClient = """
                CREATE TABLE IF NOT EXISTS client(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    societe TEXT NOT NULL,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    email TEXT,
                    telephone TEXT,
                    adresse TEXT,
                    code_postal TEXT,
                    ville TEXT,
                    pays TEXT,
                    date_inscription TEXT,
                    statut TEXT NOT NULL,
                    date_derniere_modification TEXT NOT NULL,
                    segment TEXT,
                    source_acquisition TEXT
                );
                """;

        final String sqlCommentaire = """
                CREATE TABLE IF NOT EXISTS commentaire(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_client INTEGER NOT NULL,
                    commentaire TEXT NOT NULL,
                    date_commentaire TEXT NOT NULL,
                    date_derniere_modification TEXT,
                    FOREIGN KEY(id_client) REFERENCES client(id) ON DELETE CASCADE
                );
                """;

        final String sqlDevis = """
                CREATE TABLE IF NOT EXISTS devis(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_client INTEGER NOT NULL,
                    reference TEXT NOT NULL UNIQUE,
                    date_creation TEXT NOT NULL,
                    montant REAL NOT NULL,
                    statut TEXT NOT NULL,
                    description TEXT,
                    FOREIGN KEY(id_client) REFERENCES client(id) ON DELETE CASCADE
                );
                """;

        final String sqlEnterprise = """
                CREATE TABLE IF NOT EXISTS enterprise(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                adresse TEXT NOT NULL,
                code_postal TEXT NOT NULL,
                ville TEXT NOT NULL,
                telephone TEXT NOT NULL,
                email TEXT NOT NULL,
                siret TEXT NOT NULL
                );
                """;

        final String sqlCommercial = """
                CREATE TABLE IF NOT EXISTS commercial(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_enterprise INTEGER NOT NULL,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                mail TEXT NOT NULL,
                telephone TEXT NOT NULL,
                date_embauche TEXT NOT NULL,
                ca REAL NOT NULL,
                objectif_ca REAL NOT NULL,
                FOREIGN KEY(id_enterprise) REFERENCES enterprise(id) ON DELETE CASCADE
                );
                """;


        try(Connection connection = DataBaseUtil.getConnection();
            Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlClient);
            statement.executeUpdate(sqlCommentaire);
            statement.executeUpdate(sqlDevis);
            statement.executeUpdate(sqlEnterprise);
            statement.executeUpdate(sqlCommercial);
            log.info("Tables crées avec succès");
        } catch (SQLException e) {
            throw new SQLException("erreur lors de la création des tables", e);
        }
    }
}
