package de.hhu.propra.chicken.aggregates.klausur;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class KlausurTest {

    private final static LsfId lsfId = new LsfId(234567L);

    @Test
    @DisplayName("Berechnung der Dauer korrekt")
    void test1() {

        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "praesenz");

        Long dauer = klausur.dauer();

        assertThat(dauer).isEqualTo(60L);
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur um 12:00 wird die Freistellung ab 10:00 gewährt")
    void test2() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "praesenz");

        final LocalDateTime startFreistellung = klausur.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 10, 0));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur um 10:00 wird die Freistellung ab 9:30 gewährt, also Praktikumsbeginn")
    void test3() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 11, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "praesenz");

        final LocalDateTime startFreistellung = klausur.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 9, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur um 12:00 wird die Freistellung ab 11:30 gewährt")
    void test4() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "online");

        final LocalDateTime startFreistellung = klausur.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 11, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur um 9:45 wird die Freistellung ab 09:30 gewährt")
    void test5() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 45);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 10, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "online");

        final LocalDateTime startFreistellung = klausur.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 9, 30));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur bis 11:00 wird die Freistellung bis 13:00 gewährt")
    void test6() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 11, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "praesenz");

        final LocalDateTime endeFreistellung = klausur.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 0));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur bis 13:00 wird die Freistellung bis 13:30 gewährt, also Praktkumsende")
    void test7() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "praesenz");

        final LocalDateTime endeFreistellung = klausur.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur bis 13:00 wird die Freistellung bis 13:00 gewährt.")
    void test8() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "online");

        final LocalDateTime endeFreistellung = klausur.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 0));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur bis 14:00 wird die Freistellung bis 13:30 gewährt, also Praktikumsende.")
    void test9() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 14, 0);
        Klausur klausur = new Klausur(lsfId, "Mathe", start, ende, "online");

        final LocalDateTime endeFreistellung = klausur.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 30));
    }

}
