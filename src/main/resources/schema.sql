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