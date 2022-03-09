package aggregates.student;

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

  void urlaubNehmen(Long minuten){
    resturlaub.zeitEntfernen(minuten);
  }

  void urlaubEntfernen(Long minuten){
    resturlaub.zeitHinzufuegen(minuten);
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
