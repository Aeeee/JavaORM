package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ORMFieldComplex extends ORMField {
    protected List<ORMField> fields = new ArrayList<>();
    protected Class tClass;

    public ORMFieldComplex(String name, Class tClass) {
        this.name = name;
        this.tClass = tClass;
    }

    public void addField(ORMField child) {
        fields.add(child);
    }

    @Override
    public void fill(Object obj, ResultSet resultSet) {
        Object myFieldInstance = createInstance();
        for (ORMField field : fields) {
            field.fill(myFieldInstance, resultSet);
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                f.setAccessible(true);
                try {
                    f.set(obj, myFieldInstance);
                }
                catch (Exception e) {
                    Utils.printError("ORM exception: " + e.getMessage());
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    private Object createInstance() {
        Constructor constructor = getValidConstructor();
        if (constructor == null) {
            Utils.printError("Couldn't find valid constructor for class " + tClass.getName());
            return null;
        }

        constructor.setAccessible(true);
        try {
            return constructor.newInstance();
        }
        catch (Exception e) {
            Utils.printError("ORM exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Constructor getValidConstructor() {
        Constructor[] constructors = tClass.getDeclaredConstructors();
        for (Constructor c : constructors) {
            if (c.getParameterCount() == 0)
                return c;
        }
        return null;
    }
}
