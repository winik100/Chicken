package aggregates.student;

import java.util.ArrayList;
import java.util.List;
import stereotype.AggregateRoot;

@AggregateRoot
public class Student {

  String githubHandle;
  UrlaubsZeit resturlaub;
  List<UrlaubsEintrag> urlaube;

  Student(String github){
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
}
