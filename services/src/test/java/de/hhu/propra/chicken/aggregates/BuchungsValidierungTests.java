package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;


import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class BuchungsValidierungTests {

    private final static String STARTZEIT = "09:30";
    private final static String ENDZEIT = "13:30";
    private final static String STARTTAG = "2022-03-07";
    private final static String ENDTAG = "2022-03-25";
    
    @Test
    @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 60 min true zurück")
    void test_1() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 66 min false zurück")
    void test_2() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 6);

        boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist die Startzeit ein Vielfaches von 15 (z.B. 12:00) wird true zurückgegeben")
    void test_3() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Ist die Startzeit kein Vielfaches von 15 (z.B. 12:10) wird false zurückgegeben")
    void test_4() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 10);

        boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Klausur am gleichen Tag wird erkannt (unabhängig von Uhrzeit)")
    void test_5() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        boolean b = buchungsValidierung.klausurAmGleichenTag(Set.of(PK_12_13), urlaubsStart);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Klausur am anderem Tag spielt keine Rolle")
    void test_6() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 9, 13, 10);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        boolean b = buchungsValidierung.klausurAmGleichenTag(Set.of(PK_12_13), urlaubsStart);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ganzer Tag Urlaub ist erlaubt")
    void test_7() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub unter 150 Min ist erlaubt")
    void test_8() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub >150 Min, aber <240 Min ist nicht erlaubt")
    void test_9() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

        boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Ist schon ein Urlaub in der Mitte des Tages gebucht, schlägt die zweite Buchung fehl")
    void test_10() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_11() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_12() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_13() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_14() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_15() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
    void test_16() throws IOException {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
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
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_12_13), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_12_13);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 12 bis 13 Uhr")
    void test_18() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_12_13), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_12_13);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 10 bis 11:30 Uhr")
    void test_19() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_10_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_10_1130), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_10_1130);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 12:30 überschneidet sich mit der Klausurfreistellung einer online Klausur von 11 bis 12:30 Uhr")
    void test_20() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_11_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub ist identisch mit dem Freistellungszeitraum")
    void test_21() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_11_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_11_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett im Freistellungszeitraum")
    void test_22() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_930_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_930_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub umfasst den Freistellungszeitraum komplett")
    void test_23() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1030_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_1030_1130), startUrlaub, endeUrlaub);

        assertThat(klausuren).contains(OK_1030_1130);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt vor dem Freistellungszeitraum")
    void test_24() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1130_1230);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_1130_1230), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_1130_1230);
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt nach dem Freistellungszeitraum")
    void test_25() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_930_1130);

        Set<Klausur> klausuren = buchungsValidierung.ueberschneidungMitKlausur(Set.of(OK_930_1130), startUrlaub, endeUrlaub);

        assertThat(klausuren).doesNotContain(OK_930_1130);
    }

    @Test
    @DisplayName("Student mit 240 Minuten Resturlaub hat ausreichend Resturlaub für 30 Minuten Urlaub")
    void test_26() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Student mit 240 Minuten Resturlaub hat ausreichend Resturlaub für 240 Minuten Urlaub")
    void test_27() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Student mit 240 Minuten Resturlaub hat nicht ausreichend Resturlaub für 270 Minuten Urlaub")
    void test_28() {
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 14, 0);
        Student student = new Student(10L, "ibimsgithub");

        boolean b = buchungsValidierung.hatAusreichendRestUrlaub(student, startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Samstage liegen nicht im Praktikumszeitraum.")
    void test_29(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 12, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 12, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Sonntage liegen nicht im Praktikumszeitraum.")
    void test_30(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 13, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 13, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Wochentage (z.B. Montag) liegen im Praktikumszeitraum, wenn sie zwischen Start- und Enddatum liegen.")
    void test_31(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 14, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 14, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Wochentage (z.B. Dienstag) liegen nicht im Praktikumszeitraum, wenn sie vor dem Startdatum liegen.")
    void test_32(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 1, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 1, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Wochentage (z.B. Mittwoch) liegen nicht im Praktikumszeitraum, wenn sie nach dem Enddatum liegen.")
    void test_33(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 30, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 30, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Das Startdatum (7.03.2022) liegt im Praktikumszeitraum.")
    void test_34(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 7, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 7, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Das Enddatum (25.03.2022) liegt im Praktikumszeitraum.")
    void test_35(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 25, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 25, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag zwischen Startzeit und Endzeit liegt im Praktikumszeitraum.")
    void test_36(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag aber mit Beginn vor dem Startzeitpunkt liegt nicht im Praktikumszeitraum.")
    void test_37(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 8, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag aber mit Ende nach dem Endzeitpunkt liegt nicht im Praktikumszeitraum.")
    void test_38(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 14, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag mit Beginn genau zum Startzeitpunkt liegt im Praktikumszeitraum.")
    void test_39(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 10, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Urlaub an einem gültigen Tag mit Ende genau zum Endzeitpunkt liegt im Praktikumszeitraum.")
    void test_40(){
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung(STARTZEIT, ENDZEIT, STARTTAG, ENDTAG);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 23, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 23, 13, 30);

        boolean b = buchungsValidierung.liegtImPraktikumsZeitraum(startUrlaub, endeUrlaub);

        assertThat(b).isTrue();
    }
}
