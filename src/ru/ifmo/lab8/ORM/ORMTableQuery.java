package ru.ifmo.lab8.ORM;

import ru.ifmo.lab8.ORM.Actions.ORMAction;
import ru.ifmo.lab8.ORM.Actions.TruncateTableAction;

public class ORMTableQuery<T> extends ORMQuery<T> {
    ORMTableQuery(ORMTable<T> table) {
        super(table);
    }

    ORMTableQuery(ORMTable<T> table, ORMAction firstAction) {
        super(table, firstAction);
    }

    public ORMQuery<T> truncate() {
        ORMAction truncateAction = new TruncateTableAction<>(table);
        return new ORMQuery<T>(table, truncateAction);
    }
}
