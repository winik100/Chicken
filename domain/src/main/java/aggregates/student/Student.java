package aggregates.student;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import aggregates.klausur.Klausur;
import stereotype.AggregateRoot;

@AggregateRoot
public class Student {

  Long id;
  String githubHandle;
  UrlaubsZeit resturlaub;
  List<UrlaubsEintrag> urlaube;
  Set<Klausur> klausurAnmeldungen;

  public List<UrlaubsEintrag> getUrlaube() {
    return urlaube;
  }

  public Student(Long id, String github){
    this.id = id;
    this.githubHandle = github;
    this.resturlaub = new UrlaubsZeit();
    this.urlaube = new ArrayList<>();
    this.klausurAnmeldungen = new HashSet<>();
  }

  public Long getResturlaubInMin() {
    return resturlaub.getMinuten();
  }

  public Long getId() {
    return id;
  }

  public Set<Klausur> getKlausurAnmeldungen() {
    return klausurAnmeldungen;
  }

  public void urlaubNehmen(LocalDateTime start, LocalDateTime ende){
    Long minuten = Duration.between(start, ende).toMinutes();
    UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
    urlaube.add(urlaubsEintrag);
    resturlaub.zeitEntfernen(minuten);
  }

  public void urlaubEntfernen(LocalDateTime start, LocalDateTime ende){
    Long minuten = Duration.between(start, ende).toMinutes();
    UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
    if (urlaube.remove(urlaubsEintrag)) {
      resturlaub.zeitHinzufuegen(minuten);
    }
  }

  public void klausurAbmelden(Klausur klausur) {
    klausurAnmeldungen.remove(klausur);
  }

  public void klausurAnmelden(Klausur klausur){
    klausurAnmeldungen.add(klausur);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Student student = (Student) o;
    return id.equals(student.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


}
