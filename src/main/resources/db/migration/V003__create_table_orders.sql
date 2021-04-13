create table ORDERS
(
    id           uniqueidentifier primary key,
    pet_id       uniqueidentifier                     not null references pets (id),
    order_status varchar(20)              not null,
    is_complete  bit                  not null,
    ship_date    datetime,
    quantity     integer                  not null,
    created_at   datetime not null,
    updated_at   datetime not null
)