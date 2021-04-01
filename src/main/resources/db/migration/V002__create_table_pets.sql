create table PETS
(
    id          uuid primary key,
    name        varchar(100) not null,
    category_id uuid         not null references categories (id) on delete cascade,
    status      varchar(100)
);

create table PETS_TAGS
(
    id     uuid primary key,
    tag    varchar(100) not null,
    pet_id uuid references pets (id) on delete cascade,
    unique (tag, pet_id)
);

create table PETS_IMAGES
(
    id       uuid primary key,
    pet_id   uuid references pets (id) on delete cascade,
    filename varchar(100) not null,
    content  blob not null
);