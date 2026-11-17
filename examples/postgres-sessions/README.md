# PostgreSQL conversation sessions

Add the `session-postgres` module and configure a PostgreSQL JDBC `DataSource` (for example, a pooled Spring Boot datasource). Register one `SessionStore` bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Chengine automatically uses this bean. `initializeSchema()` creates `chengine_sessions` if it is absent; production deployments may instead create the same table through their migration process, without granting schema creation rights to the application. Use a dedicated database/schema and schedule `deleteExpired()` to reclaim expired rows. A read renews the inactivity timeout, and expired rows are never returned. Connection credentials belong in application configuration or environment variables.

The store supports `ConversationSession` including the current graph node and string values. Other `Session` implementations and the old experimental `PipelineState` are not serialized. Timeout precision below one millisecond is rounded up. The application owns the `DataSource` and its connection pool. Use a datasource whose connections are in auto-commit mode. The schema must remain compatible across versions; `initializeSchema()` does not migrate an existing table.

Per-session locking remains limited to one JVM: PostgreSQL persistence alone does not prevent concurrent updates or duplicate side effects across application instances. Use external coordination for multi-instance deployments. Saving state and sending Telegram messages are not one transaction.

Integration tests use `CHENGINE_TEST_POSTGRES_URL`, `CHENGINE_TEST_POSTGRES_USER`, and `CHENGINE_TEST_POSTGRES_PASSWORD` and delete all rows from `chengine_sessions` in that database; use a dedicated test database. Without the URL they are skipped. CI runs them against PostgreSQL.
