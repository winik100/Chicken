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

    private final Student student = mock(Student.class);
    private final StudentRepository studentRepo = mock(StudentRepository.class);
    private final KlausurRepository klausurRepo = mock(KlausurRepository.class);
    private final BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);

    @AfterAll
    static void logLoeschen() {
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Das Buchen einer Klausur fügt dem Studenten eine Klausuranmeldung hinzu.")
    void test_1() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String error = buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).klausurAnmelden(PK_12_13);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Das Stornieren einer Klausur entfernt die Klausuranmeldung des Studenten.")
    void test_2() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String error = buchungsService.klausurStornieren(PK_12_13, student);

        verify(student, times(1)).klausurAbmelden(PK_12_13);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Die Stornierung einer Klausur weniger als einen Tag vor Freistellungsbeginn kann nicht " +
            "durchgeführt werden.")
    void test_3() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(false);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String error = buchungsService.klausurStornieren(PK_12_13, student);

        verify(student, never()).klausurAbmelden(PK_12_13);
        assertThat(error).isEqualTo("Klausur kann nur bis zum Vortag storniert werden.");
    }

    @Test
    @DisplayName("Die Stornierung eines Urlaubs entfernt den entsprechenden Urlaubseintrag des Studenten.")
    void test_4() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

        String error = buchungsService.urlaubStornieren(student, start, ende);

        verify(student, times(1)).urlaubEntfernen(start, ende);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Die Stornierung eines Urlaubs weniger als einen Tag vor Urlaubsbeginn kann nicht " +
            "durchgeführt werden.")
    void test_5() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(false);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

        String error = buchungsService.urlaubStornieren(student, start, ende);

        verify(student, never()).urlaubEntfernen(start, ende);
        assertThat(error).isEqualTo("Urlaub kann nur bis zum Vortag storniert werden.");
    }

    @Test
    @DisplayName("Beginnt der geplante Urlaub vor Freistellungszeitraum und endet innerhalb, wird das Urlaubsende " +
            "auf Freistellungsbeginn gesetzt.")
    void test_6() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startFreistellung = OK_1130_1230.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_1130_1230.endeFreistellungBerechnen();

        LocalDateTime neuesUrlaubsEnde = BuchungsService.neuesUrlaubsEndeBerechnen(startUrlaub, endeUrlaub,
                startFreistellung, endeFreistellung);

        assertThat(neuesUrlaubsEnde).isEqualTo(startFreistellung);
    }

    @Test
    @DisplayName("Beginnt der geplante Urlaub vor Freistellungszeitraum und endet exakt zum Freistellungsbeginn, wird " +
            "das Urlaubsende auf Freistellungsbeginn gesetzt.")
    void test_7() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime startFreistellung = OK_1130_1230.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_1130_1230.endeFreistellungBerechnen();

        LocalDateTime neuesUrlaubsEnde = BuchungsService.neuesUrlaubsEndeBerechnen(startUrlaub, endeUrlaub,
                startFreistellung, endeFreistellung);

        assertThat(neuesUrlaubsEnde).isEqualTo(startFreistellung);
    }

    @Test
    @DisplayName("Beginnt der geplante Urlaub im Freistellungszeitraum und endet danach, wird der Urlaubsstart " +
            "auf Freistellungsende gesetzt.")
    void test_8() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startFreistellung = OK_1130_1230.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_1130_1230.endeFreistellungBerechnen();

        LocalDateTime neuerUrlaubsStart = BuchungsService.neuenUrlaubsStartBerechnen(startUrlaub, endeUrlaub, startFreistellung, endeFreistellung);

        assertThat(neuerUrlaubsStart).isEqualTo(endeFreistellung);
    }

    @Test
    @DisplayName("Wenn keine Konflikte mit Freistellungszeiträumen exisitieren, wird der geplante Urlaub " +
            "unverändert genommen.")
    void test_9() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(OK_12_13);
        
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Der Student hat keine Klausur am gleichen Tag, aber bereits gebuchten Urlaub. Liegen 90 Minuten " +
            "zwischen dem bestehendem und dem geplanten Urlaub, wird dieser unverändert genommen.")
    void test_10() throws IOException {
        when(student.hatUrlaubAm(any())).thenReturn(true);
        
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.mind90MinZwischenUrlauben(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        buchungsService.urlaubBuchen(student, startUrlaub1, endeUrlaub1);

        String error = buchungsService.urlaubBuchen(student, startUrlaub2, endeUrlaub2);

        verify(student, times(1)).urlaubNehmen(startUrlaub2, endeUrlaub2);
        verify(studentRepo, times(2)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Der Student hat keine Klausur am gleichen Tag und keinen bereits gebuchten Urlaub. Ist die Dauer " +
            "des geplanten Urlaubs weniger als 150 Minuten, wird dieser unverändert genommen.")
    void test_11() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1)).urlaubNehmen(startUrlaub, endeUrlaub);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Hat der Student eine Onlineklausur von 11 bis 12, wird geplanter Urlaub von 09:30 bis 11:30 " +
            "stattdessen von 09:30 bis 10:30 genommen.")
    void test_12() throws IOException{
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);

        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of(OK_11_12));

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, times(1))
                .urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Der Student kann eine Klausur anmelden, wenn sie sich mit keiner bereits angemeldeten überschneidet.")
    void test_13() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String error = buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).klausurAnmelden(PK_12_13);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Meldet sich der Student für eine Klausur an, die sich mit besthendem Urlaub überschneidet, wird " +
            "dieser so angepasst, dass es keine Überschneidung mehr gibt, und die Klausur wird angemeldet.")
    void test_14() throws IOException {
        when(student.ueberschneidungKlausurMitBestehendemUrlaub(any())).thenReturn(true);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(PK_12_13);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);

        String error = buchungsService.klausurBuchen(PK_12_13, student);

        verify(student, times(1)).bestehendenUrlaubAnKlausurAnpassen(PK_12_13);
        verify(student, times(1)).klausurAnmelden(PK_12_13);
        verify(studentRepo, times(1)).save(student);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Ist die Dauer des geplanten Urlaubs kürzer als 15 Minuten, wird dieser nicht genommen.")
    void test_15() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);
        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 10);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, never()).urlaubNehmen(startUrlaub, endeUrlaub);
        assertThat(error).isEqualTo("Die Urlaubsdauer muss mindestens 15 Minuten betragen.");
    }

    @Test
    @DisplayName("Beginnt der geplante Urlaub genau zu Freistellungsbeginn und endet innerhalb der Freistellungszeit, " +
            "wird er nicht genommen.")
    void test_16() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);

        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of(OK_11_12));

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 45);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, never()).urlaubNehmen(startUrlaub, endeUrlaub);
        assertThat(error).isEqualTo("");
    }

    @Test
    @DisplayName("Liegt der geplante Urlaub komplett innerhalb der Freistellungszeit, wird er nicht genommen.")
    void test_17() throws IOException {
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(student);

        when(buchungsValidierung.buchungLiegtNachZeitpunkt(any(), any())).thenReturn(true);
        when(buchungsValidierung.liegtImPraktikumsZeitraum(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerMindestens15Min(any(), any())).thenReturn(true);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of(OK_11_12));

        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 45);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 45);

        String error = buchungsService.urlaubBuchen(student, startUrlaub, endeUrlaub);

        verify(student, never()).urlaubNehmen(startUrlaub, endeUrlaub);
        assertThat(error).isEqualTo("");
    }
}
