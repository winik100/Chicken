CREATE TABLE IF NOT EXISTS "klausur"
(
    id    int primary key auto_increment,
    "lsf_id"  int not null,
    name    varchar(255) not null,
    start   timestamp not null,
    ende    timestamp not null,
    typ     varchar(10) not null
);

CREATE TABLE IF NOT EXISTS "student"
(
    id int primary key auto_increment,
    "github_handle" varchar(255) not null,
    "rest_urlaub" int not null
);

create table if not exists "urlaubs_eintrag"
(
    id          int primary key auto_increment,
    start       timestamp   not null,
    ende        timestamp   not null,
    "student_id"     int not null,
    foreign key ("student_id") references "student" (id)
);

CREATE TABLE IF NOT EXISTS "student_belegt_klausur"
(
    ID int not null,
    KLAUSUR_ID int not null,
    foreign key (ID) references "student"(id),
    foreign key (KLAUSUR_ID) references "klausur"(id),
    primary key (ID, KLAUSUR_ID)
);



INSERT INTO "student" (id, "github_handle", "rest_urlaub") VALUES (1, 'testhandle_1', 240);

INSERT INTO "student" (id, "github_handle", "rest_urlaub") VALUES (2, 'testhandle_2', 120);
INSERT INTO "urlaubs_eintrag" (id, start, ende, "student_id") VALUES (1, '2022-03-17 09:30:00', '2022-03-17 11:30:00', 2);

INSERT INTO "student" (id, "github_handle", "rest_urlaub") VALUES (3, 'testhandle_3', 0);
INSERT INTO "urlaubs_eintrag" (id, start, ende, "student_id") VALUES (3, '2022-03-17 09:30:00' , '2022-03-17 11:30:00', 3);
INSERT INTO "urlaubs_eintrag" (id, start, ende, "student_id") VALUES (4, '2022-03-18 10:30:00' , '2022-03-18 12:30:00', 3);

INSERT INTO "student" (id, "github_handle", "rest_urlaub") VALUES (4, 'testhandle_4', 180);
INSERT INTO "urlaubs_eintrag" (id, start, ende, "student_id") VALUES (5, '2022-03-17 09:30:00' , '2022-03-17 10:30:00', 4);