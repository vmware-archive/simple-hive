DROP DATABASE IF EXISTS test_db CASCADE;
CREATE DATABASE test_db;
CREATE TABLE test_db.test_table (key STRING, value INT);
INSERT INTO test_db.test_table VALUES ("fifteen", 15), ("twenty", 20);
SELECT * FROM test_db.test_table;