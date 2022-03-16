package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.UrlaubsEintrag;
import de.hhu.propra.chicken.util.KlausurReferenz;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTests {

    @Test
    @DisplayName("urlaubNehmen() reduziert den Resturlaub des Studenten korrekt")
    void test_1() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 15);
        student.urlaubNehmen(start, ende);
        assertThat(student.getResturlaubInMin()).isEqualTo(225L);
    }

    @Test
    @DisplayName("urlaubEntfernen() erhöht den Resturlaub des Studenten korrekt")
    void test_2() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 30);
        student.urlaubNehmen(start, ende);
        student.urlaubEntfernen(start, ende);
        assertThat(student.getResturlaubInMin()).isEqualTo(240L);
    }

    @Test
    @DisplayName("Klausuren lassen sich dem Studenten hinzufügen.")
    void test_3() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        KlausurReferenz klausur = new KlausurReferenz(123456L);
        student.klausurAnmelden(klausur);
        assertThat(student.getKlausurAnmeldungen()).contains(klausur);
    }

    @Test
    @DisplayName("Klausuren lassen sich vom Studenten entfernen.")
    void test_4() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        KlausurReferenz klausur = new KlausurReferenz(234567L);
        student.klausurAnmelden(klausur);

        student.klausurAbmelden(klausur);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(klausur);
    }

    @Test
    @DisplayName("Student.urlaubNehmen() erzeugt einen neuen Urlaubseintrag in Student.urlaube")
    void test_5() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);

        student.urlaubNehmen(start, ende);
        assertThat(student.getUrlaube()).contains(urlaubsEintrag);
    }

    @Test
    @DisplayName("Student.urlaubEntfernen() entfernt einen bestehenden Urlaubseintrag in Student.urlaube")
    void test_6() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        student.urlaubNehmen(start, ende);

        student.urlaubEntfernen(start, ende);
        assertThat(student.getUrlaube()).doesNotContain(urlaubsEintrag);
    }

    @Test
    @DisplayName("Student.hatUrlaubAm() erkennt bereits gebuchten Urlaub an einem gegebenen Tag")
    void test_7() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        student.urlaubNehmen(start, ende);
        LocalDate datum = start.toLocalDate();

        boolean b = student.hatUrlaubAm(datum);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett im bestehenden Urlaub")
    void test_8() {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub ist identisch mit bestehendem Urlaub")
    void test_9() {

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub beginnt innerhalb bestehenden Urlaubs und endet danach")
    void test_10() {

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub beginnt vor bestehendem Urlaub und endet innerhalb")
    void test_11() {

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett vor bestehendem Urlaub")
    void test_12() {

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 10, 00);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Geplanter Urlaub liegt komplett nach bestehendem Urlaub")
    void test_13() {

        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 00);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }
}
