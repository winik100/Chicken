package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.*;
import de.hhu.propra.chicken.util.UrlaubsEintragDTO;
import de.hhu.propra.chicken.web.controllers.StudentController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StudentControllerUnitTest {

    BuchungsService buchungsService = mock(BuchungsService.class);

    KlausurService klausurService = mock(KlausurService.class);

    StudentService studentService = mock(StudentService.class);

    Model model = mock(Model.class);

    OAuth2User principal = mock(OAuth2User.class);

    @AfterAll
    static void logLoeschen() {
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Falls noch kein Benutzer mit gegebenem Handle in der Datenbank existiert, wird ein neuer Student " +
            "angelegt und per studentService.studentHinzufügen gespeichert.")
    void test_1() throws Exception {
        when(studentService.findeStudentMitHandle(any())).thenReturn(null);
        when(principal.getAttribute("login")).thenReturn("testhandle");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.index(principal, model);

        verify(studentService, times(1)).studentHinzufuegen(any());
        verify(model, times(1)).addAttribute("klausuren", Collections.emptySet());
        verify(model, times(1)).addAttribute("urlaube", Collections.emptySet());
        verify(model, times(1)).addAttribute("urlaubssumme", 0L);
        verify(model, times(1)).addAttribute("resturlaub", 240L);
    }

    @Test
    @DisplayName("Falls schon ein Benutzer mit gegebenem Handle in der Datenbank existiert, wird dieser geladen " +
            "und das Model populiert.")
    void test_2() throws Exception {
        UrlaubsEintragDTO urlaubseintrag = new UrlaubsEintragDTO(
                LocalDateTime.of(2022, 3, 20, 10, 0),
                LocalDateTime.of(2022, 3, 20, 12, 0));
        Student student = new Student(1L, "testhandle", 120L,
                Set.of(urlaubseintrag), Collections.emptySet());
        when(principal.getAttribute("login")).thenReturn("testhandle");
        when(studentService.findeStudentMitHandle("testhandle")).thenReturn(student);
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.index(principal, model);

        verify(studentService, times(0)).studentHinzufuegen(any());
        verify(model, times(1)).addAttribute("klausuren", Collections.emptySet());
        verify(model, times(1)).addAttribute("urlaube", Set.of(urlaubseintrag));
        verify(model, times(1)).addAttribute("urlaubssumme", 120L);
        verify(model, times(1)).addAttribute("resturlaub", 120L);
    }
    @Test
    @DisplayName("Die Seite für die Klausuranmeldung listet alle Klausuren in der Datenbank durch Aufruf " +
            "von alleKlausuren() im Klausurservice.")
    void test_3() {
        when(principal.getAttribute("login")).thenReturn("testhandle");
        when(klausurService.alleKlausuren()).thenReturn(Set.of(PK_10_11, PK_12_13, OK_930_1230, OK_11_12));
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausuranmeldung(model);

        verify(klausurService, times(1)).alleKlausuren();
        verify(model, times(1)).addAttribute("klausuren", Set.of(PK_10_11, PK_12_13, OK_930_1230, OK_11_12));
    }

    @Test
    @DisplayName("Bei Aufruf von klausurAnmeldungDurchfuehren() wird der Buchungsservice mit korrekten Parametern aufgerufen.")
    void test_4() throws IOException {
        when(principal.getAttribute("login")).thenReturn("testhandle");
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle("testhandle")).thenReturn(student);
        when(klausurService.findeKlausurMitLsfId(Long.valueOf("111111"))).thenReturn(PK_12_13);
        when(buchungsService.klausurBuchen(any(), any())).thenReturn("");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausurAnmeldungDurchfuehren(model, "111111", principal);

        verify(buchungsService, times(1)).klausurBuchen(PK_12_13, student);
        verify(model, times(1)).addAttribute("klausuranmeldungserror", "");
    }

    @Test
    @DisplayName("Tritt bei der Klausuranmeldung ein Fehler auf, werden dieser ins Model geschrieben, die gleiche " +
            "Seite wiedergegeben und erneut alle Klausuren gelistet.")
    void test_5() throws IOException {
        when(principal.getAttribute("login")).thenReturn("testhandle");
        Student student = mock(Student.class);
        when(studentService.findeStudentMitHandle("testhandle")).thenReturn(student);
        when(klausurService.findeKlausurMitLsfId(Long.valueOf("111111"))).thenReturn(PK_12_13);
        when(klausurService.alleKlausuren()).thenReturn(Set.of(PK_10_11, PK_12_13, OK_930_1230, OK_11_12));
        when(buchungsService.klausurBuchen(any(), any())).thenReturn("testerror");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausurAnmeldungDurchfuehren(model, "111111", principal);

        verify(buchungsService, times(1)).klausurBuchen(PK_12_13, student);
        verify(model, times(1)).addAttribute("klausuranmeldungserror", "testerror");
        verify(model, times(1)).addAttribute("klausuren", Set.of(PK_10_11, PK_12_13, OK_930_1230, OK_11_12));
    }

    @Test
    @DisplayName("klausurRegistrierungDurchfuehren ruft korrekt den Klausurservice auf.")
    void test_6() throws IOException {
        Klausur klausur = new Klausur(111111L, "testklausur",
                LocalDateTime.of(2022, 3, 20, 10, 30),
                LocalDateTime.of(2022, 3, 20, 11, 30),
                "praesenz");
        when(klausurService.findeKlausurMitLsfId(any())).thenReturn(klausur);
        when(klausurService.klausurHinzufuegen(any())).thenReturn("");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausurregistrierungDurchfuehren(model, "testklausur","111111", "true",
                "2022-03-20", "10:30", "11:30");

        verify(klausurService, times(1)).klausurHinzufuegen(klausur);
        verify(model, times(1)).addAttribute("klausurregistrierungserror", "");
    }

    @Test
    @DisplayName("klausurRegistrierungDurchfuehren fügt im Fehlerfall eine Fehlernachricht dem Model hinzu.")
    void test_7() throws IOException {
        Klausur klausur = new Klausur(111111L, "testklausur",
                LocalDateTime.of(2022, 3, 20, 10, 30),
                LocalDateTime.of(2022, 3, 20, 11, 30),
                "online");
        when(klausurService.findeKlausurMitLsfId(any())).thenReturn(klausur);
        when(klausurService.klausurHinzufuegen(any())).thenReturn("testerror");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausurregistrierungDurchfuehren(model, "testklausur","111111", "false",
                "2022-03-20", "10:30", "11:30");

        verify(model, times(1)).addAttribute("klausurregistrierungserror", "testerror");
    }

    @Test
    @DisplayName("klausurstornierung() ruft korrekt den Buchungsservice auf.")
    void test_8() throws IOException {
        Student student = mock(Student.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        Klausur klausur = new Klausur(111111L, "testklausur",
                LocalDateTime.of(2022, 3, 20, 10, 30),
                LocalDateTime.of(2022, 3, 20, 11, 30),
                "online");
        when(principal.getAttribute("login")).thenReturn("testhandle");
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(klausurService.findeKlausurMitLsfId(any())).thenReturn(klausur);
        when(buchungsService.klausurStornieren(any(), any())).thenReturn("testerror");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.klausurstornierung(principal, "111111", redirectAttributes);

        verify(buchungsService, times(1)).klausurStornieren(klausur, student);
        verify(redirectAttributes, times(1)).addFlashAttribute("klausurstornoerror", "testerror");
    }

    @Test
    @DisplayName("Bei Aufruf von urlaubsbuchungDurchfuehren() wird der Buchungsservice mit korrekten Parametern aufgerufen.")
    void test_9() throws IOException {
        Student student = mock(Student.class);
        when(principal.getAttribute("login")).thenReturn("testhandle");
        when(studentService.findeStudentMitHandle("testhandle")).thenReturn(student);
        when(buchungsService.urlaubBuchen(any(), any(), any())).thenReturn("testerror");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.urlaubsbuchungDurchfuehren(model, principal, "2022-03-20", "10:30", "11:30");

        verify(buchungsService, times(1)).urlaubBuchen(student,
                LocalDateTime.of(2022, 3, 20, 10, 30),
                LocalDateTime.of(2022, 3, 20, 11, 30));
        verify(model, times(1)).addAttribute("urlaubbuchungserror", "testerror");
    }

    @Test
    @DisplayName("urlaubsstornierung() ruft korrekt den Buchungsservice auf.")
    void test_10() throws IOException {
        Student student = mock(Student.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(principal.getAttribute("login")).thenReturn("testhandle");
        when(studentService.findeStudentMitHandle(any())).thenReturn(student);
        when(buchungsService.urlaubStornieren(any(), any(), any())).thenReturn("testerror");
        StudentController studentController = new StudentController(buchungsService, studentService, klausurService);

        studentController.urlaubsstornierung(principal, "2022-03-20", "10:30", "11:30", redirectAttributes);

        verify(buchungsService, times(1)).urlaubStornieren(student,
                LocalDateTime.of(2022, 3, 20, 10, 30),
                LocalDateTime.of(2022, 3, 20, 11, 30));
        verify(redirectAttributes, times(1)).addFlashAttribute("urlaubsstornoerror", "testerror");
    }


}
