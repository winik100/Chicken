package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
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
        student.klausurAnmelden(PK_12_13);
        assertThat(student.getKlausurAnmeldungen()).contains(PK_12_13.getId());
    }

    @Test
    @DisplayName("Klausuren lassen sich vom Studenten entfernen.")
    void test_4() {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        //KlausurReferenz klausur = new KlausurReferenz(234567L);
        student.klausurAnmelden(PK_12_13);

        student.klausurAbmelden(PK_12_13);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(PK_12_13.getId());
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
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 10, 0);
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
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(startUrlaub2, endeUrlaub2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 09:30 bis 11:30, bei Onlineklausur von 11:00 bis 12:00, wird zu Urlaub von 09:30 bis 10:30")
    void test_14() {

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);
        LocalDateTime startFreistellung = OK_11_12.startFreistellungBerechnen();

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, startFreistellung));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:00 bis 13:30, bei Onlineklausur von 11:00 bis 12:00, wird zu Urlaub von 12:00 bis 13:30")
    void test_15() {

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);
        LocalDateTime endeFreistellung = OK_11_12.endeFreistellungBerechnen();

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(endeFreistellung, endeUrlaub));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 10:00 bis 12:30, bei Onlineklausur von 11:00 bis 12:00, wird zu zwei Urlauben von 10:00 bis 10:30 und von 12:00 bis 12:30")
    void test_16() {

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);
        LocalDateTime startFreistellung = OK_11_12.startFreistellungBerechnen();
        LocalDateTime endeFreistellung = OK_11_12.endeFreistellungBerechnen();

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);

        assertThat(student.getUrlaube().size()).isEqualTo(2);
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, startFreistellung));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(endeFreistellung, endeUrlaub));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:00 bis 12:00, bei Onlineklausur von 11:00 bis 12:00, wird nicht gebucht.")
    void test_17() {

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), startUrlaub, endeUrlaub);

        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 9:30 bis 13:30, bei Onlineklausur von 10:15 bis 11:00 und einer Onlineklausur von" +
            "12:00 bis 13:00, werden drei Urlaube gebucht (9:30-9:45, 11:00-11:30, 13:00-13:30).")
    void test_18() {

        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1015_11);
        student.klausurAnmelden(OK_12_13);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_1015_11, OK_12_13), startUrlaub, endeUrlaub);

        assertThat(student.getUrlaube().size()).isEqualTo(3);
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, OK_1015_11.startFreistellungBerechnen()));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(OK_1015_11.endeFreistellungBerechnen(), OK_12_13.startFreistellungBerechnen()));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(OK_12_13.endeFreistellungBerechnen(), endeUrlaub));
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich mit ganztägigem Urlaub.")
    void test_19() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich nicht, da der Student keinen Urlaub genommen hat.")
    void test_20() {
        Student student = new Student(10L, "ibimsgithub");

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich nicht mit Urlaub von 13 bis 13:30.")
    void test_21() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 11 bis 12.")
    void test_22() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 12 bis 13:30.")
    void test_23() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 11 bis 12 und einem Urlaub von 12:30 bis 13:30.")
    void test_24() {
        LocalDateTime startUrlaub1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime endeUrlaub1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime startUrlaub2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime endeUrlaub2 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub1, endeUrlaub1);
        student.urlaubNehmen(startUrlaub2, endeUrlaub2);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12 bis 13 wird der ganztägige Urlaub in zwei Urlaube geteilt.")
    void test_25() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, startUrlaub.plusMinutes(120)));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(endeUrlaub.minusMinutes(30), endeUrlaub));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        assertThat(student.getUrlaube().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12 bis 13 wird der Urlaub bis 12:30 auf 11:30 verkürtzt.")
    void test_26() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, startUrlaub.plusMinutes(120)));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaubsbeginn von 12:00 auf 13:00 geändert.")
    void test_27() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(endeUrlaub.minusMinutes(30), endeUrlaub));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaub von 12:00 bis 12:30 entfernt.")
    void test_28() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaub von 9:30 bis 10:00 nicht geändert.")
    void test_29() {
        LocalDateTime startUrlaub = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime endeUrlaub = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(startUrlaub, endeUrlaub);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(startUrlaub, endeUrlaub));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }
}
