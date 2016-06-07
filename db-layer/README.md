db-layer
========

_Data layer to perform operations on a MySQL instance._

--------

This module implements the data layer accessors defined in the `core` module to allow persisting and reading object from a MySQL database.

### Configuration

To configure the database you need to have a `database.credentials` file in the root project directory, with the following properties defined:

* `DB_USER`, username for the database instance
* `DB_PASSWORD`, password for the database instance
* `DB_CONNECTION_STRING`, JDBS connection string to the database instance

For an example, see the [`database.credentials.sample` file](../database.credentials.sample);

### Usage

To use the components of this module, you need to retrieve an instance of `DbConnectionManager`, then use it to retrieve data layers' instances,
for example:

```java
ConnectionManager connectionManager = DbConnectionManager.newInstance();
DbRepoDataLayer repoDataLayer = DbRepoDataLayer.newInstance(connectionManager);
// use repoDataLayer
```

**NOTE**: Please note that, even though `DbConnectionManager` returns reusable pooled connections, you still need to close it after you finish using
it for your transactions (not necessary if you just use the available data layer classes).

### Build

The project uses jOOQ as a dynamic query building library. If you decide to swap out MySQL in favour of another DBMS, simply change the jOOQ
configuration in [`build.gradle`](build.gradle).

The SQL generation file is stored in [`sql/generate.sql`](sql/generate.sql).
Successful migration files should be put in the same folder for consistency.

When changing the database schema, you need to re-generate the Java models: to do so, simply run the Gradle task `generateReportsJooqSchemaSource`.
