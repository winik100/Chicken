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

public class BuchungsSzenarioTests {





    @AfterAll
    static void logLoeschen(){
        File file = new File("auditlog.txt");
        file.delete();
    }


    @Test
    @DisplayName("Michaela will einen kompletten Tag frei machen. Das geht, wenn Sie sonst keinen Urlaub genommen hat.")
    void test_1() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student michaela = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(michaela);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        buchungsService.urlaubBuchen(michaela, urlaubsStart, urlaubsEnde);

        verify(michaela, times(1)).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }

    @Test
    @DisplayName("Gustav möchte an einem Tag mittags Feierabend machen. Er kann einen Urlaub von 12:00 Uhr bis zum Praktikumsende einreichen.")
    void test_2() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student gustav = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(gustav);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        buchungsService.urlaubBuchen(gustav, urlaubsStart, urlaubsEnde);

        verify(gustav, times(1)).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }

    @Test
    @DisplayName("Otto schreibt an einem Tag eine Onlineklausur und ist dafür von 10:30 bis 12:00 Uhr freigestellt. " +
            "Er kann einen Urlaub vor und nach der Klausur nehmen, also zum Beispiel von 10:00 Uhr bis 10:30 und dann " +
            "von 12:00 bis 12:30 Uhr.")
    void test_3() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student otto = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(otto);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(true);
        when(buchungsValidierung.ueberschneidungMitKlausur(any(), any(), any())).thenReturn(Set.of());
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(OK_11_12);
        LocalDateTime ersterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime erstesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime zweiterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime zweitesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

        buchungsService.klausurBuchen(OK_11_12, otto);
        buchungsService.urlaubBuchen(otto, ersterUrlaubsStart, erstesUrlaubsEnde);
        buchungsService.urlaubBuchen(otto, zweiterUrlaubsStart, zweitesUrlaubsEnde);

        verify(otto, times(1)).urlaubNehmen(ersterUrlaubsStart, erstesUrlaubsEnde);
        verify(otto, times(1)).urlaubNehmen(zweiterUrlaubsStart, zweitesUrlaubsEnde);
    }

    @Test
    @DisplayName("Petra möchte von 10:00 Uhr bis 10:30 Uhr und von 11:30 bis 12:00 freinehmen. Das geht leider nicht, da die beiden Blöcke am Anfang und Ende des Tages liegen müssen.")
    void test_4() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student petra = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(petra);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.hatAusreichendRestUrlaub(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.klausurAmGleichenTag(any(), any())).thenReturn(false);
        when(buchungsValidierung.mind90MinZwischenUrlauben(any(), any(), any())).thenReturn(false);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(true);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime ersterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime erstesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime zweiterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime zweitesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 0);
        when(petra.hatUrlaubAm(any())).thenReturn(false).thenReturn(true);
        when(petra.ueberschneidungMitBestehendemUrlaub(any(), any())).thenReturn(false);

        buchungsService.urlaubBuchen(petra, ersterUrlaubsStart, erstesUrlaubsEnde);
        buchungsService.urlaubBuchen(petra, zweiterUrlaubsStart, zweitesUrlaubsEnde);

        verify(petra, times(1)).urlaubNehmen(ersterUrlaubsStart, erstesUrlaubsEnde);
        verify(petra, never()).urlaubNehmen(zweiterUrlaubsStart, zweitesUrlaubsEnde);
    }

    @Test
    @DisplayName("Fritz möchte an einem Tag drei Stunden freinehmen. Das geht leider nicht, da er entweder komplett frei machen oder mindestens 90 Minuten im Praktikum sein muss.")
    void test_5() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student fritz = mock(Student.class);
        when(studentRepo.studentMitGitHubHandle(any())).thenReturn(fritz);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = mock(BuchungsValidierung.class);
        when(buchungsValidierung.dauerIstVielfachesVon15(any(), any())).thenReturn(true);
        when(buchungsValidierung.startZeitIstVielfachesVon15(any())).thenReturn(true);
        when(buchungsValidierung.mind90MinZwischenUrlauben(any(), any(), any())).thenReturn(true);
        when(buchungsValidierung.blockEntwederGanzerTagOderMax150Min(any(), any())).thenReturn(false);
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 0);

        buchungsService.urlaubBuchen(fritz, urlaubsStart, urlaubsEnde);

        verify(fritz, never()).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }
}
