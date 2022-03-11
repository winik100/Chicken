package aggregates.student;

import aggregates.klausur.Klausur;
import stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@AggregateRoot
public class Student {

    Long id;
    String githubHandle;
    UrlaubsZeit resturlaub;
    Set<UrlaubsEintrag> urlaube;
    Set<Klausur> klausurAnmeldungen;

    public Student(Long id, String github) {
        this.id = id;
        this.githubHandle = github;
        this.resturlaub = new UrlaubsZeit();
        this.urlaube = new HashSet<>();
        this.klausurAnmeldungen = new HashSet<>();
    }

    public Set<UrlaubsEintrag> getUrlaube() {
        return urlaube;
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

    //TODO: Mögliche Überschneidung Klausur/Urlaub entfernen
    public void urlaubNehmen(LocalDateTime start, LocalDateTime ende) {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        urlaube.add(urlaubsEintrag);
        resturlaub.zeitEntfernen(minuten);
    }

    public void urlaubEntfernen(LocalDateTime start, LocalDateTime ende) {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        if (urlaube.remove(urlaubsEintrag)) {
            resturlaub.zeitHinzufuegen(minuten);
        }
    }

    public void klausurAbmelden(Klausur klausur) {
        klausurAnmeldungen.remove(klausur);
    }

    public void klausurAnmelden(Klausur klausur) {
        klausurAnmeldungen.add(klausur);
    }

    public boolean hatUrlaubAm(LocalDate tag) {
        List<LocalDate> urlaubsDaten = urlaube.stream().map(x -> x.start().toLocalDate()).toList();
        return urlaubsDaten.contains(tag);
    }

    // wird nur aufgerufen, wenn an dem Tag bereits ein Urlaub eingetragen ist
    public LocalDateTime startDesUrlaubsAm(LocalDate tag) {
        Optional<LocalDateTime> startZeit = urlaube.stream().map(UrlaubsEintrag::start).filter(x -> x.toLocalDate().equals(tag)).findFirst();
        return startZeit.get();
    }

    // wird nur aufgerufen, wenn an dem Tag bereits ein Urlaub eingetragen ist
    public LocalDateTime endeDesUrlaubsAm(LocalDate tag) {
        Optional<LocalDateTime> endZeit = urlaube.stream().map(UrlaubsEintrag::ende).filter(x -> x.toLocalDate().equals(tag)).findFirst();
        return endZeit.get();
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
