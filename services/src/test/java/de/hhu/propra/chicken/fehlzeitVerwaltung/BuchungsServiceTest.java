package de.hhu.propra.chicken.fehlzeitVerwaltung;

import de.hhu.propra.chicken.aggregates.klausur.Klausur;
import de.hhu.propra.chicken.aggregates.student.Student;
import de.hhu.propra.chicken.repositories.KlausurRepository;
import de.hhu.propra.chicken.repositories.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static templates.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BuchungsServiceTest {

    @Test
    @DisplayName("Buchungsservice.klausurBuchen fügt Klausuranmeldung zu Student hinzu")
    void test_1() {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        StudentRepository studentRepo = mock(StudentRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(anyInt())).thenReturn(klausur);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.klausurBuchen(234567, 10L);

        assertThat(student.getKlausurAnmeldungen()).contains(klausur);
    }

    @Test
    @DisplayName("Buchungsservice.klausurStornieren entfernt Klausuranmeldung von Student")
    void test_2() {
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = new Student(10L, "ibimsgithub");
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(anyInt())).thenReturn(PK_12_13);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);
        buchungsService.klausurBuchen(PK_12_13.getLsfId(), 10L);

        buchungsService.klausurStornieren(PK_12_13.getLsfId(), 10L);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(PK_12_13);
    }

    //TODO: nutzloser Test, da Use-Case-Tests weiter unten
    @Test
    @DisplayName("BuchungsService.urlaubBuchen() ruft Student.urlaubNehmen() korrekt auf")
    void test_3() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.urlaubBuchen(student.getId(), start, ende);

        verify(student, times(1)).urlaubNehmen(start, ende);
    }

    @Test
    @DisplayName("BuchungsService.urlaubStornieren() ruft Student.urlaubEntfernen() korrekt auf")
    void test_4() {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);
        buchungsService.urlaubBuchen(student.getId(), start, ende);

        buchungsService.urlaubStornieren(student.getId(), start, ende);

        verify(student, times(1)).urlaubEntfernen(start, ende);
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
    @DisplayName("Wenn keine Konflikte mit Freistellungszeiträumen exisitieren, wird einfach student.urlaubNehmen mit unverändertem Start- und Endzeitpunkt aufgerufen")
    void test_7(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.klausurBuchen(OK_12_13.getLsfId(), 10L);
        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, bereits gebuchter Urlaub, aber 90 Min dazwischen -> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_8(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        LocalDateTime startErsterUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeErsterUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.urlaubBuchen(10L, startErsterUrlaub, endeErsterUrlaub);
        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }

    @Test
    @DisplayName("Keine Klausur am gleichen Tag, kein bereits gebuchter Urlaub, weniger als 150 Min Urlaub -> student.urlaubNehmen() mit unveränderten Argumenten")
    void test_9(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student student = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(student);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo);

        buchungsService.urlaubBuchen(10L, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
    }
}
