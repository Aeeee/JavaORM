package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.Utils;

import java.lang.reflect.Field;
import java.sql.*;
import java.time.LocalDateTime;

public class ORMAttribute {
    private String name;
    private String psqlType;

    ORMAttribute(String name, Field field) {
        this.name = name;

        switch (field.getType().getSimpleName()) {
            case "int":
                psqlType = "integer";
                break;
            case "String":
                psqlType = "text";
                break;
            case "float":
                psqlType = "real";
                break;
            case "double":
                psqlType = "double precision";
                break;
            case "LocalDateTime":
                psqlType = "timestamp without time zone";
                break;
            default:
                Utils.printError("Unknown data type: " + field.getType().getSimpleName());
                psqlType = "unknown";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public String getPsqlType() {
        return psqlType;
    }

    public void write(PreparedStatement prepStatement, int index, Object toWrite) {
        String[] route = name.split("_");
        if (route.length == 0)
            route = new String[] { name };

        Class curClass = toWrite.getClass();
        Object curObject = toWrite;
        Field curField = null;
        try {
            for (int i = 0; i < route.length; i++) {
                curField = curClass.getDeclaredField(route[i]);
                curClass = curField.getType();
                curField.setAccessible(true);
                if (i < route.length - 1)
                    curObject = curField.get(curObject);
            }
        }
        catch (Exception e) {
            Utils.printError("ORM Error: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (curField == null) {
            Utils.printError("Could not find field for attribute: " + name);
            return;
        }

        try {
            write(prepStatement, index, curField, curObject);
        }
        catch (Exception e) {
            Utils.printError("ORM Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void write(PreparedStatement s, int index, Field field, Object obj)
            throws IllegalAccessException, SQLException {
        switch (psqlType) {
            case "int":
            case "integer":
            case "serial":
                s.setInt(index, field.getInt(obj));
                break;

            case "text":
                s.setString(index, (String)field.get(obj));
                break;

            case "real":
                s.setFloat(index, field.getFloat(obj));
                break;

            case "double precision":
                s.setDouble(index,field.getDouble(obj));
                break;

            case "timestamp without time zone":
                s.setTimestamp(index, Timestamp.valueOf((LocalDateTime)field.get(obj)));
                break;

            default:
                Utils.printWarning("Unknown data type: " + psqlType);
                break;
        }
    }

    public void readTo(Field field, Object obj, ResultSet resultSet)
            throws SQLException, IllegalAccessException
    {
        switch (psqlType) {
            case "int":
            case "integer":
            case "serial":
                field.setInt(obj, resultSet.getInt(name));
                break;

            case "text":
                field.set(obj, resultSet.getString(name));
                break;

            case "real":
                field.setFloat(obj, resultSet.getFloat(name));
                break;

            case "double precision":
                field.setDouble(obj, resultSet.getDouble(name));
                break;

            case "timestamp without time zone":
                field.set(obj, (resultSet.getTimestamp(name)).toLocalDateTime());
                break;

            default:
                Utils.printWarning("Unknown data type: " + psqlType);
                break;
        }
    }

    static boolean isSimple(Field field) {
        Class type = field.getType();

        if (type.isPrimitive())
            return true;
        if (LocalDateTime.class.isAssignableFrom(field.getType()))
            return true;
        if (String.class.isAssignableFrom(field.getType()))
            return true;

        return false;
    }
}
