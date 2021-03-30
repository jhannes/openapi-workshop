create table CATEGORIES
(
    id   uuid primary key,
    name varchar(100) not null unique
);