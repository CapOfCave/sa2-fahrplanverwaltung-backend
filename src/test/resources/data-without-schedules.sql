INSERT INTO BUS_STOP (name)
VALUES ('Abbey Road'),
       ('Barn Street'),
       ('Camp Street');

INSERT INTO LINE (name)
VALUES ('S65');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),
       (1, 3, 2, null),
       (1, 2, 1, 30);