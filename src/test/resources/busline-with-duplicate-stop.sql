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
VALUES ('1');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),
       (1, 4, 1, 120),
       (1, 5, 2, 47),
       (1, 1, 3, null);


INSERT INTO SCHEDULE (line_id, reverse_direction, start_time)
VALUES (1, false, '14:35');
