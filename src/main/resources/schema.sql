CREATE TABLE BUS_STOP
(
    id   bigint identity primary key,
    name varchar(255) unique not null
);

CREATE TABLE LINE
(
    id   bigint identity primary key,
    name varchar(255) unique not null
);

CREATE TABLE LINE_STOP
(
    id                   bigint identity primary key,
    bus_stop_id          bigint  not null,
    line_id              bigint  not null,
    index                integer not null,
    seconds_to_next_stop int,
    CONSTRAINT fk_bus_stop_id
        FOREIGN KEY (bus_stop_id)
            REFERENCES BUS_STOP (id),
    CONSTRAINT fk_line_id
        FOREIGN KEY (line_id)
            REFERENCES LINE (id)
);

CREATE TABLE SCHEDULE
(
    id                bigint identity primary key,
    line_id           bigint  not null,
    reverse_direction boolean not null,
    start_time        time    not null
);