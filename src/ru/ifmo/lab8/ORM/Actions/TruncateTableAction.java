package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMTable;

import java.sql.Connection;
import java.sql.SQLException;

public class TruncateTableAction<T> extends ORMAction {
    private ORMTable<T> table;

    public TruncateTableAction(ORMTable<T> table) {
        this.table = table;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        String sql = "TRUNCATE " + table.getName();
        conn.createStatement().execute(sql);
    }

}
