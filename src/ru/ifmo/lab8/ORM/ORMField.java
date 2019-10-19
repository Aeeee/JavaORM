package ru.ifmo.lab8.ORM;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class ORMField {
    protected String name;

    public abstract void fill(Object obj, ResultSet resultSet);
}
