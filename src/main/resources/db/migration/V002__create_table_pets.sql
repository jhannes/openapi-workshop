create table PETS
(
    id          uuid primary key,
    name        varchar(100) not null,
    category_id uuid references categories (id),
    status      varchar(100)
);
