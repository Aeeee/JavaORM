package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.ORM.Actions.*;
import ru.ifmo.lab8.Utils;

import java.lang.reflect.Constructor;

public final class ORMTable<T> implements IDbQueryable<T> {
    private SimpleORM parentORM;
    private Class<T> tClass;

    private ORMAttributeCollection attributes = new ORMAttributeCollection();
    private Constructor noArgConstructor = null;

    private boolean enableDebugging = false;

    public ORMTable(SimpleORM parentORM, Class<T> tClass) throws IllegalArgumentException {
        this.parentORM = parentORM;
        this.tClass = tClass;

        attributes.addAttributes(tClass);
        findConstructor();
        if (!attributes.validateCollection())
            throw new IllegalArgumentException("Specified class does not meet certain requirements.");

        if (enableDebugging)
            attributes.debugPrint();
    }

    Class<T> getTypeClass() {
        return tClass;
    }

    SimpleORM getParentORM() {
        return parentORM;
    }

    public String getName() {
        return tClass.getSimpleName();
    }

    public ORMAttributeCollection getAttributes() {
        return attributes;
    }

    public boolean getDebuggingEnabled() {
        return enableDebugging;
    }

    public void setDebuggingEnabled(boolean value) {
        enableDebugging = value;
    }

    private void findConstructor() throws IllegalArgumentException {
        Constructor[] constructors = tClass.getDeclaredConstructors();
        for (Constructor c : constructors) {
            if (c.getParameterCount() == 0) {
                noArgConstructor = c;
                break;
            }
        }

        if (noArgConstructor == null) {
            throw new IllegalArgumentException("ru.ifmo.lab8.ORM Error: class must have a parameterless constructor!");
        }
    }

    public ORMTableQuery<T> ensureCreated() {
        ORMAction createAction = new CreateTableAction<>(this, false);
        return new ORMTableQuery<T>(this, createAction);
    }

    public ORMTableQuery<T> ensureCreatedAndValid() {
        ORMAction createAction = new CreateTableAction<>(this, true);
        return new ORMTableQuery<T>(this, createAction);
    }

    public ORMQuery<T> truncate() {
        ORMAction truncateAction = new TruncateTableAction<>(this);
        return new ORMQuery<T>(this, truncateAction);
    }

    public boolean delete() {
        ORMAction deleteAction = new DeleteTableAction<>(this);
        return new ORMQuery<T>(this, deleteAction).execute();
    }

    @Override
    public Object[] selectAll() {
        return new ORMQuery<T>(this).selectAll();
    }

    @Override
    public Object[] selectTop(int count) {
        return new ORMQuery<T>(this).selectTop(count);
    }

    @Override
    public Object[] selectBottom(int count) {
        return new ORMQuery<T>(this).selectBottom(count);
    }

    @Override
    public ORMQuery<T> insert(T toInsert) {
        ORMAction insertAction = new InsertAction<T>(this, toInsert);
        return new ORMQuery<>(this, insertAction);
    }

    @Override
    public ORMQuery<T> insert(Iterable<T> toInsert) {
        ORMAction insertAction = new InsertManyAction<>(this, toInsert);
        return new ORMQuery<>(this, insertAction);
    }

    @Override
    public ORMQuery<T> remove(T toRemove) {
        ORMAction removeAction = new RemoveAction<>(this, toRemove);
        return new ORMQuery<>(this, removeAction);
    }

    @Override
    public ORMQuery<T> removeById(int id) {
        ORMAction removeAction = new RemoveByIdAction<>(this, id);
        return new ORMQuery<>(this, removeAction);
    }
}
