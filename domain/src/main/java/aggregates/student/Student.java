package aggregates.student;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import stereotype.AggregateRoot;

@AggregateRoot
public class Student {

  Long id;
  String githubHandle;
  UrlaubsZeit resturlaub;
  List<UrlaubsEintrag> urlaube;

  Student(Long id, String github){
    this.id = id;
    this.githubHandle = github;
    this.resturlaub = new UrlaubsZeit();
    this.urlaube = new ArrayList<UrlaubsEintrag>();
  }

  public Long getResturlaubInMin() {
    return resturlaub.getMinuten();
  }

  void urlaubNehmen(Long minuten){
    resturlaub.zeitEntfernen(minuten);
  }

  void urlaubEntfernen(Long minuten){
    resturlaub.zeitHinzufuegen(minuten);
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
