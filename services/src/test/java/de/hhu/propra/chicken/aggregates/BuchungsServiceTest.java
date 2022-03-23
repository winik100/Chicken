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
import static org.mockito.ArgumentMatchers.anyLong;
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
        Student student = new Student(10L, "ibimsgithub");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.gueltigeLsfId(any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.klausurBuchen(PK_12_13.getLsfId(), 10L);

        assertThat(student.getKlausurAnmeldungen()).contains(PK_12_13.getId());
    }

    @Test
    @DisplayName("Buchungsservice.klausurStornieren entfernt Klausuranmeldung von Student")
    void test_2() throws IOException {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        Student student = new Student(10L, "ibimsgithub");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.gueltigeLsfId(any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        buchungsService.klausurBuchen(PK_12_13.getLsfId(), 10L);

        buchungsService.klausurStornieren(PK_12_13.getLsfId(), 10L);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(PK_12_13.getId());
    }

    @Test
    @DisplayName("BuchungsService.urlaubStornieren() ruft Student.urlaubEntfernen() korrekt auf")
    void test_4() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = new Student(10L, "ibimsgithub");
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        buchungsService.urlaubBuchen(student.getId(), start, ende);

        buchungsService.urlaubStornieren(student.getId(), start, ende);

        assertThat(student.getUrlaube()).isEmpty();
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
//        Student student = new Student(10L, "ibimsgithub");
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(OK_12_13);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.gueltigeLsfId(any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        buchungsService.klausurBuchen(OK_12_13.getLsfId(), 10L);

        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

        //assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, bereits gebuchter Urlaub, aber 90 Min dazwischen " +
            "-> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_8() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(student.hatUrlaubAm(any())).thenReturn(true);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.mind90MinZwischenUrlauben(any(), any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(10L, startErsterUrlaub, endeErsterUrlaub);
        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

//        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, kein bereits gebuchter Urlaub, weniger als 150 Min Urlaub " +
            "-> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_9() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 09:30 bis 11:30, bei Onlineklausur von 11:00 bis 12:00, wird zu Urlaub von 09:30 bis 10:30")
    void test_10() throws IOException{
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.gueltigeLsfId(any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of(OK_11_12));
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        buchungsService.urlaubBuchen(student.getId(), startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);
    }
}
