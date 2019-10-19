package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMAttribute;
import ru.ifmo.lab8.ORM.ORMTable;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertAction<T> extends ORMAction {

    private ORMTable<T> table;
    private T toInsert;

    public InsertAction(ORMTable<T> table, T toInsert) {
        this.table = table;
        this.toInsert = toInsert;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(table.getName()).append(" (");

        List<ORMAttribute> attributes = table.getAttributes().getAttributesList();
        for (int i = 0; i < attributes.size(); i++) {
            if (i > 0)
                sql.append(", ");
            sql.append(attributes.get(i).getName());
        }

        sql.append(") VALUES (");
        for (int i = 0; i < attributes.size(); i++) {
            if (i > 0)
                sql.append(", ");
            sql.append("?");
        }
        sql.append(");");

        if (table.getDebuggingEnabled())
            Utils.print(sql.toString());

        PreparedStatement statement = conn.prepareStatement(sql.toString());
        for (int i = 0; i < attributes.size(); i++) {
            attributes.get(i).write(statement, (i + 1), toInsert);
        }

        statement.execute();
    }
}
