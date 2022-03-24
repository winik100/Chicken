package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;

class KlausurTest {


    @Test
    @DisplayName("Bei einer Präsenzklausur um 12:00 wird die Freistellung ab 10:00 gewährt")
    void test2() {
        LocalDateTime startFreistellung = PK_12_13.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 10, 0));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur um 10:00 wird die Freistellung ab 9:30 gewährt, also Praktikumsbeginn")
    void test3() {
        LocalDateTime startFreistellung = PK_10_11.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 9, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur um 12:00 wird die Freistellung ab 11:30 gewährt")
    void test4() {
        LocalDateTime startFreistellung = OK_12_13.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 11, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur um 9:45 wird die Freistellung ab 09:30 gewährt")
    void test5() {
        LocalDateTime startFreistellung = OK_945_1045.startFreistellungBerechnen();

        assertThat(startFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 9, 30));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur bis 11:00 wird die Freistellung bis 13:00 gewährt")
    void test6() {
        LocalDateTime endeFreistellung = PK_10_11.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 0));
    }

    @Test
    @DisplayName("Bei einer Präsenzklausur bis 13:00 wird die Freistellung bis 13:30 gewährt, also Praktkumsende")
    void test7() {
        LocalDateTime endeFreistellung = PK_12_13.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 30));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur bis 13:00 wird die Freistellung bis 13:00 gewährt.")
    void test8() {
        LocalDateTime endeFreistellung = OK_12_13.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 0));
    }

    @Test
    @DisplayName("Bei einer Onlineklausur bis 14:00 wird die Freistellung bis 13:30 gewährt, also Praktikumsende.")
    void test9() {
        LocalDateTime endeFreistellung = OK_13_14.endeFreistellungBerechnen();

        assertThat(endeFreistellung).isEqualTo(LocalDateTime.of(2022, 3, 8, 13, 30));
    }

}
