create table orders
(
    id           uuid primary key,
    pet_id       uuid                     not null references pets (id) on delete restrict,
    order_status varchar(20)              not null,
    is_complete  boolean                  not null,
    ship_date    timestamp with time zone,
    quantity     integer                  not null,
    created_at   timestamp with time zone not null,
    updated_at   timestamp with time zone not null
)