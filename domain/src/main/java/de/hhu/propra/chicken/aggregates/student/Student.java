package de.hhu.propra.chicken.aggregates.student;

import de.hhu.propra.chicken.stereotype.AggregateRoot;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Student {

    Long id;
    String githubHandle;
    UrlaubsZeit restUrlaub;
    Set<UrlaubsEintrag> urlaube;
    Set<KlausurReferenz> klausurAnmeldungen;

    public Student(Long id, String github) {
        this.id = id;
        this.githubHandle = github;
        this.restUrlaub = new UrlaubsZeit();
        this.urlaube = new HashSet<>();
        this.klausurAnmeldungen = new HashSet<>();
    }

    public Set<UrlaubsEintrag> getUrlaube() {
        return urlaube;
    }

    public Long getResturlaubInMin() {
        return restUrlaub.getMinuten();
    }

    public Long getId() {
        return id;
    }

    public Set<KlausurReferenz> getKlausurAnmeldungen() {
        return klausurAnmeldungen;
    }

    //TODO: Mögliche Überschneidung Klausur/Urlaub entfernen
    public void urlaubNehmen(LocalDateTime start, LocalDateTime ende) {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        urlaube.add(urlaubsEintrag);
        restUrlaub.zeitEntfernen(minuten);
    }

    public void urlaubEntfernen(LocalDateTime start, LocalDateTime ende) {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        if (urlaube.remove(urlaubsEintrag)) {
            restUrlaub.zeitHinzufuegen(minuten);
        }
    }

    public void klausurAbmelden(KlausurReferenz klausur) {
        klausurAnmeldungen.remove(klausur);
    }

    public void klausurAnmelden(KlausurReferenz klausur) {
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


    public String getGithubHandle() {
        return githubHandle;
    }

    public boolean ueberschneidungMitBestehendemUrlaub(LocalDateTime start, LocalDateTime ende) {
        Set<UrlaubsEintrag> urlaubeMitUeberschneidung = new HashSet<>();
        Set<UrlaubsEintrag> alleUrlaube = urlaube.stream()
                .filter(x -> x.start().toLocalDate().equals(start.toLocalDate()))
                .collect(Collectors.toSet());
        for (UrlaubsEintrag u : alleUrlaube) {
            if (u.start().isAfter(start.minusMinutes(1)) && u.start().isBefore((ende))) {
                urlaubeMitUeberschneidung.add(u);
            }
            if (u.ende().isAfter(start) && u.ende().minusMinutes(1).isBefore(ende)) {
                urlaubeMitUeberschneidung.add(u);
            }
            if (u.start().isBefore(start) && u.ende().isAfter(ende)) {
                urlaubeMitUeberschneidung.add(u);
            }
        }
        return !urlaubeMitUeberschneidung.isEmpty();
    }
}
