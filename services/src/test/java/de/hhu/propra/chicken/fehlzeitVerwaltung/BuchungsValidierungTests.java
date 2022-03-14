package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BuchungsValidierungTests {

    @Test
    @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 60 min true zurück")
    void test_1() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 66 min false zurück")
    void test_2() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 6);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist die Startzeit ein Vielfaches von 15 (z.B. 12:00) wird true zurückgegeben")
    void test_3() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Ist die Startzeit kein Vielfaches von 15 (z.B. 12:10) wird false zurückgegeben")
    void test_4() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 10);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Klausur am gleichen Tag wird erkannt (unabhängig von Uhrzeit)")
    void test_5() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime klausurStart = LocalDateTime.of(2022, 3, 8, 12, 10);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 13, 10);
        LocalDateTime klausurEnde = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        Klausur klausur = new Klausur(234567, "Mathe", klausurStart, klausurEnde, "praesenz");
        student.klausurAnmelden(klausur);

        boolean b = buchungsValidierung.klausurAmGleichenTag(student, urlaubsStart);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Klausur am anderem Tag spielt keine Rolle")
    void test_6() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime klausurStart = LocalDateTime.of(2022, 3, 8, 12, 10);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 9, 13, 10);
        LocalDateTime klausurEnde = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        Klausur klausur = new Klausur(234567, "Mathe", klausurStart, klausurEnde, "praesenz");
        student.klausurAnmelden(klausur);

        boolean b = buchungsValidierung.klausurAmGleichenTag(student, urlaubsStart);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ganzer Tag Urlaub ist erlaubt")
    void test_7() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub unter 150 Min ist erlaubt")
    void test_8() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub >150 Min, aber <240 Min ist nicht erlaubt")
    void test_9() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub in der Mitte des Tages gebucht, schlägt die zweite Buchung fehl")
    void test_10() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann kein zweiter Urlaub mit 60 Minuten Abstand gebucht werden")
    void test_11() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann kein zweiter Urlaub mit 60 Minuten Abstand gebucht werden")
    void test_12() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann kein zweiter Urlaub mit 90 Minuten " +
            "Abstand aber nicht am Ende des Tages gebucht werden")
    void test_13() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 15);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann kein zweiter Urlaub mit 90 Minuten " +
            "Abstand aber nicht am Anfang des Tages gebucht werden")
    void test_14() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 45);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann ein zweiter Urlaub am Ende des Tages mit" +
            "90 Minuten Abstand gebucht werden")
    void test_15() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann ein zweiter Urlaub am Anfang des Tages mit" +
            "90 Minuten Abstand gebucht werden")
    void test_16() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startErsterUrlaub, endeErsterUrlaub);
        LocalDateTime startZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeZweiterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startZweiterUrlaub, endeZweiterUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 12 bis 13 Uhr")
    void test_17() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 12 bis 13 Uhr")
    void test_18() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 13, 0);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 10 bis 11:30 Uhr")
    void test_19() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 11, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 11 bis 12:30 Uhr")
    void test_20() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 12, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub ist identisch mit dem Freistellungszeitraum")
    void test_21() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 12, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett im Freistellungszeitraum")
    void test_22() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 12, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub umfasst den Freistellungszeitraum komplett")
    void test_23() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 11, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt vor dem Freistellungszeitraum")
    void test_24() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 12, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt nach dem Freistellungszeitraum")
    void test_25() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime startKlausur = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeKlausur = LocalDateTime.of(2022, 3, 8, 11, 30);
        Klausur klausur = new Klausur(234567, "Mathe", startKlausur, endeKlausur, "online");
        student.klausurAnmelden(klausur);

        List<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student, startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(klausur);
    }
}
