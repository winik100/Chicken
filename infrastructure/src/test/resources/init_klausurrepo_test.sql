CREATE TABLE IF NOT EXISTS "klausur"
(
    id    int primary key auto_increment,
    "lsf_id"  int not null,
    NAME    varchar(255) not null,
    START   timestamp not null,
    ENDE    timestamp not null,
    TYP     varchar(10) not null
);

INSERT INTO "klausur" (id, "lsf_id", NAME, START, ENDE, TYP) VALUES (1, 999999, 'iwas fuer info',  '2022-03-08 10:00:00', '2022-03-08 11:00:00', 'praesenz');
INSERT INTO "klausur" (id, "lsf_id", NAME, START, ENDE, TYP) VALUES (2, 888888, 'iwas anderes fuer info',  '2022-03-08 11:00:00', '2022-03-08 12:00:00', 'praesenz');