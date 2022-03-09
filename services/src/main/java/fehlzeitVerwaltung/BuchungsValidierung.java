package fehlzeitVerwaltung;

import aggregates.klausur.Klausur;
import aggregates.student.Student;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class BuchungsValidierung {

  boolean dauerIstVielfachesVon15(LocalDateTime start, LocalDateTime ende){
    long dauer = Duration.between(start, ende).toMinutes();
    return (dauer % 15) == 0;
  }

  boolean startZeitIstVielfachesVon15(LocalDateTime start){
    return (start.getMinute() % 15) == 0;
  }

  boolean klausurAmGleichenTag(Student student, LocalDateTime start){
    LocalDate datum = start.toLocalDate();
    Set<Klausur> klausurAnmeldungen = student.getKlausurAnmeldungen();
    Set<LocalDate> klausurDaten =
        klausurAnmeldungen.stream().map(Klausur::getStart).map(LocalDateTime::toLocalDate).collect(Collectors.toSet());
    return klausurDaten.contains(datum);
  }

  boolean blockEntwederGanzerTagOderMax150Min(LocalDateTime start, LocalDateTime ende){
    long dauer = Duration.between(start, ende).toMinutes();
    return (dauer == 240 || dauer <= 150);
  }

  boolean mind90MinZwischenUrlauben(Student student, LocalDateTime startZweiterUrlaub, LocalDateTime endeZweiterUrlaub){
    return false;
  }
}
