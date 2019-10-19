package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMAttribute;
import ru.ifmo.lab8.ORM.ORMAttributeCollection;
import ru.ifmo.lab8.ORM.ORMTable;
import ru.ifmo.lab8.Utils;

import java.sql.*;
import java.util.List;

public class CreateTableAction<T> extends ORMAction {
    private ORMTable<T> table;
    private boolean checkIfValid;

    public CreateTableAction(ORMTable<T> table, boolean checkIfValid) {
        this.table = table;
        this.checkIfValid = checkIfValid;
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        createIfNotExists(conn);

        if (checkIfValid)
            ensureIsValid(conn);
    }

    private void createIfNotExists(Connection conn) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(table.getName()).append(" ( "); // 'table_name ('
        sql.append("_id serial PRIMARY KEY, ");
        List<ORMAttribute> attributes = table.getAttributes().getAttributesList();
        for (int i = 0; i < attributes.size(); i++) {
            ORMAttribute attr = attributes.get(i);
            sql.append(attr.getName()).append(" ").append(attr.getPsqlType()); // 'attrName attrType, '

            if (i < attributes.size() - 1)
                sql.append(", ");
        }
        sql.append(");");

        debug("Executing query: " + sql.toString());
        PreparedStatement s = conn.prepareStatement(sql.toString());
        s.execute();
    }

    private void ensureIsValid(Connection conn) throws SQLException {
        if (isValid(conn))
            return;

        // Drop table
        DeleteTableAction<T> deleteAction = new DeleteTableAction<>(table);
        deleteAction.execute(conn);

        // Create new, valid one
        createIfNotExists(conn);
    }

    private boolean isValid(Connection conn) throws SQLException {
        String sql = "SELECT column_name, data_type" +
                " FROM information_schema.columns" +
                " WHERE table_name = '" + table.getName().toLowerCase() +
                "' ORDER BY ordinal_position;";
        PreparedStatement s = conn.prepareStatement(sql);
        ResultSet resultSet = s.executeQuery();

        if (!isFirstAttrValid(resultSet))
            return false;

        List<ORMAttribute> attributes = table.getAttributes().getAttributesList();

        int i = 0;
        while (resultSet.next()) {
            if (i >= attributes.size()) {
                debug("Old table has too many attributes.");
                return false;
            }

            String oldAttrName = resultSet.getString("column_name");
            String oldAttrType = resultSet.getString("data_type");

            ORMAttribute attr = attributes.get(i);
            if (!attr.getName().equalsIgnoreCase(oldAttrName)) {
                debug((i+2) + "-th attribute name: " + oldAttrName +
                        ", but expected: " + attr.getName());
                return false;
            }

            if (!attr.getPsqlType().equalsIgnoreCase(oldAttrType)) {
                debug((i+2) + "-th attribute type: " + oldAttrType +
                        ", but expected: " + attr.getPsqlType());
                return false;
            }

            i += 1;
        }

        if (i < attributes.size() - 1) {
            debug("Old table has too few attributes.");
            return false;
        }

        debug("Old table is OK.");
        return true;
    }

    private boolean isFirstAttrValid(ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            debug("There are not attributes at all!.");
            return false;
        }

        String oldAttrName = resultSet.getString("column_name");
        String oldAttrType = resultSet.getString("data_type");

        if (!oldAttrName.equalsIgnoreCase("_id")) {
            debug("Table's first attribute should be named '_id'.");
            return false;
        }
        if (!oldAttrType.equalsIgnoreCase("integer")) {
            debug("Table's first attribute should have type 'integer'.");
            return false;
        }

        return true;
    }

    private void debug(String str) {
        if (table.getDebuggingEnabled())
            Utils.print(str);
    }
}
