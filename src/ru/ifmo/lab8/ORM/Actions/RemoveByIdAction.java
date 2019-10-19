package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMAttribute;
import ru.ifmo.lab8.ORM.ORMTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class RemoveByIdAction<T> extends ORMAction {
    private ORMTable<T> table;
    private int removeId;

    public RemoveByIdAction(ORMTable<T> table, int removeId) {
        this.table = table;
        this.removeId = removeId;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        String sql = "DELETE FROM " + table.getName() + " WHERE _Id = " + removeId + ";";
        conn.createStatement().execute(sql);
    }
}
