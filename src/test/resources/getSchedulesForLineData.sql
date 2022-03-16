INSERT INTO BUS_STOP (name)
VALUES ('Abbey Road'),
       ('Barn Street'),
       ('Camp Street');

INSERT INTO LINE (name)
VALUES ('L1');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),  -- start   at 06:00   ///  arrival   at 11:03
       (1, 2, 1, 120), -- arrival at 06:01   ///  arrival   at 11:02
       (1, 3, 2, 180); -- arrival at 06:03   ///  start     at 11:00

INSERT INTO SCHEDULE (line_id, reverse_direction, start_time)
VALUES (1, false, '06:00'),
       (1, true, '11:00');