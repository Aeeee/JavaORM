package ru.ifmo.lab8.ORM;

public interface IDbSettings {
    String getDbServerName();
    String getDbSslMode();
    int getDbPort();
    String getDbName();
    String getDbUserName();
    String getDbUserPassword();
}
