package fehlzeitVerwaltung;

import static org.assertj.core.api.Assertions.assertThat;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BuchungsValidierungTests {

  @Test
  @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 60 min true zurück")
  void test_1(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
    LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 0);

    boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

    assertThat(b).isTrue();
  }

  @Test
  @DisplayName("dauerIstVielFachesVon15 gibt bei Dauer von 66 min false zurück")
  void test_2(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);
    LocalDateTime ende = LocalDateTime.of(2022, 3, 8, 13, 6);

    boolean b = buchungsValidierung.dauerIstVielfachesVon15(start, ende);

    assertThat(b).isFalse();
  }

  @Test
  @DisplayName("Ist die Startzeit ein Vielfaches von 15 (z.B. 12:00) wird true zurückgegeben")
  void test_3(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 0);

    boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

    assertThat(b).isTrue();
  }

  @Test
  @DisplayName("Ist die Startzeit kein Vielfaches von 15 (z.B. 12:10) wird false zurückgegeben")
  void test_4(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime start = LocalDateTime.of(2022, 3, 8, 12, 10);

    boolean b = buchungsValidierung.startZeitIstVielfachesVon15(start);

    assertThat(b).isFalse();
  }

  @Test
  @DisplayName("Klausur am gleichen Tag wird erkannt (unabhängig von Uhrzeit)")
  void test_5(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime klausurStart = LocalDateTime.of(2022, 3, 8, 12, 10);
    LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 13, 10);
    LocalDateTime klausurEnde = LocalDateTime.of(2022, 3, 8, 13, 0);
    Student student = new Student(10L, "ibimsgithub");
    Klausur klausur = new Klausur(234567, "Mathe", klausurStart, klausurEnde, "praesenz");
    student.klausurAnmelden(klausur);

    boolean b = buchungsValidierung.klausurAmGleichenTag(student, urlaubsStart);

    assertThat(b).isTrue();
  }

  @Test
  @DisplayName("Klausur am anderem Tag spielt keine Rolle")
  void test_6(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime klausurStart = LocalDateTime.of(2022, 3, 8, 12, 10);
    LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 9, 13, 10);
    LocalDateTime klausurEnde = LocalDateTime.of(2022, 3, 8, 13, 0);
    Student student = new Student(10L, "ibimsgithub");
    Klausur klausur = new Klausur(234567, "Mathe", klausurStart, klausurEnde, "praesenz");
    student.klausurAnmelden(klausur);

    boolean b = buchungsValidierung.klausurAmGleichenTag(student, urlaubsStart);

    assertThat(b).isFalse();
  }

  @Test
  @DisplayName("Ganzer Tag Urlaub ist erlaubt")
  void test_7(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
    LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 13, 30);

    boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

    assertThat(b).isTrue();
  }

  @Test
  @DisplayName("Urlaub unter 150 Min ist erlaubt")
  void test_8(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
    LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 10, 30);

    boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

    assertThat(b).isTrue();
  }

  @Test
  @DisplayName("Urlaub >150 Min, aber <240 Min ist nicht erlaubt")
  void test_9(){
    BuchungsValidierung buchungsValidierung = new BuchungsValidierung();
    LocalDateTime urlaubsStart = LocalDateTime.of(2022, 3, 8, 9, 30);
    LocalDateTime urlaubsEnde = LocalDateTime.of(2022, 3, 8, 12, 30);

    boolean b = buchungsValidierung.blockEntwederGanzerTagOderMax150Min(urlaubsStart, urlaubsEnde);

    assertThat(b).isFalse();
  }
}
