package ru.ifmo.lab8.ORM.Actions;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class ORMAction {
    public abstract void execute(Connection conn) throws SQLException; //create statement and add batch
}
