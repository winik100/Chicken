create table if not exists klausur
(
    id      bigserial primary key,
    lsf_id  bigint       not null,
    name    varchar(255) not null,
    start   timestamp    not null,
    ende    timestamp    not null,
    typ     varchar(10)  not null
);

create table if not exists student
(
    id            bigserial primary key,
    github_handle varchar(255) not null,
    rest_urlaub   bigint       not null
);

create table if not exists urlaubs_eintrag
(
    id          bigserial primary key,
    start       timestamp   not null,
    ende        timestamp   not null,
    student_id     bigint references student (id)
);

create table if not exists student_belegt_klausur
(
    id bigint references student(id),
    klausur_id bigint references klausur(id),
    primary key (id, klausur_id)
);



