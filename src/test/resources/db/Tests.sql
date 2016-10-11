-->>create_test_table
CREATE TABLE test_table (
  id INTEGER PRIMARY KEY NOT NULL,
  createddate TIMESTAMP NOT NULL,
  modulename VARCHAR(80) NOT NULL,
  classname VARCHAR(80) NOT NULL,
  methodname VARCHAR(80) NOT NULL
)
-->>