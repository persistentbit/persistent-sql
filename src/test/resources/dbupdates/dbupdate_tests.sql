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

-->>