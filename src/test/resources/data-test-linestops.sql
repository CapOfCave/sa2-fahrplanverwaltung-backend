INSERT INTO BUS_STOP (name)
VALUES ('Abbey Road'),
       ('Barn Street'),
       ('Camp Street'),
       ('Dean Avenue'),
       ('East Hills Avenue'),
       ('Farmer''s lane'),
       ('Gold Street'),
       ('Zosnul Street');

INSERT INTO LINE (name)
VALUES ('N63');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),
       (1, 2, 1, 120),
       (1, 3, 2, 240),
       (1, 4, 3, null);