-->>DropAll
DROP TABLE sqltest_table;
DROP TABLE db_update_test;
DROP TABLE person;
DROP TABLE invoice;
DROP TABLE invoice_line;
DROP TABLE company;

-->>first_update
CREATE TABLE db_update_test (
  id   INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  name VARCHAR(256)
);
-->>create_test_table
CREATE TABLE sqltest_table (
  id           INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  created_date TIMESTAMP       NOT NULL,
  module_name  VARCHAR(80)     NOT NULL,
  class_name   VARCHAR(80)     NOT NULL,
  method_name  VARCHAR(80)     NOT NULL
);

-->>withJavaUpdateTest


-->>createCompany
CREATE TABLE company (
  id                 INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  adres_street       VARCHAR(256)    NOT NULL,
  adres_house_number INT             NOT NULL,
  adres_bus_number   VARCHAR(10),
  adres_postalcode   VARCHAR(15)     NOT NULL,
  adres_city         VARCHAR(50)     NOT NULL,
  adres_country      VARCHAR(80)     NOT NULL
);


-->>createPerson
CREATE TABLE person (
  id           INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  user_name    VARCHAR(256)    NOT NULL,
  password     VARCHAR(256)    NOT NULL,
  street       VARCHAR(256)    NOT NULL,
  house_number INT             NOT NULL,
  bus_number   VARCHAR(10),
  postalcode   VARCHAR(15)     NOT NULL,
  city         VARCHAR(50)     NOT NULL,
  country      VARCHAR(80)     NOT NULL

);
-->>createInvoices
CREATE TABLE invoice (
  id              INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  invoice_nummer  VARCHAR(20)     NOT NULL,
  from_company_id INT             NOT NULL REFERENCES company,
  to_company_id   INT             NOT NULL REFERENCES company
);
CREATE TABLE invoice_line (
  id         INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY ( START WITH 1, INCREMENT BY 1),
  invoice_id INT             NOT NULL,
  product    VARCHAR(256)
);

-->>
