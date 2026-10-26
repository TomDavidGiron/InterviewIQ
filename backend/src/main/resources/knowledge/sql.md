---
topic: sql
tags: sql, postgresql
---
# SQL and Relational Databases

## Joins
An INNER JOIN returns only rows that have a match in both tables. A LEFT (OUTER) JOIN returns every row from the left table plus matching right rows, with NULLs where there is no match. RIGHT JOIN is the mirror image, FULL OUTER JOIN keeps unmatched rows from both sides, and CROSS JOIN produces every combination.
Example: "all customers and their orders, including customers with no orders" needs customers LEFT JOIN orders. Finding customers with no orders is the anti-join pattern: LEFT JOIN orders ... WHERE orders.id IS NULL (or NOT EXISTS). A common mistake is filtering the right table in WHERE after a LEFT JOIN, which silently turns it back into an inner join. Put that condition in the ON clause instead.

## Indexes
An index is a separate data structure, usually a B-tree, that lets the database find rows without scanning the whole table, much like a book's index. It speeds up WHERE filters, JOINs, ORDER BY and uniqueness checks, turning O(n) scans into roughly O(log n) lookups.
The trade-offs: every INSERT, UPDATE and DELETE must also update each index, so heavy indexing slows write-heavy workloads; indexes use disk and memory; and an index on a low-selectivity column (for example a boolean) is often ignored by the planner. Primary keys and unique constraints create indexes automatically. Index the columns real queries filter and join on, and confirm with EXPLAIN that they are used.

## Composite indexes
A composite index covers several columns, for example (customer_id, created_at). It follows the leftmost-prefix rule: it helps queries filtering on customer_id, or on customer_id plus created_at, but not queries filtering on created_at alone. Column order matters: put columns used for equality first, then range or sort columns, so a query like WHERE customer_id = ? ORDER BY created_at DESC can read rows straight from the index in order.
A covering index includes every column the query needs (PostgreSQL supports INCLUDE), so the table itself doesn't have to be read (an index-only scan).

## Transactions and ACID
A transaction groups statements into one unit of work that either fully succeeds (COMMIT) or has no effect (ROLLBACK). ACID: Atomicity (all or nothing; a transfer never debits one account without crediting the other), Consistency (constraints and rules hold before and after), Isolation (concurrent transactions don't see each other's partial work, to the degree set by the isolation level), Durability (committed data survives crashes, usually via a write-ahead log).
They matter whenever several related writes must stay in sync, such as orders and stock or payments and balances, and when concurrent users touch the same data.

## Isolation levels
Isolation levels trade correctness for concurrency by deciding which anomalies are possible. Dirty read: seeing another transaction's uncommitted data. Non-repeatable read: reading the same row twice and getting different values. Phantom read: re-running a query and getting new rows. Lost update: two transactions read and then overwrite each other's change.
READ UNCOMMITTED allows dirty reads (PostgreSQL treats it as READ COMMITTED). READ COMMITTED, PostgreSQL's default, sees only committed data, but each statement can see newer commits. REPEATABLE READ gives a consistent snapshot for the whole transaction. SERIALIZABLE makes concurrent transactions behave as if they ran one after another, at the cost of serialization failures the application must retry. Higher isolation means fewer anomalies but more blocking or retries.

## Keys and constraints
A primary key uniquely identifies each row. It is NOT NULL, there is one per table, and it is indexed automatically. A unique constraint also enforces uniqueness (for example email) but allows NULLs, and a table can have many. A foreign key references a primary or unique key in another table and enforces referential integrity: you can't insert an order for a customer that doesn't exist, and ON DELETE CASCADE / RESTRICT / SET NULL decides what happens when the parent row is deleted. CHECK and NOT NULL constraints enforce validity rules.
Constraints guard data integrity at the database level, even when application code has bugs or several services write to the same tables.

## Normalization and denormalization
Normalization organizes tables to remove redundancy and the update anomalies that come with it, where the same fact is stored in several places and drifts out of sync. 1NF: atomic values, no repeating groups. 2NF: no partial dependency on part of a composite key. 3NF: no transitive dependencies, meaning non-key columns depend only on the key. For example, store a customer's address once in a customers table, not on every order.
Denormalization deliberately duplicates data (pre-computed totals, copied fields, summary tables) to speed up read-heavy queries and avoid expensive joins, at the cost of extra storage and keeping the copies consistent. It's a typical choice for reporting and analytics.

