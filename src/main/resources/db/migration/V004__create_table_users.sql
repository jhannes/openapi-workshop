create table USERS
(
    id            uniqueidentifier primary key,
    username      varchar(50)              not null unique,
    first_name    varchar(50)              not null,
    last_name     varchar(50)              not null,
    email         varchar(100),
    phone         varchar(50),
    password_hash varchar(100),
    created_at    datetime not null,
    updated_at    datetime not null
);