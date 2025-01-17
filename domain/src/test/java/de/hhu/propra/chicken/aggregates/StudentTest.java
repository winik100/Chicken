package de.hhu.propra.chicken.aggregates;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static de.hhu.propra.chicken.util.KlausurTemplates.*;
import static org.assertj.core.api.Assertions.assertThat;

class StudentTest {

    @AfterAll
    static void logLoeschen() {
        File file = new File("auditlog.txt");
        file.delete();
    }

    @Test
    @DisplayName("Wenn der Student Urlaub nimmt, wird sein Resturlaub entsprechend reduziert.")
    void test_1() throws IOException {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 15);

        student.urlaubNehmen(start, ende);

        assertThat(student.getResturlaubInMin()).isEqualTo(225L);
    }

    @Test
    @DisplayName("Wenn der Student Urlaub ebtfernt, wird sein Resturlaub entsprechend erhöht.")
    void test_2() throws IOException {
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

        student.klausurAnmelden(PK_12_13);

        assertThat(student.getKlausurAnmeldungen()).contains(PK_12_13.getId());
    }

    @Test
    @DisplayName("Klausuren lassen sich vom Studenten entfernen.")
    void test_4() {
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(PK_12_13);

        student.klausurAbmelden(PK_12_13);

        assertThat(student.getKlausurAnmeldungen()).doesNotContain(PK_12_13.getId());
    }

    @Test
    @DisplayName("Wenn der Student Urlaub nimmt, wird dieser dem Set von Urlauben hinzugefügt")
    void test_5() throws IOException {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);

        student.urlaubNehmen(start, ende);

        assertThat(student.getUrlaube()).contains(urlaubsEintrag);
    }

    @Test
    @DisplayName("Wenn der Student Urlaub entfernt, wird dieser aus dem Set von Urlauben gelöscht")
    void test_6() throws IOException {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        student.urlaubNehmen(start, ende);

        student.urlaubEntfernen(start, ende);

        assertThat(student.getUrlaube()).doesNotContain(urlaubsEintrag);
    }

    @Test
    @DisplayName("Student.hatUrlaubAm() erkennt bereits gebuchten Urlaub an einem gegebenen Tag.")
    void test_7() throws IOException {
        Student student = new Student(10L, "ibimsgithub");
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
        student.urlaubNehmen(start, ende);
        LocalDate datum = start.toLocalDate();

        boolean b = student.hatUrlaubAm(datum);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 12 bis 12:30 liegt komplett im bestehenden Urlaub von 11:30 bis 13.")
    void test_8() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:30 bis 13 ist identisch mit bestehendem Urlaub von 11:30 bis 13.")
    void test_9() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 12:30 bis 13:30 beginnt innerhalb bestehenden Urlaubs von 11:30 bis 13 " +
            "und endet danach.")
    void test_10() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11 bis 12:30 beginnt vor bestehendem Urlaub von 11:30 bis 13 und endet innerhalb.")
    void test_11() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 9:30 bis 10 liegt komplett vor bestehendem Urlaub von 11:30 bis 12.")
    void test_12() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 12:30 bis 13 liegt komplett nach bestehendem Urlaub von 11:30 bis 12.")
    void test_13() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 13, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);

        boolean b = student.ueberschneidungMitBestehendemUrlaub(start2, ende2);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Geplanter Urlaub von 9:30 bis 11:30, bei Onlineklausur von 11:00 bis 12:00, " +
            "wird zu Urlaub von 9:30 bis 10:30.")
    void test_14() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), start, ende);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, OK_11_12.startFreistellungBerechnen()));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:00 bis 13:30, bei Onlineklausur von 11:00 bis 12:00, " +
            "wird zu Urlaub von 12:00 bis 13:30.")
    void test_15() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);
        LocalDateTime endeFreistellung = OK_11_12.endeFreistellungBerechnen();

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), start, ende);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(endeFreistellung, ende));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 10:00 bis 12:30, bei Onlineklausur von 11:00 bis 12:00, " +
            "wird zu zwei Urlauben von 10:00 bis 10:30 und von 12:00 bis 12:30.")
    void test_16() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), start, ende);

        assertThat(student.getUrlaube().size()).isEqualTo(2);
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, OK_11_12.startFreistellungBerechnen()));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(OK_11_12.endeFreistellungBerechnen(), ende));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 11:00 bis 12:00, bei Onlineklausur von 11:00 bis 12:00, wird nicht gebucht.")
    void test_17() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_11_12);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_11_12), start, ende);

        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Geplanter Urlaub von 9:30 bis 13:30, bei Onlineklausur von 10:15 bis 11:00 und einer Onlineklausur " +
            "von 12:00 bis 13:00, werden drei Urlaube gebucht (9:30-9:45, 11:00-11:30, 13:00-13:30).")
    void test_18() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_1015_11);
        student.klausurAnmelden(OK_12_13);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_1015_11, OK_12_13), start, ende);

        assertThat(student.getUrlaube().size()).isEqualTo(3);
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, OK_1015_11.startFreistellungBerechnen()));
        assertThat(student.getUrlaube()).contains(
                new UrlaubsEintrag(OK_1015_11.endeFreistellungBerechnen(), OK_12_13.startFreistellungBerechnen()));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(OK_12_13.endeFreistellungBerechnen(), ende));
    }

    @Test
    @DisplayName("Geplanter Urlaub von 9:30 bis 13:30, bei Onlineklausur von 12 bis 13 und bestehendem Urlaub vor " +
            "der Klausur von 9:30 bis 11:30, wird eingetragen von 13 bis 13:30.")
    void test_18b() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.klausurAnmelden(OK_12_13);
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 13, 30);

        student.urlaubAnKlausurAnpassenUndNehmen(Set.of(OK_12_13), start2 , ende2);

        assertThat(student.getUrlaube().size()).isEqualTo(2);
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start1, ende1));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(OK_12_13.endeFreistellungBerechnen(), ende2));
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich mit ganztägigem Urlaub.")
    void test_19() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich nicht, da der Student keinen Urlaub " +
            "genommen hat.")
    void test_20() {
        Student student = new Student(10L, "ibimsgithub");

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Präsenzklausur von 10 bis 11 überschneidet sich nicht mit Urlaub von 13 bis 13:30.")
    void test_21() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 13, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(PK_10_11);

        assertThat(b).isFalse();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 11 bis 12.")
    void test_22() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 12 bis 13:30.")
    void test_23() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Eine Onlineklausur von 12 bis 13 überschneidet sich mit Urlaub von 11 bis 12 und einem Urlaub " +
            "von 12:30 bis 13:30.")
    void test_24() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 12, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        student.urlaubNehmen(start2, ende2);

        boolean b = student.ueberschneidungKlausurMitBestehendemUrlaub(OK_12_13);

        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12 bis 13 wird der ganztägige Urlaub in zwei " +
            "Urlaube geteilt.")
    void test_25() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, start.plusMinutes(120)));
        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(ende.minusMinutes(30), ende));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12 bis 13 wird der Urlaub bis 12:30 auf 11:30 verkürtzt.")
    void test_26() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, start.plusMinutes(120)));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaubsbeginn von 12:00 " +
            "auf 13:00 geändert.")
    void test_27() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(ende.minusMinutes(30), ende));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaub von 12:00 bis 12:30 " +
            "entfernt.")
    void test_28() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 12, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Beim Anpassen an eine Onlineklausur von 12:00 bis 13:00 wird der Urlaub von 9:30 bis 10:00 " +
            "nicht geändert.")
    void test_29() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 10, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start, ende);

        student.bestehendenUrlaubAnKlausurAnpassen(OK_12_13);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Hat der Student noch keinen Urlaub, kann der geplante Urlaub unverändert genommen werden.")
    void test_30() throws IOException {
        LocalDateTime start = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 11, 0);
        Student student = new Student(10L, "ibimsgithub");

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start, ende);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start, ende));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Hat der Student keinen Urlaub, der sich mit dem geplanten Urlaub überschneidet, kann dieser " +
            "unverändert genommen werden.")
    void test_31() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 11, 0);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 12, 0);

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start2, ende2);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start2, ende2));
        assertThat(student.getUrlaube().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Beginnt der geplante Urlaub innerhalb bestehenden Urlaubs, wird die Startzeit auf die Startzeit " +
            "des bestehenden Urlaubs gesetzt.")
    void test_32() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 10, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 12, 0);

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start2, ende2);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start1, ende2));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Endet der geplante Urlaub innerhalb bestehenden Urlaubs, wird die Endzeit auf die Endzeit " +
            "des bestehenden Urlaubs gesetzt.")
    void test_33() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 10, 30);

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start2, ende2);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start2, ende1));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Umfasst der geplante Urlaub bestehenden Urlaub komplett, wird der geplante Urlaub gebucht " +
            "und der bestehende entfernt.")
    void test_34() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 0);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 11, 30);

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start2, ende2);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start2, ende2));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start1, ende1));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Liegt der geplante Urlaub komplett innerhalb bestehenden Urlaubs, wird er nicht eingetragen.")
    void test_35() throws IOException {
        LocalDateTime start1 = LocalDateTime.of(2022, 3, 8, 9, 30);
        LocalDateTime ende1 = LocalDateTime.of(2022, 3, 8, 11, 30);
        Student student = new Student(10L, "ibimsgithub");
        student.urlaubNehmen(start1, ende1);
        LocalDateTime start2 = LocalDateTime.of(2022, 3, 8, 10, 0);
        LocalDateTime ende2 = LocalDateTime.of(2022, 3, 8, 11, 0);

        student.urlaubAnBestehendenUrlaubAnpassenUndNehmen(start2, ende2);

        assertThat(student.getUrlaube()).contains(new UrlaubsEintrag(start1, ende1));
        assertThat(student.getUrlaube()).doesNotContain(new UrlaubsEintrag(start2, ende2));
        assertThat(student.getUrlaube().size()).isEqualTo(1);
    }
}
