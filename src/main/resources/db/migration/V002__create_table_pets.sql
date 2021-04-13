create table PETS
(
    id          uniqueidentifier primary key,
    name        varchar(100) not null,
    category_id uniqueidentifier         not null references categories (id) on delete cascade,
    status      varchar(100)
);

create table PETS_TAGS
(
    id     uniqueidentifier primary key,
    tag    varchar(100) not null,
    pet_id uniqueidentifier references pets (id) on delete cascade,
    unique (tag, pet_id)
);

create table PETS_IMAGES
(
    id       uniqueidentifier primary key,
    pet_id   uniqueidentifier references pets (id) on delete cascade,
    filename varchar(100) not null,
    content  varbinary not null
);