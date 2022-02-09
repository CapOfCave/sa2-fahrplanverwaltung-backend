CREATE TABLE BUS_STOP
(
    id   bigint identity primary key,
    name varchar(255) unique
);

CREATE TABLE LINE
(
    id   bigint identity primary key,
    name varchar(255) unique
);

CREATE TABLE LINE_STOP
(
    id                   bigint identity primary key,
    bus_stop_id          bigint,
    line_id              bigint,
    index                integer,
    seconds_to_next_stop int,
    CONSTRAINT fk_bus_stop_id
        FOREIGN KEY (bus_stop_id)
            REFERENCES BUS_STOP (id),
    CONSTRAINT fk_line_id
        FOREIGN KEY (line_id)
            REFERENCES LINE (id)
);