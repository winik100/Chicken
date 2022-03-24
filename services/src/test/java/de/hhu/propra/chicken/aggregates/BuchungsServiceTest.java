package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;


import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BuchungsServiceTest {

    @AfterAll
    static void logLoeschen(){
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Buchungsservice.klausurBuchen fügt Klausuranmeldung zu Student hinzu")
    void test_1() throws IOException {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).klausurAnmelden(PK_12_13);
    }

    @Test
    @DisplayName("Buchungsservice.klausurStornieren entfernt Klausuranmeldung von Student")
    void test_2() throws IOException {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurStornieren(PK_12_13, student);

        verify(student, times(1)).klausurAbmelden(PK_12_13);
        verify(studentRepo, times(1)).save(student);
    }

    @Test
    @DisplayName("Stornierung der Klausur später als am Vortag kann nicht durchgeführt werden.")
    void test_2b() throws IOException {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(false);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurStornieren(PK_12_13, student);

        verify(student, never()).klausurAbmelden(PK_12_13);
    }

    @Test
    @DisplayName("BuchungsService.urlaubStornieren() ruft Student.urlaubEntfernen() korrekt auf.")
    void test_4() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubStornieren(student, start, ende);

        verify(student, times(1)).urlaubEntfernen(start, ende);
    }

    @Test
    @DisplayName("BuchungsService.urlaubStornieren() ruft Student.urlaubEntfernen() nicht auf, falls weniger als ein Tag bis Urlaubsbeginn verbleibt.")
    void test_4b() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(false);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String result = buchungsService.urlaubStornieren(student, start, ende);

        verify(student, never()).urlaubEntfernen(start, ende);
        assertThat(result).isEqualTo("Urlaub kann nur bis zum Vortag storniert werden.");
    }

    @Test
    @DisplayName("Urlaub beginnt vor Freistellungszeitraum und endet innerhalb -> Urlaubsende wird auf Freistellungsbeginn gesetzt")
    void test_5(){
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startFreistellung = OK_1130_1230.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_1130_1230.endeFreistellungBerechnen();

        LocalDateTime neuesEnde = BuchungsService.neuesUrlaubsEndeBerechnen(startUrlaub, endeUrlaub, startFreistellung, endeFreistellung);

        assertThat(neuesEnde).isEqualTo(startFreistellung);
    }

    @Test
    @DisplayName("Urlaub beginnt im Freistellungszeitraum und endet nachher -> Urlaubsstart wird auf Freistellungsende gesetzt")
    void test_6(){
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startFreistellung = OK_1130_1230.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_1130_1230.endeFreistellungBerechnen();

        LocalDateTime neuerAnfang = BuchungsService.neuenUrlaubsStartBerechnen(startUrlaub, endeUrlaub, startFreistellung, endeFreistellung);

        assertThat(neuerAnfang).isEqualTo(endeFreistellung);
    }

    @Test
    @DisplayName("Wenn keine Konflikte mit Freistellungszeiträumen exisitieren, wird einfach student.urlaubNehmen mit " +
            "unverändertem Start- und Endzeitpunkt aufgerufen")
    void test_7() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(OK_12_13);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);


        buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, bereits gebuchter Urlaub, aber 90 Min dazwischen " +
            "-> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_8() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(student.hatUrlaubAm(any())).thenReturn(true);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.mind90MinZwischenUrlauben(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(student, startErsterUrlaub, endeErsterUrlaub);
        buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, kein bereits gebuchter Urlaub, weniger als 150 Min Urlaub " +
            "-> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_9() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 09:30 bis 11:30, bei Onlineklausur von 11:00 bis 12:00, wird zu Urlaub von 09:30 bis 10:30")
    void test_10() throws IOException{
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of(OK_11_12));
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);
    }


    @Test
    @DisplayName("KlausurBuchen ohne Überschneidung ruft einfach student.klausurAnmelden() auf")
    void test_11() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).klausurAnmelden(PK_12_13);
    }

    @Test
    @DisplayName("KlausurBuchen mit Überschneidung ruft student.bestehendenUrlaubAnKlausurAnpassen und danach klausurAnmelden auf")
    void test_12() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = mock(Student.class);
        when(student.ueberschneidungKlausurMitBestehendemUrlaub(any())).thenReturn(true);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).bestehendenUrlaubAnKlausurAnpassen(PK_12_13);
        verify(student, times(1)).klausurAnmelden(PK_12_13);
    }

    @Test
    @DisplayName("urlaubBuchen mit zu kurzer Dauer (<15 Minuten) ruft nicht urlaubNehmen auf.")
    void test_13() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 10);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String result = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, never()).urlaubNehmen(startUrlaub, endeUrlaub);
        assertThat(result).isEqualTo("Die Urlaubsdauer muss mindestens 15 Minuten betragen.");
    }


}
