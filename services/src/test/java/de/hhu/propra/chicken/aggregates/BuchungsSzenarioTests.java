package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;


import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BuchungsSzenarioTests {

    @Test
    @DisplayName("Michaela will einen kompletten Tag frei machen. Das geht, wenn Sie sonst keinen Urlaub genommen hat.")
    void test_1(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student michaela = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(michaela);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        buchungsService.urlaubBuchen(10L, urlaubsStart, urlaubsEnde);

        verify(michaela, times(1)).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }

    @Test
    @DisplayName("Gustav möchte an einem Tag mittags Feierabend machen. Er kann einen Urlaub von 12:00 Uhr bis zum Praktikumsende einreichen.")
    void test_2(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student gustav = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(gustav);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

        buchungsService.urlaubBuchen(10L, urlaubsStart, urlaubsEnde);

        verify(gustav, times(1)).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }

    @Test
    @DisplayName("Otto schreibt an einem Tag eine Onlineklausur und ist dafür von 10:30 bis 12:00 Uhr freigestellt. Er kann einen Urlaub vor und nach der Klausur nehmen, also zum Beispiel von 10:00 Uhr bis 10:30 und dann von 12:00 bis 12:30 Uhr.")
    void test_3() throws IOException {
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student otto = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(otto);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        when(klausurRepo.klausurMitLsfId(any())).thenReturn(OK_11_12);
        LocalDateTime ersterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime erstesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime zweiterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime zweitesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

        buchungsService.klausurBuchen(OK_11_12.getLsfId(), 10L);
        buchungsService.urlaubBuchen(10L, ersterUrlaubsStart, erstesUrlaubsEnde);
        buchungsService.urlaubBuchen(10L, zweiterUrlaubsStart, zweitesUrlaubsEnde);

        verify(otto, times(1)).urlaubNehmen(ersterUrlaubsStart, erstesUrlaubsEnde);
        verify(otto, times(1)).urlaubNehmen(zweiterUrlaubsStart, zweitesUrlaubsEnde);
    }

    @Test
    @DisplayName("Petra möchte von 10:00 Uhr bis 10:30 Uhr und von 11:30 bis 12:00 freinehmen. Das geht leider nicht, da die beiden Blöcke am Anfang und Ende des Tages liegen müssen.")
    void test_4(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student petra = new Student(10L, "ibimspetra");
        when(studentRepo.studentMitId(anyLong())).thenReturn(petra);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime ersterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime erstesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime zweiterUrlaubsStart = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime zweitesUrlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 0);

        buchungsService.urlaubBuchen(10L, ersterUrlaubsStart, erstesUrlaubsEnde);
        buchungsService.urlaubBuchen(10L, zweiterUrlaubsStart, zweitesUrlaubsEnde);

        assertThat(petra.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Fritz möchte an einem Tag drei Stunden freinehmen. Das geht leider nicht, da er entweder komplett frei machen oder mindestens 90 Minuten im Praktikum sein muss.")
    void test_5(){
        StudentRepository studentRepo = mock(StudentRepository.class);
        Student fritz = mock(Student.class);
        when(studentRepo.studentMitId(anyLong())).thenReturn(fritz);
        KlausurRepository klausurRepo = mock(KlausurRepository.class);
        BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
        BuchungsService buchungsService = new BuchungsService(studentRepo, klausurRepo, buchungsValidierung);
        LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 0);

        buchungsService.urlaubBuchen(10L, urlaubsStart, urlaubsEnde);

        verify(fritz, times(0)).urlaubNehmen(urlaubsStart, urlaubsEnde);
    }
}
