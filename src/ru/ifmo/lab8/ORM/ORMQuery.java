package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.ORM.Actions.*;
import ru.ifmo.lab8.Utils;

import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.Queue;

public class ORMQuery<T> implements IORMQuery, IDbQueryable<T> {
    protected Queue<ORMAction> actionsQueue = new ArrayDeque<>();
    protected ORMTable<T> table;

    ORMQuery(ORMTable<T> table) {
        this.table = table;
    }

    ORMQuery(ORMTable<T> table, ORMAction firstAction) {
        this.table = table;
        actionsQueue.add(firstAction);
    }

    private ORMQuery(ORMQuery<T> query, ORMAction addAction) {
        this.actionsQueue.addAll(query.actionsQueue);
        this.table = query.table;
        actionsQueue.add(addAction);
    }

    @Override
    public boolean execute() {
        try (Connection conn = table.getParentORM().getConnection()) {
            if (conn == null)
                return false;

            conn.setAutoCommit(false);

            while (!actionsQueue.isEmpty()) {
                ORMAction next = actionsQueue.remove();
                next.execute(conn);
            }

            conn.commit();
            return true;
        }
        catch (Exception e) {
            Utils.printError("SQL Exception: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Object[] performSelect(SelectAction<T> action) {
        // Execute all actions before select
        execute();

        // Perform select action
        try (Connection conn = table.getParentORM().getConnection()) {
            action.execute(conn);
        } catch (Exception e) {
            Utils.printError("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return action.getResult();
    }

    @Override
    public Object[] selectAll() {
        SelectAction<T> action = new SelectAction<>(table);
        return performSelect(action);
    }

    @Override
    public Object[] selectTop(int count) {
        SelectAction<T> action = new SelectAction<>(table).orderByAsc().setLimit(count);
        return performSelect(action);
    }

    @Override
    public Object[] selectBottom(int count) {
        SelectAction<T> action = new SelectAction<>(table).orderByDesc().setLimit(count);
        return performSelect(action);
    }

    @Override
    public ORMQuery<T> insert(T toInsert) {
        ORMAction insertAction = new InsertAction<>(table, toInsert);
        return new ORMQuery<>(this, insertAction);
    }

    @Override
    public ORMQuery<T> insert(Iterable<T> toInsert) {
        ORMAction insertAction = new InsertManyAction<>(table, toInsert);
        return new ORMQuery<>(this, insertAction);
    }

    @Override
    public ORMQuery<T> remove(T toRemove) {
        ORMAction removeAction = new RemoveAction<>(table, toRemove);
        return new ORMQuery<T>(this, removeAction);
    }

    @Override
    public ORMQuery<T> removeById(int id) {
        ORMAction removeAction = new RemoveByIdAction<>(table, id);
        return new ORMQuery<T>(this, removeAction);
    }
}
