create table PETS
(
    id          uuid primary key,
    name        varchar(100) not null,
    category_id uuid not null references categories (id),
    status      varchar(100)
);

create table PETS_TAGS
(
    id     uuid primary key,
    tag    varchar(100) not null,
    pet_id uuid references pets (id),
    unique (tag, pet_id)
);

create table PETS_URLS
(
    id     uuid primary key,
    url    varchar(100) not null,
    pet_id uuid references pets (id),
    unique (url, pet_id)
);