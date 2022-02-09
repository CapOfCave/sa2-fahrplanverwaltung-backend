INSERT INTO BUS_STOP (name)
VALUES ('Abbey Road'),
       ('Barn Street'),
       ('Camp Street'),
       ('Dean Avenue'),
       ('East Hills Avenue'),
       ('Farmer''s lane'),
       ('Gold Street');

INSERT INTO LINE (name)
VALUES ('1'),
       ('10'),
       ('S40'),
       ('N89');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),
       (1, 3, 1, 180),
       (1, 4, 2, 120),
       (1, 5, 3, null),
       (3, 2, 0, 600),
       (3, 7, 1, null),
       (4, 1, 0, 60),
       (4, 2, 1, 120),
       (4, 3, 2, 180),
       (4, 4, 3, 60),
       (4, 5, 4, 60),
       (4, 6, 5, 60),
       (4, 7, 6, null);

