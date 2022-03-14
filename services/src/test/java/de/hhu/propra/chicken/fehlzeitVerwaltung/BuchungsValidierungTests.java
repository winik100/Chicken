package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.student.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static templates.KlausurTemplates.*;
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
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        boolean b = buchungsValidierung.klausurAmGleichenTag(student, urlaubsStart);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Klausur am anderem Tag spielt keine Rolle")
    void test_6() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 9, 13, 10);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

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

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(klausur);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 12 bis 13 Uhr")
    void test_18() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_12_13);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 10 bis 11:30 Uhr")
    void test_19() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_10_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_10_1130);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 11 bis 12:30 Uhr")
    void test_20() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub ist identisch mit dem Freistellungszeitraum")
    void test_21() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett im Freistellungszeitraum")
    void test_22() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_930_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub umfasst den Freistellungszeitraum komplett")
    void test_23() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1030_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_1030_1130);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt vor dem Freistellungszeitraum")
    void test_24() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1130_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_1130_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt nach dem Freistellungszeitraum")
    void test_25() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(student.getKlausurAnmeldungen(), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_930_1130);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett im bestehenden Urlaub")
    void test_26() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub ist identisch mit bestehendem Urlaub")
    void test_27() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub beginnt innerhalb bestehenden Urlaubs und endet danach")
    void test_28() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub beginnt vor bestehendem Urlaub und endet innerhalb")
    void test_29() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 00);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett vor bestehendem Urlaub")
    void test_30() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 10, 00);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett nach bestehendem Urlaub")
    void test_31() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 00);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = buchungsValidierung.ueberschneidungMitBestehendemUrlaub(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

}
