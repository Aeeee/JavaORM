package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMAttribute;
import ru.ifmo.lab8.ORM.ORMTable;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RemoveAction<T> extends ORMAction {
    private ORMTable<T> table;
    private T toRemove;

    public RemoveAction(ORMTable<T> table, T toRemove) {
        this.table = table;
        this.toRemove = toRemove;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(table.getName()).append(" WHERE ");
        List<ORMAttribute> attributes = table.getAttributes().getAttributesList();
        sql.append(attributes.get(0).getName()).append(" = ?");
        for (int i = 1; i < attributes.size(); i++) {
            ORMAttribute attr = attributes.get((i));
            sql.append(" AND ").append(attr.getName()).append(" = ?");
        }

        if (table.getDebuggingEnabled())
            Utils.print("Executing query: " + sql.toString());

        PreparedStatement statement = conn.prepareStatement(sql.toString());
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).write(statement, (i + 1), toRemove);
        }

        statement.execute();
    }
}
