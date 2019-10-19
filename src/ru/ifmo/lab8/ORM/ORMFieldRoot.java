package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.Utils;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class ORMFieldRoot extends ORMFieldComplex {

    ORMFieldRoot(Class tClass) {
        super("", tClass);
    }

    public Object createInstance(ResultSet resultSet) {
        Constructor constructor = getValidConstructor();
        if (constructor == null) {
            Utils.printError("Couldn't find valid constructor for class " + tClass.getName());
            return null;
        }

        constructor.setAccessible(true);
        Object obj;
        try {
            obj = constructor.newInstance();
        }
        catch (Exception e) {
            Utils.printError("ORM exception: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        for (ORMField field : fields)
            field.fill(obj, resultSet);

        return obj;
    }

    private Constructor getValidConstructor() {
        Constructor[] constructors = tClass.getDeclaredConstructors();
        for (Constructor c : constructors) {
            if (c.getParameterCount() == 0)
                return c;
        }
        return null;
    }

    @Override
    public void fill(Object obj, ResultSet resultSet) {
        // Not implemented (and will never be)
    }
}
