package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMTable;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;
import java.sql.SQLException;

public final class DeleteTableAction<T> extends ORMAction {
    private ORMTable<T> table;

    public DeleteTableAction(ORMTable<T> table) {
        this.table = table;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        String sql = "DROP TABLE " + table.getName() + ";";
        if (table.getDebuggingEnabled())
            Utils.print("Executing query: " + sql);
        conn.createStatement().execute(sql);
    }
}
