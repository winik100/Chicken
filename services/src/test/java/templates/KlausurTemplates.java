package templates;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;

import java.time.LocalDateTime;

public class KlausurTemplates {

    public static final Klausur PK_12_13 = new Klausur(111111, "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "praesenz");

    public static final Klausur OK_930_1130 = new Klausur(222222, "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_930_1230 = new Klausur(333333, "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_10_1130 = new Klausur(444444, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 0),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_1030_1130 = new Klausur(555555, "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 30),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_11_12 = new Klausur(666666, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 0), "online");

    public static final Klausur OK_11_1230 = new Klausur(777777, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_1130_1230 = new Klausur(888888, "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_12_13 = new Klausur(999999, "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "online");




}
