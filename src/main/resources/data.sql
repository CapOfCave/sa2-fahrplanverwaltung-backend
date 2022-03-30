INSERT INTO BUS_STOP (name)
VALUES ('Schlossallee'),
       ('Parkstrasse'),
       ('Hauptbahnhof'),
       ('Bahnhofstrasse'),
       ('Hauptstrasse'),
       ('Rathausplatz'),
       ('Goethestrasse'),
       ('Wasser-Werk'),
       ('Schillerstrasse'),
       ('Lessingstrasse'),
       ('Opernplatz'),
       ('Museumstrasse'),
       ('Theaterstrasse');


INSERT INTO LINE (name)
VALUES ('1'),
       ('10'),
       ('S40'),
       ('N89'),
       ('U17'),
       ('S6-A'),
       ('42');

INSERT INTO LINE_STOP (line_id, bus_stop_id, index, seconds_to_next_stop)
VALUES (1, 1, 0, 60),
       (1, 2, 1, 60),
       (1, 3, 2, 120),
       (1, 4, 3, 300),
       (1, 5, 4, 600),
       (1, 6, 5, 120),
       (1, 7, 6, null),
       (2, 8, 0, 60),
       (2, 9, 1, 120),
       (2, 10, 2, 180),
       (2, 11, 3, 240),
       (2, 12, 4, 60),
       (2, 13, 5, 60),
       (2, 1, 6, null),
       (3, 2, 0, 300),
       (3, 3, 1, 120),
       (3, 4, 2, 180),
       (3, 1, 3, 60),
       (3, 13, 4, 600),
       (3, 3, 5, 300),
       (3, 9, 6, null),
       (4, 10, 0, 600),
       (4, 5, 1, 300),
       (4, 11, 2, 180),
       (4, 7, 3, 240),
       (4, 1, 4, 600),
       (4, 13, 5, 60),
       (4, 2, 6, null),
       (5, 4, 0, 60),
       (5, 9, 1, 60),
       (5, 1, 2, 180),
       (5, 8, 3, 240),
       (5, 6, 4, 60),
       (5, 5, 5, 60),
       (5, 3, 6, null),
       (6, 10, 0, 60),
       (6, 11, 1, 120),
       (6, 12, 2, 300),
       (6, 13, 3, 240),
       (6, 6, 4, 600),
       (6, 9, 5, 600),
       (6, 8, 6, null),
       (7, 7, 0, 60),
       (7, 8, 1, 120),
       (7, 9, 2, 180),
       (7, 10, 3, 240),
       (7, 11, 4, 300),
       (7, 12, 5, 600),
       (7, 13, 6, null);

INSERT INTO SCHEDULE (line_id, reverse_direction, start_time)
VALUES (1, false, '14:35'),
       (2, false, '15:35'),
       (3, false, '16:35'),
       (4, false, '17:35'),
       (5, false, '18:35'),
       (6, false, '19:35'),
       (7, false, '20:35'),
       (1, false, '21:35'),
       (2, false, '22:35'),
       (3, true, '14:55'),
       (4, true, '15:55'),
       (5, true, '16:55'),
       (6, true, '17:55'),
       (7, true, '18:55'),
       (1, true, '19:55'),
       (2, true, '20:55'),
       (3, true, '21:55'),
       (4, true, '22:55'),
       (5, false, '14:12'),
       (6, false, '15:12'),
       (7, false, '16:12'),
       (1, false, '17:12'),
       (2, false, '18:12'),
       (3, false, '19:12'),
       (4, false, '20:12'),
       (5, false, '21:12'),
       (6, false, '22:12'),
       (7, true, '14:31'),
       (1, true, '15:31'),
       (2, true, '16:31'),
       (3, true, '17:31'),
       (4, true, '18:31'),
       (5, true, '19:31'),
       (6, true, '20:31'),
       (7, true, '21:31'),
       (1, true, '19:05'),
       (2, true, '20:05'),
       (3, true, '21:05'),
       (4, true, '22:05'),
       (5, false, '14:42'),
       (6, false, '15:42'),
       (7, false, '16:42'),
       (1, false, '17:42'),
       (2, false, '18:42'),
       (3, false, '19:42'),
       (4, false, '20:42'),
       (5, false, '21:42'),
       (6, false, '22:42'),
       (7, true, '11:01'),
       (1, true, '10:01'),
       (2, true, '09:01'),
       (3, true, '08:01'),
       (4, true, '07:01'),
       (5, true, '06:01'),
       (6, true, '05:01'),
       (7, true, '04:01');




