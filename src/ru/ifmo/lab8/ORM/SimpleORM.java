package ru.ifmo.lab8.ORM;

import com.sun.istack.internal.NotNull;
import org.postgresql.ds.PGConnectionPoolDataSource;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;

public final class SimpleORM {
    private IDbSettings connSettings;
    private PGConnectionPoolDataSource connPool;

    public SimpleORM(@NotNull IDbSettings connSettings) {
        this.connSettings = connSettings;

        connPool = new PGConnectionPoolDataSource();
        connPool.setServerName(connSettings.getDbServerName());
        connPool.setSslMode(connSettings.getDbSslMode());
        connPool.setPortNumber(connSettings.getDbPort());
        connPool.setDatabaseName(connSettings.getDbName());
        connPool.setLoginTimeout(200);
        connPool.setSocketTimeout(200);
    }

    Connection getConnection() {
        try {
            return connPool.getPooledConnection(
                    connSettings.getDbUserName(),
                    connSettings.getDbUserPassword()
            ).getConnection();
        }
        catch (Exception e) {
            Utils.printError("Failed to get pooled connection: " + e.getMessage());
            return null;
        }
    }

}
