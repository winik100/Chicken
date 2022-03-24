package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotype.AggregateRoot;
import de.hhu.propra.chicken.util.AuditLog;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AggregateRoot
public class Student {

    private final AuditLog log = new AuditLog("auditlog.txt");

    Long id;
    String githubHandle;
    Long restUrlaub;
    Set<UrlaubsEintrag> urlaube;
    Set<KlausurReferenz> klausurAnmeldungen;

    public Student(String github){
        this.id = null;
        this.githubHandle = github;
        this.restUrlaub = 240L;
        this.urlaube = new HashSet<>();
        this.klausurAnmeldungen = new HashSet<>();
    }

    // Konstruktor für Tests, keine Zeit für komplettes Refactoring :(
    public Student(Long id, String github) {
        this.id = id;
        this.githubHandle = github;
        this.restUrlaub = 240L;
        this.urlaube = new HashSet<>();
        this.klausurAnmeldungen = new HashSet<>();
    }

    public Student(Long id, String githubHandle, Long restUrlaub, Set<UrlaubsEintrag> urlaube, Set<KlausurReferenz> klausurAnmeldungen) {
        this.id = id;
        this.githubHandle = githubHandle;
        this.restUrlaub = restUrlaub;
        this.urlaube = urlaube;
        this.klausurAnmeldungen = klausurAnmeldungen;
    }

    public Long summeBisherigenUrlaubs(){
        if (urlaube.isEmpty()){
            return 0L;
        }
        Set<Long> urlaubsZeitraeume = urlaube.stream().map(x -> Duration.between(x.start(), x.ende()).toMinutes()).collect(Collectors.toSet());
        return urlaubsZeitraeume.stream().reduce(0L, Long::sum);
    }

    public Set<UrlaubsEintrag> getUrlaube() {
        return urlaube;
    }

    public Long getResturlaubInMin() {
        return restUrlaub;
    }

    public Long getId() {
        return id;
    }

    public Set<Long> getKlausurAnmeldungen() {
        return klausurAnmeldungen.stream().map(KlausurReferenz::klausur_id).collect(Collectors.toSet());
    }

    public void urlaubNehmen(LocalDateTime start, LocalDateTime ende) throws IOException {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        urlaube.add(urlaubsEintrag);
        log.info(githubHandle,"Urlaub am " + start.toLocalDate() + " von " + start.toLocalTime() + " bis " + ende.toLocalTime() + " eingetragen.", LocalDateTime.now());
        restUrlaub -= minuten;
    }

    public void urlaubEntfernen(LocalDateTime start, LocalDateTime ende) {
        Long minuten = Duration.between(start, ende).toMinutes();
        UrlaubsEintrag urlaubsEintrag = new UrlaubsEintrag(start, ende);
        if (urlaube.remove(urlaubsEintrag)) {
            restUrlaub += minuten;
        }
    }

     public void klausurAbmelden(Klausur klausur) {
            klausurAnmeldungen.remove(new KlausurReferenz(klausur.getId()));
        }

     public void klausurAnmelden(Klausur klausur) {
            klausurAnmeldungen.add(new KlausurReferenz(klausur.getId()));
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
        Set<UrlaubsEintrag> urlaubeMitUeberschneidung = ueberschneidendeUrlaube(start, ende);
        return !urlaubeMitUeberschneidung.isEmpty();
    }

    public boolean ueberschneidungKlausurMitBestehendemUrlaub(Klausur klausur) {
        LocalDateTime start = klausur.startFreistellungBerechnen();
        LocalDateTime ende = klausur.endeFreistellungBerechnen();
        Set<UrlaubsEintrag> urlaubeMitUeberschneidung = ueberschneidendeUrlaube(start, ende);
        return !urlaubeMitUeberschneidung.isEmpty();
    }

    private Set<UrlaubsEintrag> ueberschneidendeUrlaube(LocalDateTime start, LocalDateTime ende) {
        Set<UrlaubsEintrag> urlaubeMitUeberschneidung = new HashSet<>();
        Set<UrlaubsEintrag> alleUrlaube = urlaube.stream()
                .filter(x -> x.start().toLocalDate().equals(start.toLocalDate()))
                .collect(Collectors.toSet());
        for (UrlaubsEintrag u : alleUrlaube) {
            if (ueberschneidung(u.start(), u.ende(), start, ende)) {
                urlaubeMitUeberschneidung.add(u);
            }
        }
        return urlaubeMitUeberschneidung;
    }

