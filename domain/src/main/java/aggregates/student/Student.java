package aggregates.student;

import stereotype.AggregateRoot;

@AggregateRoot
public record Student(String githubHandle, Urlaubszeit resturlaub) {

  void urlaubNehmen(Long minuten){
    resturlaub.zeitEntfernen(minuten);
  }

  void urlaubEntfernen(Long minuten){
    resturlaub.zeitHinzufuegen(minuten);
  }
}
