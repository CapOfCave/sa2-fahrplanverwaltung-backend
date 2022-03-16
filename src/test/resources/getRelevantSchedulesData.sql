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
VALUES (1, 1, 0, 60),  -- start   at 06:00
       (1, 4, 1, 120), -- arrival at 06:01
       (1, 3, 2, 180), -- arrival at 06:03

       (2, 1, 0, 60),  -- arrival at 11:03
       (2, 5, 1, 120), -- arrival at 11:02
       (2, 3, 2, 180); -- start   at 11:00

INSERT INTO SCHEDULE (line_id, reverse_direction, start_time)
VALUES (1, false, '06:00'),
       (2, true, '11:00');