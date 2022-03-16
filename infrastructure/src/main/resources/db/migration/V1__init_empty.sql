drop table if exists student, klausur, "urlaubsEintrag";

create table klausur
(
    "lsfId" int          not null,
    name    varchar(255) not null,
    start   date         not null,
    ende    date         not null,
    typ     varchar(10)  not null,
    constraint KLAUSUR_PK
        primary key ("lsfId")
);

create table student
(
    id             long auto_increment,
    "githubHandle" varchar(255) not null,
    "restUrlaub"   long         not null,
    constraint STUDENT_PK
        primary key (id)
);

create table "urlaubsEintrag"
(
    id         long auto_increment,
    start      date not null,
    ende       date not null,
    student_id long not null,
    constraint URLAUBSEINTRAG_PK
        primary key (id),
    constraint URLAUBSEINTRAG_STUDENT_ID_FK
        foreign key (student_id) references student (id)
);

create table "studentBelegtKlausur"
(
    student_id long not null,
    klausur_id long not null,
    constraint STUDENTBELEGTKLAUSUR_PK
        primary key (student_id),
    constraint STUDENTBELEGTKLAUSUR_PK
        primary key (klausur_id),
    constraint STUDENTBELEGTKLAUSUR_STUDENT_ID_FK
        foreign key (student_id) references student (id),
    constraint STUDENTBELEGTKLAUSUR_KLAUSUR_ID_FK
        foreign key (student_id) references klausur ("lsfId")
)



