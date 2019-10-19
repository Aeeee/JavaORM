package ru.ifmo.lab8.ORM.Actions;

import ru.ifmo.lab8.ORM.ORMTable;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectAction<T> extends ORMAction {

    private ORMTable<T> table;
    private String orderStr = "";
    private String limitStr = "";

    private ResultSet resultSet = null;

    public SelectAction(ORMTable<T> table) {
        this.table = table;
    }

    public SelectAction<T> orderByAsc() {
        orderStr = " ORDER BY ASC";
        return this;
    }

    public SelectAction<T> orderByDesc() {
        orderStr = " ORDER BY DESC";
        return this;
    }

    public SelectAction<T> setLimit(int limit) {
        limitStr = " LIMIT " + limit;
        return this;
    }

    public Object[] getResult() {
        if (resultSet == null) {
            Utils.printError("No result set found. Did you forget to call execute()?");
            return new Object[0];
        }

        List<Object> entries = new ArrayList<>();

        try {
            while (resultSet.next()) {
                Object entry = table.getAttributes().getRootField().createInstance(resultSet);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }
        catch (Exception e) {
            Utils.printError("ORM select exception: " + e.getMessage());
            e.printStackTrace();
        }

        return entries.toArray();
    }

    @Override
    public void execute(Connection conn) throws SQLException {
        String SQL = "SELECT * FROM " + table.getName() + orderStr + limitStr + ";";
        resultSet = conn.createStatement().executeQuery(SQL);
    }
}
