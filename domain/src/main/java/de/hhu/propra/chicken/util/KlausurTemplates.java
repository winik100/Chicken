package de.hhu.propra.chicken.util;

import de.hhu.propra.chicken.aggregates.Klausur;

import java.time.LocalDateTime;

public class KlausurTemplates {

    public static final Klausur PK_10_11 = new Klausur(10L, 111112L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 0),
            LocalDateTime.of(2022, 3, 8, 11, 0), "praesenz");

    public static final Klausur PK_12_13 = new Klausur(1L, 111111L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "praesenz");

    public static final Klausur OK_930_1130 = new Klausur(2L, 222222L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_945_1045 = new Klausur(11L, 222223L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 45),
            LocalDateTime.of(2022, 3, 8, 10, 45), "online");

    public static final Klausur OK_930_1230 = new Klausur(3L, 333333L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_10_1130 = new Klausur(4L, 444444L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 0),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_1015_11 = new Klausur(41L, 444445L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 15),
            LocalDateTime.of(2022, 3, 8, 11, 0), "online");

    public static final Klausur OK_1030_1130 = new Klausur(5L, 555555L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 30),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_11_12 = new Klausur(6L, 666666L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 0), "online");

    public static final Klausur OK_11_1230 = new Klausur(7L, 777777L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_1130_1230 = new Klausur(8L, 888888L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_12_13 = new Klausur(9L, 999999L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "online");

    public static final Klausur OK_13_14 = new Klausur(13L, 111115L, "Mathe",
            LocalDateTime.of(2022, 3, 8, 13, 0),
            LocalDateTime.of(2022, 3, 8, 14, 0), "online");




}
