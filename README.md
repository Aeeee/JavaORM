# JavaORM
A basic ORM implementation using Java Reflection.

This is an educational ORM and is not suitable for production projects.
It has, however, pretty simple sintax and therefore can be used for quick-and-dirty programs when you just want to test something really fast.

## Features
- Table creation (class nesting is supported)
- Table operations: truncate, drop
- Data selection operations: SELECT, SELECT N FIRST/LAST entries
- Data modification operations: INSERT, INSERT MANY, DELETE (by id or by class instance)

## Usage:
1. Mark all the fields in your class that should be accessed by ORM by @ORMAccessible annotation.

2. Create ORM Settings class that implements IDbSettings interface.

3. Use ORM as follows:

```Java
// Prepare ORM
SimpleORM orm = new SimpleORM(settings);

// Prepare table
ORMTable<MyClass> table = new ORMTable<>(orm);

// Execute queries
MyClass[] selectedInstances = table.selectAll();
table.insert(instanceToInsert).execute();
int count = table.truncate().insert(myCollection).getCount(); // Queries can be chained
```
