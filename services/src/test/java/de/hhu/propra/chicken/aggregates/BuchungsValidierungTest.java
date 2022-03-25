package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BuchungsValidierungTest {

    private final static String STARTZEIT = "09:30";
    private final static String ENDZEIT = "13:30";
    private final static String STARTTAG = "2022-03-07";
    private final static String ENDTAG = "2022-03-25";

    private final BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
    
    @Test
    @DisplayName("Eine Dauer von 60 Minuten ist ein Vielfaches von 15.")
    void test_1() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Dauer von 66 Minuten ist ein Vielfaches von 15.")
    void test_2() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 6);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Die Startzeit 12 Uhr ist ein Vielfaches von 15.")
    void test_3() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Die Startzeit 12:10 Uhr ist ein Vielfaches von 15.")
    void test_4() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 10);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Zwei Klausuren am 08.03.2022 sind am gleichen Tag, unabhängig von der Uhrzeit.")
    void test_5() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        boolean b = buchungsValidierung.klausurAmGleichenTag(Set.of(PK_12_13), urlaubsStart);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Klausur am 08.03.2022 und eine am 09.03.2022 sind nicht am gleichen Tag.")
    void test_6() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 9, 13, 10);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        boolean b = buchungsValidierung.klausurAmGleichenTag(Set.of(PK_12_13), urlaubsStart);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ein Urlaub von der Startzeit des Praktikums bis zur Endzeit dauert den ganzen Praktikumstag.")
    void test_7() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub von 9:30 bis 10:30 dauert weniger als 150 Minuten.")
    void test_8() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub von 9:30 bis 12:30 dauert weder den ganzen Praktikumstag noch weniger als 150 Minuten.")
    void test_9() {
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub in der Mitte des Tages gebucht, schlägt die zweite Buchung fehl.")
    void test_10() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann kein zweiter Urlaub mit 60 Minuten Abstand " +
            "gebucht werden.")
    void test_11() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann kein zweiter Urlaub mit 60 Minuten Abstand " +
            "gebucht werden.")
    void test_12() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann kein zweiter Urlaub mit 90 Minuten " +
            "Abstand aber nicht am Ende des Tages gebucht werden.")
    void test_13() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 15);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann kein zweiter Urlaub mit 90 Minuten " +
            "Abstand aber nicht am Anfang des Tages gebucht werden.")
    void test_14() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 9, 45);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Anfang des Tages gebucht, kann ein zweiter Urlaub am Ende des Tages mit" +
            "90 Minuten Abstand gebucht werden.")
    void test_15() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 10, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub am Ende des Tages gebucht, kann ein zweiter Urlaub am Anfang des Tages mit" +
            "90 Minuten Abstand gebucht werden.")
    void test_16() throws IOException {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.mind90MinZwischenUrlauben(student, startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 12 bis 13 Uhr überschneidet sich mit Urlaub von 11 " +
            "bis 12:30.")
    void test_17() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_12_13), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_12_13);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 12 bis 13 Uhr überschneidet sich mit Urlaub " +
            "von 11:30 bis 12:30.")
    void test_18() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_12_13),
                startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_12_13);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlinelausur von 10 bis 11:30 Uhr überschneidet sich mit Urlaub von 11 " +
            "bis 12:30.")
    void test_19() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_10_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_10_1130),
                startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_10_1130);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 11 bis 12:30 Uhr überschneidet sich mit Urlaub " +
            "von 11:30 bis 12:30.")
    void test_20() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_11_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 11 bis 12:30 überschneidet sich mit Urlaub " +
            "von 10:30 bis 12:30.")
    void test_21() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_11_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 9:30 bis 12:30 überschneidet sich mit Urlaub " +
            "von 10:30 bis 11:30.")
    void test_22() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_930_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_930_1230);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 10:30 bis 11:30 überschneidet sich mit Urlaub " +
            "von 9:30 bis 12:30.")
    void test_23() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1030_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_1030_1130), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_1030_1130);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 11:30 bis 12:30 überschneidet sich nicht mit Urlaub " +
            "von 9:30 bis 10.")
    void test_24() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1130_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_1130_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_1130_1230);
    }

    @Test
    @DisplayName("Die Freistellungszeit einer Onlineklausur von 9:30 bis 11:30 überschneidet sich nicht mit Urlaub " +
            "von 12:30 bis 13.")
    void test_25() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_930_1130), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_930_1130);
    }

    @Test
    @DisplayName("240 Minuten Resturlaub reichen für eine Buchung von 30 Minuten Urlaub.")
    void test_26() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("240 Minuten Resturlaub reichen für eine Buchung von 240 Minuten Urlaub.")
    void test_27() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("240 Minuten Resturlaub reichen nicht für eine Buchung von 270 Minuten Urlaub.")
    void test_28() {
        
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 14, 0);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Samstage liegen nicht im Praktikumszeitraum.")
    void test_29() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 12, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 12, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Sonntage liegen nicht im Praktikumszeitraum.")
    void test_30() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 13, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 13, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Wochentage (z.B. Montag) liegen im Praktikumszeitraum, wenn sie zwischen Start- und Enddatum liegen.")
    void test_31() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 14, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 14, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Wochentage (z.B. Dienstag) liegen nicht im Praktikumszeitraum, wenn sie vor dem Startdatum liegen.")
    void test_32() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 1, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 1, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Wochentage (z.B. Mittwoch) liegen nicht im Praktikumszeitraum, wenn sie nach dem Enddatum liegen.")
    void test_33() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 30, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 30, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Das Startdatum (7.03.2022) liegt im Praktikumszeitraum.")
    void test_34() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 7, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 7, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Das Enddatum (25.03.2022) liegt im Praktikumszeitraum.")
    void test_35() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 25, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 25, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag zwischen Startzeit und Endzeit liegt im Praktikumszeitraum.")
    void test_36() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag aber mit Beginn vor dem Startzeitpunkt liegt nicht im " +
            "Praktikumszeitraum.")
    void test_37() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 8, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag aber mit Ende nach dem Endzeitpunkt liegt nicht im " +
            "Praktikumszeitraum.")
    void test_38() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 14, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag mit Beginn genau zum Startzeitpunkt liegt im Praktikumszeitraum.")
    void test_39() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag mit Ende genau zum Endzeitpunkt liegt im Praktikumszeitraum.")
    void test_40() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 13, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Buchung ab 12:00 liegt nach dem Zeitpunkt 10:00 am selben Tag.")
    void test_41() {
        LocalDateTime buchungsStart = LocalDateTime.of(2022, 3, 23, 12, 0);
        LocalDateTime zeitpunkt = LocalDateTime.of(2022, 3, 23, 10, 0);

        boolean b = buchungsValidierung.buchungLiegtNachZeitpunkt(buchungsStart, zeitpunkt);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Buchung ab 10:00 ist nicht nach dem Zeitpunkt 12:00 am selben Tag.")
    void test_42() {
        LocalDateTime buchungsStart = LocalDateTime.of(2022, 3, 23, 10, 0);
        LocalDateTime zeitpunkt = LocalDateTime.of(2022, 3, 23, 12, 0);

        boolean b = buchungsValidierung.buchungLiegtNachZeitpunkt(buchungsStart, zeitpunkt);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Buchung ab 10:00 ist nicht nach dem Zeitpunkt 10:00 am selben Tag.")
    void test_43() {
        LocalDateTime buchungsStart = LocalDateTime.of(2022, 3, 23, 10, 0);
        LocalDateTime zeitpunkt = LocalDateTime.of(2022, 3, 23, 10, 0);

        boolean b = buchungsValidierung.buchungLiegtNachZeitpunkt(buchungsStart, zeitpunkt);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Buchung ab 10:01 ist nach dem Zeitpunkt 10:00 am selben Tag.")
    void test_44() {
        LocalDateTime buchungsStart = LocalDateTime.of(2022, 3, 23, 10, 1);
        LocalDateTime zeitpunkt = LocalDateTime.of(2022, 3, 23, 10, 0);

        boolean b = buchungsValidierung.buchungLiegtNachZeitpunkt(buchungsStart, zeitpunkt);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Dauer von 0 Minuten ist kürzer als 15 Minuten :o")
    void test_45() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 23, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 23, 10, 0);

        boolean b = buchungsValidierung.dauerMindestens15Min(start,ende);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Dauer von 30 Minuten ist länger als 15 Minuten :o")
    void test_46() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 23, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.dauerMindestens15Min(start,ende);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Dauer von 15 Minuten ist >= 15 Minuten :o")
    void test_47() {
        LocalDateTime start = LocalDateTime.of(2022, 3, 23, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 23, 10, 15);

        boolean b = buchungsValidierung.dauerMindestens15Min(start,ende);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Klausur mit Freistellungszeit innerhalb der Praktikumszeit liegt komplett im " +
            "Praktikumszeitraum.")
    void test_48() {
        boolean b = buchungsValidierung.klausurLiegtImPraktikumsZeitraum(OK_11_12);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Klausur mit Freistellungsbeginn vor Beginn der Praktikumszeit und Freistellungsende danach " +
            "liegt im Praktikumszeitraum.")
    void test_49() {
        boolean b = buchungsValidierung.klausurLiegtImPraktikumsZeitraum(PK_10_11);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Klausur mit Freistellungsbeginn innerhalb der Praktikumszeit und Freistellungsende danach " +
            "liegt im Praktikumszeitraum.")
    void test_50() {
        boolean b = buchungsValidierung.klausurLiegtImPraktikumsZeitraum(PK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Klausur mit Freistellungszeit komplett außerhalb der Praktikumszeit liegt nicht im " +
            "Praktikumszeitraum.")
    void test_51() {
        Klausur klausur = new Klausur(2L, 222222L, "Mathe",
                LocalDateTime.of(2022, 3, 8, 14, 0),
                LocalDateTime.of(2022, 3, 8, 15, 0), "online");

        boolean b = buchungsValidierung.klausurLiegtImPraktikumsZeitraum(klausur);

        assertThat(b).isFalse();
    }
}
