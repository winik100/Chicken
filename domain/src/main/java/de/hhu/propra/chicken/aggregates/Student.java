package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.stereotypes.AggregateRoot;
import de.hhu.propra.chicken.util.AuditLog;
import de.hhu.propra.chicken.util.KlausurReferenz;
import de.hhu.propra.chicken.util.UrlaubsEintragDTO;

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

    public Student(String github) {
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

    public Student(Long id, String githubHandle, Long restUrlaub, Set<UrlaubsEintragDTO> urlaube, Set<KlausurReferenz> klausurAnmeldungen) {
        this.id = id;
        this.githubHandle = githubHandle;
        this.restUrlaub = restUrlaub;
        this.urlaube = urlaube.stream()
                .map(x -> new UrlaubsEintrag(x.start(), x.ende()))
                .collect(Collectors.toSet());
        this.klausurAnmeldungen = klausurAnmeldungen;
    }

    public Long getId() {
        return id;
    }

    public String getGithubHandle() {
        return githubHandle;
    }

    public Long getResturlaubInMin() {
        return restUrlaub;
    }

    public Set<UrlaubsEintrag> getUrlaube() {
        return urlaube;
    }

    public Set<UrlaubsEintragDTO> getUrlaubeAlsDTOs() {
        return urlaube.stream()
                .map(x -> new UrlaubsEintragDTO(x.start(), x.ende()))
                .collect(Collectors.toSet());
    }

    public Set<Long> getKlausurAnmeldungen() {
        return klausurAnmeldungen.stream()
                .map(KlausurReferenz::klausur_id)
                .collect(Collectors.toSet());
    }

    // wird nur aufgerufen, wenn an dem Tag bereits ein Urlaub eingetragen ist
    public LocalDateTime startDesUrlaubsAm(LocalDate tag) {
        return urlaube.stream()
                .map(UrlaubsEintrag::start)
                .filter(x -> x.toLocalDate().equals(tag))
                .findFirst().get();
    }

    // wird nur aufgerufen, wenn an dem Tag bereits ein Urlaub eingetragen ist
    public LocalDateTime endeDesUrlaubsAm(LocalDate tag) {
        return urlaube.stream()
                .map(UrlaubsEintrag::ende)
                .filter(x -> x.toLocalDate().equals(tag))
                .findFirst().get();
    }

    public boolean hatUrlaubAm(LocalDate tag) {
        return urlaube.stream()
                .map(x -> x.start().toLocalDate())
                .toList()
                .contains(tag);
    }

    public Long summeBisherigenUrlaubs() {
        if (urlaube.isEmpty()) {
            return 0L;
        }
        return urlaube.stream()
                .map(x -> Duration.between(x.start(), x.ende()).toMinutes())
                .reduce(0L, Long::sum);
    }

    public void urlaubNehmen(LocalDateTime start, LocalDateTime ende) throws IOException {
        urlaube.add(new UrlaubsEintrag(start, ende));
        restUrlaub -= Duration.between(start, ende).toMinutes();
        log.info(githubHandle, "Urlaub am " + start.toLocalDate() + " von " + start.toLocalTime() + " bis "
                + ende.toLocalTime() + " eingetragen.", LocalDateTime.now());
    }

    public void urlaubEntfernen(LocalDateTime start, LocalDateTime ende) throws IOException {
        if (urlaube.remove(new UrlaubsEintrag(start, ende))) {
            restUrlaub += Duration.between(start, ende).toMinutes();
            log.info(githubHandle, "Urlaub am " + start.toLocalDate() + " von " + start.toLocalTime() + " bis "
                    + ende.toLocalTime() + " entfernt.", LocalDateTime.now());
        }
    }

    public void klausurAnmelden(Klausur klausur) {
        klausurAnmeldungen.add(new KlausurReferenz(klausur.getId()));
    }

    public void klausurAbmelden(Klausur klausur) {
            klausurAnmeldungen.remove(new KlausurReferenz(klausur.getId()));
        }

    public boolean ueberschneidungMitBestehendemUrlaub(LocalDateTime start, LocalDateTime ende) {
        return !ueberschneidendeUrlaube(start, ende).isEmpty();
    }

    public boolean ueberschneidungKlausurMitBestehendemUrlaub(Klausur klausur) {
        LocalDateTime start = klausur.startFreistellungBerechnen();
        LocalDateTime ende = klausur.endeFreistellungBerechnen();
        return !ueberschneidendeUrlaube(start, ende).isEmpty();
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

    private boolean ueberschneidung(LocalDateTime start1, LocalDateTime ende1,
                                     LocalDateTime start2, LocalDateTime ende2) {
        return start1.isBefore(ende2) && start2.isBefore(ende1);
    }

    public void bestehendenUrlaubAnKlausurAnpassen(Klausur klausur) throws IOException {
        LocalDateTime freistellungsStart = klausur.startFreistellungBerechnen();
        LocalDateTime freistellungsEnde = klausur.endeFreistellungBerechnen();
        Set<UrlaubsEintrag> ueberschneidendeUrlaube = urlaube.stream()
                .filter(u -> ueberschneidung(u.start(), u.ende(), freistellungsStart, freistellungsEnde))
                .collect(Collectors.toSet());
        for (UrlaubsEintrag u : ueberschneidendeUrlaube) {
            urlaubAnKlausurAnpassenUndNehmen(Set.of(klausur), u.start(), u.ende());
            urlaubEntfernen(u.start(), u.ende());
        }
    }


    public void urlaubAnKlausurAnpassenUndNehmen(Set<Klausur> ueberschneidendeKlausuren,
                                                 LocalDateTime geplanterStart,
                                                 LocalDateTime geplantesEnde) throws IOException {
        UrlaubsEintrag geplanterUrlaub = new UrlaubsEintrag(geplanterStart, geplantesEnde);
        Set<UrlaubsEintrag> angepassteUrlaubsBloecke = new HashSet<>();
        angepassteUrlaubsBloecke.add(geplanterUrlaub);

        for (Klausur k : ueberschneidendeKlausuren) {
            LocalDateTime freistellungsStart = k.startFreistellungBerechnen();
            LocalDateTime freistellungsEnde = k.endeFreistellungBerechnen();
            Set<UrlaubsEintrag> temp = new HashSet<>(angepassteUrlaubsBloecke);

            boolean fertig = false;
            while (!fertig) {
                for (UrlaubsEintrag u : angepassteUrlaubsBloecke) {
                    if (u.start().isEqual(freistellungsStart)
                            && u.ende().isEqual(freistellungsEnde)) {
                        // geplanter Urlaub überschneidet sich exakt mit Freistellungszeitraum
                        temp.remove(u);
                        fertig = true;
                    } else if (u.start().isBefore(freistellungsStart)
                            && u.ende().isAfter(freistellungsStart)
                            && u.ende().isBefore(freistellungsEnde.plusMinutes(1))) {
                        // geplanter Urlaub beginnt vor Freistellungsbeginn und endet innerhalb der Freistellungszeit
                        temp.add(new UrlaubsEintrag(u.start(), freistellungsStart));
                        temp.remove(u);
                        fertig = false;
                    } else if (u.start().isAfter(freistellungsStart.minusMinutes(1))
                            && u.start().isBefore(freistellungsEnde)
                            && u.ende().isAfter(freistellungsEnde)) {
                        // geplanter Urlaub beginnt nach Freistellungsbeginn und endet nach Freistellungsende
                        temp.add(new UrlaubsEintrag(freistellungsEnde, geplantesEnde));
                        temp.remove(u);
                        fertig = false;
                    } else if (u.start().isBefore(freistellungsStart.plusMinutes(1))
                            && u.ende().isAfter(freistellungsEnde.minusMinutes(1))) {
                        // geplanter Urlaub umfasst die gesamte Freistellungszeit
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
        for (UrlaubsEintrag u : angepassteUrlaubsBloecke) {
            if (ueberschneidungMitBestehendemUrlaub(u.start(), u.ende())) {
                urlaubAnBestehendenUrlaubAnpassenUndNehmen(u.start(), u.ende());
            } else {
                urlaubNehmen(u.start(), u.ende());
            }
        }
    }

    public void urlaubAnBestehendenUrlaubAnpassenUndNehmen(LocalDateTime geplanterUlaubsStart,
                                                            LocalDateTime geplantesUrlaubsEnde) throws IOException {
        Set<UrlaubsEintrag> ueberschneidendeUrlaube = urlaube.stream()
                .filter(u -> ueberschneidung(u.start(), u.ende(), geplanterUlaubsStart, geplantesUrlaubsEnde))
                .collect(Collectors.toSet());

        if (ueberschneidendeUrlaube.isEmpty()) {
            urlaubNehmen(geplanterUlaubsStart, geplantesUrlaubsEnde);
            return;
        }

        UrlaubsEintrag neuerUrlaub = new UrlaubsEintrag(geplanterUlaubsStart, geplantesUrlaubsEnde);
        for (UrlaubsEintrag u : ueberschneidendeUrlaube) {
            neuerUrlaub = urlaubeVerschmelzen(u, neuerUrlaub.start(), neuerUrlaub.ende());
            urlaubEntfernen(u.start(), u.ende());
            urlaubNehmen(neuerUrlaub.start(), neuerUrlaub.ende());
        }
    }

    private UrlaubsEintrag urlaubeVerschmelzen(UrlaubsEintrag u,
                                               LocalDateTime geplanterUrlaubsStart,
                                               LocalDateTime geplantesUrlaubsEnde) {
        if (u.start().isBefore(geplanterUrlaubsStart) && u.ende().isAfter(geplantesUrlaubsEnde)) {
            // u umfasst geplanterUrlaubsStart und geplantesUrlaubsEnde
            return new UrlaubsEintrag(u.start(), u.ende());
        } else if (u.start().isBefore(geplanterUrlaubsStart) && u.ende().isAfter(geplanterUrlaubsStart)
                && u.ende().isBefore(geplantesUrlaubsEnde)) {
            // u überschneidung mit geplanterUrlaubsStart
            return new UrlaubsEintrag(u.start(), geplantesUrlaubsEnde);
        } else if (u.start().isAfter(geplanterUrlaubsStart) && u.start().isBefore(geplantesUrlaubsEnde)
                && u.ende().isAfter(geplantesUrlaubsEnde)) {
            // u überschneidung mit geplantesUrlaubsEnde
            return new UrlaubsEintrag(geplanterUrlaubsStart, u.ende());
        }
        // u komplett zwischen geplanterUrlaubsStart und geplantesUrlaubsEnde
        return  new UrlaubsEintrag(geplanterUrlaubsStart, geplantesUrlaubsEnde);
        // keine Überschneidung nicht möglich, da auf ueberschneidendeUrlaube aufgerufen
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
