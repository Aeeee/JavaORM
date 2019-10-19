package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.Utils;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class ORMFieldSimple extends ORMField {
    protected ORMAttribute attribute;

    public ORMFieldSimple(String name, ORMAttribute attribute) {
        this.name = name;
        this.attribute = attribute;
    }

    @Override
    public void fill(Object obj, ResultSet resultSet) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                try {
                    f.setAccessible(true);
                    attribute.readTo(f, obj, resultSet);
                }
                catch (Exception e) {
                    Utils.printError("ORM exception: " + e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
        }
    }

}
