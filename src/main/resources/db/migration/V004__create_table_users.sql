create table users
(
    id         uuid primary key,
    username   varchar(50)              not null unique,
    first_name varchar(50)              not null,
    last_name  varchar(50)              not null,
    email      varchar(100),
    phone      varchar(50),
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);