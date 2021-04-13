create table CATEGORIES
(
    id   uniqueidentifier primary key,
    name varchar(100) not null unique
);

insert into CATEGORIES (id, name) values 
('00000000-0000-0000-1000-000000000001', 'cat'),
('00000000-0000-0000-1000-000000000002', 'dog');