## GROUP BY, HAVING and WHERE
GROUP BY collapses rows that share the same values into groups so aggregate functions (COUNT, SUM, AVG, MAX) can be computed per group. WHERE filters individual rows before grouping and cannot use aggregates. HAVING filters groups after aggregation.
Example: SELECT customer_id, COUNT(*) FROM orders WHERE status = 'PAID' GROUP BY customer_id HAVING COUNT(*) > 3 finds customers with more than three paid orders. Also: COUNT(*) counts rows, while COUNT(column) counts only non-NULL values in that column. Every selected column must either be in GROUP BY or be aggregated.

## Execution plans and query tuning
An execution plan shows how the database will run a query: scan types (sequential scan, index scan, index-only scan), join algorithms (nested loop, hash join, merge join), estimated row counts and costs. In PostgreSQL, EXPLAIN shows the plan and EXPLAIN ANALYZE actually runs the query and reports real timings and row counts.
To debug a slow query: look for sequential scans on large tables, big gaps between estimated and actual rows (stale statistics; run ANALYZE), missing indexes on filter and join columns, functions on indexed columns that prevent index use, and SELECT * fetching more data than needed. Avoid SELECT * in production: it transfers unneeded columns, blocks index-only scans, and breaks when the schema changes.

## DELETE, TRUNCATE and DROP
DELETE removes rows that match a WHERE clause (or all rows). It is row-by-row, fires triggers, is fully logged and can be rolled back. TRUNCATE removes all rows quickly by deallocating storage instead of deleting row by row, can reset identity sequences, and is still transactional in PostgreSQL. DROP removes the table itself: structure, data, indexes and constraints.
Use DELETE for selective removal, TRUNCATE to empty a table fast (for example staging tables), and DROP to remove the object entirely.

## Upserts in PostgreSQL
An upsert inserts a row, or updates it if it already exists, in one atomic statement. In PostgreSQL: INSERT ... ON CONFLICT (unique_column) DO UPDATE SET col = EXCLUDED.col, where EXCLUDED refers to the row that was proposed for insertion. ON CONFLICT DO NOTHING silently skips duplicates. It needs a unique constraint or index on the conflict target.
It avoids the race condition in "SELECT, then INSERT or UPDATE", where two concurrent requests both see no row and both insert. PostgreSQL 15+ also supports the standard MERGE statement.

## Database deadlocks
A deadlock happens when two transactions each hold a lock the other needs. Transaction A locks row 1 and waits for row 2, while transaction B locks row 2 and waits for row 1. The database detects the cycle and aborts one transaction with a deadlock error. That transaction should be retried.
Prevention: acquire locks in a consistent order (for example always update accounts in ascending id order), keep transactions short, avoid user interaction or network calls inside a transaction, use the lowest isolation level that is correct, and add indexes so updates lock fewer rows.

## SQL injection
SQL injection happens when user input is concatenated into a SQL string, letting an attacker change the query: an input like ' OR '1'='1 bypasses a login check, and '; DROP TABLE users; -- runs extra statements. The fix is parameterized queries or prepared statements (PreparedStatement with ? placeholders, JPA parameters such as :name), so input is always treated as data, never as SQL. ORMs and query builders parameterize by default, but raw or native queries built with string concatenation are still vulnerable.
Defence in depth: validate input, run with a least-privilege database user, and never show raw database errors to users.

## Migrations and schema changes in production
Database migrations are versioned, ordered scripts (Flyway or Liquibase) that evolve the schema the same way in every environment. They are kept in version control and applied automatically on deploy. Never edit an already-applied migration; add a new one.
For large production tables, avoid long locks. Adding a NOT NULL column to a table with millions of rows can lock or rewrite it. Safer steps: add the column as nullable (or with a constant default, which PostgreSQL 11+ handles without a rewrite), backfill in batches, then add the NOT NULL constraint. Create indexes with CREATE INDEX CONCURRENTLY, and make changes backward-compatible so old and new app versions can run during the deploy (expand, then contract).

## Replication, partitioning and sharding
A read replica is a copy of the primary database that receives changes through replication. It serves read traffic and failover, but replication is usually asynchronous, so replicas can return slightly stale data (replication lag), and writes still go to the primary. Partitioning splits one large table into smaller pieces inside the same database (for example by month), which speeds up queries that target one partition and makes it cheap to drop old data. Sharding spreads data across multiple database servers by a shard key (for example customer_id) to scale writes and storage beyond a single machine, at the cost of complex cross-shard queries, transactions and rebalancing.
