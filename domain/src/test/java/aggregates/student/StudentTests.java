package aggregates.student;

import static org.assertj.core.api.Assertions.assertThat;

import aggregates.klausur.Klausur;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class StudentTests {

  @Test
  @DisplayName("urlaubNehmen() reduziert den Resturlaub des Studenten korrekt")
  void test_1(){
    Student student = new Student(10L,"ibimsgithub");
    student.urlaubNehmen(15L);
    assertThat(student.getResturlaubInMin()).isEqualTo(225L);
  }

  @Test
  @DisplayName("urlaubEntfernen() erhöht den Resturlaub des Studenten korrekt")
  void test_2(){
    Student student = new Student(10L,"ibimsgithub");
    student.urlaubNehmen(30L);
    student.urlaubEntfernen(15L);
    assertThat(student.getResturlaubInMin()).isEqualTo(225L);
  }

  @Test
  @DisplayName("Klausuren lassen sich dem Studenten hinzufügen.")
  void test_3(){
    Student student = new Student(10L,"ibimsgithub");
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
    LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
    Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
    student.klausurAnmelden(klausur);
    assertThat(student.getKlausurAnmeldungen()).contains(klausur);
  }

  @Test
  @DisplayName("Klausuren lassen sich vom Studenten entfernen.")
  void test_4(){
    Student student = new Student(10L,"ibimsgithub");
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
    LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);
    Klausur klausur = new Klausur(234567, "Mathe", start, ende, "praesenz");
    student.klausurAnmelden(klausur);

    student.klausurAbmelden(klausur);

    assertThat(student.getKlausurAnmeldungen()).doesNotContain(klausur);
  }
}
