-->>DropAll
DROP TABLE test_table;
DROP TABLE db_update_test;
DROP TABLE person;
DROP TABLE invoice;
DROP TABLE invoice_line;

-->>first_update
CREATE TABLE db_update_test (
  id   INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  name VARCHAR(256)
);
-->>create_test_table
CREATE TABLE test_table (
  id           INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  created_date TIMESTAMP       NOT NULL,
  module_name  VARCHAR(80)     NOT NULL,
  class_name   VARCHAR(80)     NOT NULL,
  method_name  VARCHAR(80)     NOT NULL
);

-->>withJavaUpdateTest

-->>createPerson
CREATE TABLE person (
  id        INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  user_name VARCHAR(256)    NOT NULL,
  password  VARCHAR(256)    NOT NULL
);
-->>createInvoices
CREATE TABLE invoice (
  id             INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  invoice_nummer VARCHAR(20)     NOT NULL,
  from_person_id INT             NOT NULL,
  to_person_id   INT             NOT NULL
);
CREATE TABLE invoice_line (
  id         INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  invoice_id INT             NOT NULL,
  product    VARCHAR(256)
);
-->>