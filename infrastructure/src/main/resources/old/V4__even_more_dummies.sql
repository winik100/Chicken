-- student

    --(10, 'winik100', 240);

insert into student (id, github_handle, rest_urlaub)
values (42, 'eljue100', 0),
    (57, 'danielrieger', 90),
    (48, 'wummlamm', 180),
    (83, 'charzim', 240);

-- klausur

    -- values (1, 219960, 'Mathe für Info', '2022-03-08 10:00:00', '2022-03-08 11:00:00', 'praesenz');

insert into klausur (id, lsf_id, name, start, ende, typ)
values (2, 219468, 'Rechnerarchitektur', '2022-03-15 08:30:00', '2022-03-15 09:30:00', 'online'),
    (3, 215773, 'Machine Learning', '2022-03-15 11:30:00', '2022-03-15 13:00:00', 'online'),
    (4, 219469, 'Wissenschaftliches Arbeiten', '2022-03-17 11:30:00', '2022-03-17 12:30:00', 'online'),
    (5, 214613, 'Stochastik', '2022-03-25 12:30:00', '2022-03-25 13:30:00', 'praesenz');

-- student belegt klausur

insert into student_belegt_klausur (id, klausur_id)
values (10, 4),
    (42, 5),
    (48, 5),
    (57, 4),
    (83, 2),
    (83, 3);

-- urlaubs eintrag

insert into urlaubs_eintrag (id, start, ende, student_id)
values (1, '2022-03-17 09:30:00', '2022-03-17 11:00:00', 57),
    (2, '2022-03-17 12:30:00', '2022-03-17 13:30:00', 57),
    (3, '2022-03-10 09:30:00', '2022-03-10 13:30:00', 42),
    (4, '2022-03-22 9:30:00', '2022-03-22 10:30:00', 48);