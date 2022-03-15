package templates;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.klausur.LsfId;

import java.time.LocalDateTime;

public class KlausurTemplates {

    public static final Klausur PK_12_13 = new Klausur(new LsfId(111111L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "praesenz");

    public static final Klausur OK_930_1130 = new Klausur(new LsfId(222222L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_930_1230 = new Klausur(new LsfId(333333L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 9, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_10_1130 = new Klausur(new LsfId(444444L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 0),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_1030_1130 = new Klausur(new LsfId(555555L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 10, 30),
            LocalDateTime.of(2022, 3, 8, 11, 30), "online");

    public static final Klausur OK_11_12 = new Klausur(new LsfId(666666L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 0), "online");

    public static final Klausur OK_11_1230 = new Klausur(new LsfId(777777L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 0),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_1130_1230 = new Klausur(new LsfId(888888L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 11, 30),
            LocalDateTime.of(2022, 3, 8, 12, 30), "online");

    public static final Klausur OK_12_13 = new Klausur(new LsfId(999999L), "Mathe",
            LocalDateTime.of(2022, 3, 8, 12, 0),
            LocalDateTime.of(2022, 3, 8, 13, 0), "online");




}
