drop table if exists studentBelegtKlausur, urlaubsEintrag, klausur, student;

create table klausur
(
    id      bigserial
        primary key,
    lsf_id bigint       not null,
    name    varchar(255) not null,
    start   timestamp    not null,
    ende    timestamp    not null,
    typ     varchar(10)  not null
);

create table student
(
    id             bigserial
        constraint student_pk
            primary key,
    github_handle varchar(255) not null,
    rest_urlaub   bigint       not null
);

create table urlaubs_eintrag
(
    id          bigserial
        constraint urlaubseintrag_pk
            primary key,
    start       timestamp   not null,
    ende        timestamp   not null,
    student_id bigint not null
        constraint urlaubseintrag_student_id_fk
            references student (id)
);

create table student_belegt_klausur
(
    student_id bigint references student(id),
    klausur_id bigint references klausur(id),
    primary key (student_id, klausur_id)
);



