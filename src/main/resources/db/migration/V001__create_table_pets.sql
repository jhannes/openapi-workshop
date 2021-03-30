create table PETS
(
    id   uuid primary key,
    name varchar(100) not null unique,
    category_id uuid,
    status varchar(100)
);
