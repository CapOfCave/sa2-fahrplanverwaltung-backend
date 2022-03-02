INSERT INTO BUS_STOP (name)
VALUES ('Abbey Road'),
       ('Barn Street'),
       ('Camp Street'),
       ('Dean Avenue'),
       ('East Hills Avenue');

INSERT INTO LINE (name)
VALUES ('L1'),
VALUES ('L2');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 18000),
       (1, 4, 1, 25200),
       (1, 3, 2, 18000),
       (2, 1, 0, 18000),
       (2, 5, 1, 25200),
       (2, 3, 2, null);

INSERT INTO SCHEDULE (line_id, reverse_direction, start_time)
VALUES (1, false, '22:00'),
       (2, true, '21:30');