    private boolean ueberschneidung (LocalDateTime start1, LocalDateTime ende1, LocalDateTime start2, LocalDateTime ende2) {
        return start1.isBefore(ende2) && start2.isBefore(ende1);
    }

    public void bestehendenUrlaubAnKlausurAnpassen (Klausur klausur) throws IOException {
        LocalDateTime freistellungsStart = klausur.startFreistellungBerechnen();
        LocalDateTime freistellungsEnde = klausur.endeFreistellungBerechnen();

        Set<UrlaubsEintrag> ueberschneidendeUrlaube = urlaube.stream()
                .filter(u -> ueberschneidung(u.start(), u.ende(), freistellungsStart, freistellungsEnde))
                .collect(Collectors.toSet());

        for ( UrlaubsEintrag u : ueberschneidendeUrlaube) {
            urlaubAnKlausurAnpassenUndNehmen(Set.of(klausur), u.start(), u.ende());
            urlaubEntfernen(u.start(), u.ende());
        }
    }


    public void urlaubAnKlausurAnpassenUndNehmen(Set<Klausur> ueberschneidendeKlausuren, LocalDateTime geplanterStart, LocalDateTime geplantesEnde) throws IOException {
        UrlaubsEintrag geplanterUrlaub = new UrlaubsEintrag(geplanterStart, geplantesEnde);
        Set<UrlaubsEintrag> angepassteUrlaubsBloecke = new HashSet<>();
        angepassteUrlaubsBloecke.add(geplanterUrlaub);

        for (Klausur k : ueberschneidendeKlausuren) {
            LocalDateTime freistellungsStart = k.startFreistellungBerechnen();
            LocalDateTime freistellungsEnde = k.endeFreistellungBerechnen();
            Set<UrlaubsEintrag> temp = new HashSet<>(angepassteUrlaubsBloecke);

            boolean fertig = false;
            while(!fertig) {
                for (UrlaubsEintrag u : angepassteUrlaubsBloecke) {
                    if (u.start().isEqual(freistellungsStart) // geplanter Urlaub überschneidet sich exakt mit Freistellungszeitraum
                            && u.ende().isEqual(freistellungsEnde)) {
                        temp.remove(u);
                        fertig = true;
                    } else if (u.start().isBefore(freistellungsStart) //geplanter Urlaub beginnt vor Freistellungsbeginn und endet innerhalb der Freistellungszeit
                            && u.ende().isAfter(freistellungsStart)
                            && u.ende().isBefore(freistellungsEnde.plusMinutes(1))) {
                        temp.add(new UrlaubsEintrag(u.start(), freistellungsStart));
                        temp.remove(u);
                        fertig = false;
                    } else if (u.start().isAfter(freistellungsStart.minusMinutes(1)) //geplanter Urlaub beginnt nach Freistellungsbeginn und endet nach Freistellungsende
                            && u.start().isBefore(freistellungsEnde)
                            && u.ende().isAfter(freistellungsEnde)) {
                        temp.add(new UrlaubsEintrag(freistellungsEnde, geplantesEnde));
                        temp.remove(u);
                        fertig = false;
                    } else if (u.start().isBefore(freistellungsStart.plusMinutes(1)) //geplanter Urlaub umfasst die ganze Freistellungszeit
                            && u.ende().isAfter(freistellungsEnde.minusMinutes(1))) {
                        temp.add(new UrlaubsEintrag(u.start(), freistellungsStart));
                        temp.add(new UrlaubsEintrag(freistellungsEnde, u.ende()));
                        temp.remove(u);
                        fertig = false;
                    } else {
                        fertig = true;
                    }
                }
                angepassteUrlaubsBloecke.clear();
                angepassteUrlaubsBloecke.addAll(temp);
            }

        }
        for (UrlaubsEintrag u: angepassteUrlaubsBloecke){
            urlaubNehmen(u.start(), u.ende());
        }
    }
}
