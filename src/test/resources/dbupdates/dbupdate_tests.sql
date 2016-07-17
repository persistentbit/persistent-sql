-->>first update
create table db_update_test(
  id int PRIMARY KEY not null,
  name varchar(256)
)

-->>withJavaUpdateTest

-->>

-->>createPerson
create table person(
 id int PRIMARY  key not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
 user_name varchar(256) not null,
 password varchar(256) not null
)

-->>createInvoices
create table invoice(
 id int PRIMARY  key not null GENERATED always as identity (start with 1, increment by 1),
 invoice_nummer varchar(20) not null,
 from_person_id int not null,
 to_person_id int not null
);
create table invoice_line(
 id int PRIMARY  key not null GENERATED always as identity (start with 1, increment by 1),
 invoice_id int not null,
 product varchar(256)
);
-->>