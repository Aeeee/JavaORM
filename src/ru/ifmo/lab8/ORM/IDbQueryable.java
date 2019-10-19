package ru.ifmo.lab8.ORM;

public interface IDbQueryable<T> {
    Object[] selectAll();
    Object[] selectTop(int count);
    Object[] selectBottom(int count);
    ORMQuery<T> insert(T toInsert);
    ORMQuery<T> insert(Iterable<T> toInsert);
    ORMQuery<T> remove(T toRemove);
    ORMQuery<T> removeById(int id);
}
