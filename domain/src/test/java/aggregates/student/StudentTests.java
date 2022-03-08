package aggregates.student;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudentTests {

  @Test
  @DisplayName("urlaubNehmen() reduziert den Resturlaub des Studenten korrekt")
  void test_1(){
    Student student = new Student("ibimsgithub");
    student.urlaubNehmen(15L);
    assertThat(student.getResturlaubInMin()).isEqualTo(225L);
  }

  @Test
  @DisplayName("urlaubEntfernen() erhöht den Resturlaub des Studenten korrekt")
  void test_2(){
    Student student = new Student("ibimsgithub");
    student.urlaubNehmen(30L);
    student.urlaubEntfernen(15L);
    assertThat(student.getResturlaubInMin()).isEqualTo(225L);
  }
}
