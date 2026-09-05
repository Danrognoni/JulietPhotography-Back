package com.julietamarateo.photography.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabasePragmaInitializer {

    private static final Logger log = LoggerFactory.getLogger(DatabasePragmaInitializer.class);
    private final DataSource dataSource;

    public DatabasePragmaInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void configurePragmas() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            // Activar Write-Ahead Logging (WAL) para lecturas y escrituras concurrentes sin contención
            try (ResultSet rs = statement.executeQuery("PRAGMA journal_mode = WAL;")) {
                if (rs.next()) {
                    log.info("SQLite journal_mode configurado en: {}", rs.getString(1));
                }
            }

            // Reducir operaciones fsync manteniendo integridad ACID en WAL mode
            statement.execute("PRAGMA synchronous = NORMAL;");

            // Timeout de espera de bloqueo para evitar 'database is locked' bajo concurrencia
            statement.execute("PRAGMA busy_timeout = 5000;");

            // Asignar 64MB de memoria RAM para la caché de páginas de SQLite (-64000 KB)
            statement.execute("PRAGMA cache_size = -64000;");

            log.info("PRAGMAs de optimización SQLite aplicados exitosamente (WAL, NORMAL, busy_timeout=5000, cache_size=-64000).");

            // Asegurar que las columnas del layout del canvas existan en la tabla albums de SQLite
            ensureColumnExists(statement, "albums", "x_pos", "REAL");
            ensureColumnExists(statement, "albums", "y_pos", "REAL");
            ensureColumnExists(statement, "albums", "width", "REAL");
            ensureColumnExists(statement, "albums", "z_index", "INTEGER DEFAULT 1");
        } catch (SQLException e) {
            log.warn("No se pudieron inicializar algunos PRAGMAs o columnas de SQLite: {}", e.getMessage());
        }
    }

    private void ensureColumnExists(Statement statement, String table, String column, String type) {
        try (ResultSet rs = statement.executeQuery("PRAGMA table_info(" + table + ");")) {
            boolean exists = false;
            while (rs.next()) {
                String colName = rs.getString("name");
                if (column.equalsIgnoreCase(colName)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                statement.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type + ";");
                log.info("Columna {} agregada exitosamente a la tabla {}.", column, table);
            }
        } catch (SQLException e) {
            log.warn("No se pudo verificar o agregar columna {}.{}: {}", table, column, e.getMessage());
        }
    }
}
