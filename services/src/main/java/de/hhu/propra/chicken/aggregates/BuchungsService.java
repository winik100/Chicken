package de.hhu.propra.chicken.aggregates;

import de.hhu.propra.chicken.util.KlausurReferenz;
import de.hhu.propra.chicken.stereotypes.ApplicationService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

@ApplicationService
public class BuchungsService {

    private final StudentRepository studentRepository;
    private final KlausurRepository klausurRepository;
    private final BuchungsValidierung validierung = new BuchungsValidierung();


    public BuchungsService(StudentRepository studentRepository, KlausurRepository klausurRepository) {
        this.studentRepository = studentRepository;
        this.klausurRepository = klausurRepository;
    }

    static LocalDateTime neuesUrlaubsEndeBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isBefore(freistellungsStart) && (ende.isAfter(freistellungsStart) && ende.isBefore(freistellungsEnde))) {
            return freistellungsStart;
        }
        return ende;
    }

    static LocalDateTime neuenUrlaubsStartBerechnen(LocalDateTime start, LocalDateTime ende, LocalDateTime freistellungsStart, LocalDateTime freistellungsEnde) {
        if (start.isAfter(freistellungsStart) && ende.isAfter(freistellungsEnde)) {
            return freistellungsEnde;
        }
        return start;
    }

    public String klausurBuchen(LsfId lsfId, Long studentID) throws IOException {
        Document doc = Jsoup.connect("https://lsf.hhu.de/qisserver/rds?state=verpublish&status=init&vmfile=no&publishid="
                + lsfId.toString()
                + "&moduleCall=webInfo&publishConfFile=webInfo&publishSubDir=veranstaltung").get();
        if (!validierung.gueltigeLsfId(lsfId, doc)){
            return "Die Veranstaltung mit der angegebenen Veranstaltungs-ID existiert nicht.";
        }
        KlausurReferenz klausur = new KlausurReferenz(lsfId.getId());
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAnmelden(klausur);
        return "Die Eingabe ist ok.";
    }

    public void klausurStornieren(LsfId lsfId, Long studentID) {
        KlausurReferenz klausur = new KlausurReferenz(lsfId.getId());
        Student student = studentRepository.studentMitId(studentID);
        student.klausurAbmelden(klausur);
    }

    public String urlaubBuchen(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        Set<Klausur> klausuren = klausurRepository.klausurenMitReferenzen(student.getKlausurAnmeldungen());
        if (!validierung.dauerIstVielfachesVon15(start, ende)) {
            return "Die Urlaubsdauer muss ein Vielfaches von 15 sein.";
        }
        if (!validierung.startZeitIstVielfachesVon15(start)) {
            return "Die Startzeit muss ein Vielfaches von 15 sein.";
        }

        // Urlaubszeit an Klausuren anpassen, kann keinen Fehler geben
        if (validierung.klausurAmGleichenTag(klausuren, start)) {
            Set<Klausur> ueberschneidendeKlausuren = validierung.ueberschneidungMitKlausur(klausuren, start, ende);
            if (!ueberschneidendeKlausuren.isEmpty()) {
                // Urlaubszeit an Klausuren anpassen
            }
        }

        if (student.hatUrlaubAm(start.toLocalDate())) {
            if (student.ueberschneidungMitBestehendemUrlaub(start, ende)) {
                return "Bestehender Urlaub muss erst storniert werden.";
            }
            if (!validierung.klausurAmGleichenTag(klausuren, start)) {
                if (!validierung.mind90MinZwischenUrlauben(student, start, ende)) {
                    return "Zwischen zwei Urlauben am selben Tag müssen mindestens 90 Minuten liegen" +
                            "und die beiden Urlaubsblöcke müssen am Anfang und Ende des Tages liegen.";
                }
            }
        }

        if (!validierung.klausurAmGleichenTag(klausuren, start)) {
            if (!validierung.blockEntwederGanzerTagOderMax150Min(start, ende)) {
                return "Der Urlaub muss entweder den ganzen Tag oder maximal 150 Minuten dauern.";
            }
        }
        student.urlaubNehmen(start, ende);
        return "Die Eingabe ist ok.";
    }

    public void urlaubStornieren(Long studentID, LocalDateTime start, LocalDateTime ende) {
        Student student = studentRepository.studentMitId(studentID);
        student.urlaubEntfernen(start, ende);
    }

//    public Set<UrlaubsEintrag> urlaubAnKlausurenAnpassen(Set<Klausur> klausuren, LocalDateTime geplanterStart, LocalDateTime geplantesEnde) {
//        UrlaubsEintrag urlaub = new UrlaubsEintrag(geplanterStart, geplantesEnde);
//        Set<UrlaubsEintrag> angepassteUrlaubsBloecke = new HashSet<>();
//        angepassteUrlaubsBloecke.add(urlaub);
//        for (Klausur k : klausuren) {
//            LocalDateTime freistellungsBeginn = k.startFreistellungBerechnen();
//            LocalDateTime freistellungsEnde = k.endeFreistellungBerechnen();
//            for (UrlaubsEintrag u : angepassteUrlaubsBloecke) {
//                if (u.start().isBefore(freistellungsBeginn) //geplanter Urlaub beginnt vor Freistellungsbeginn und endet innerhalb der Freistellungszeit
//                        && u.ende().isAfter(freistellungsBeginn)
//                        && u.ende().isBefore(freistellungsEnde)){
//                    angepassteUrlaubsBloecke.add(new UrlaubsEintrag(u.start(), freistellungsBeginn));
//                }
//                else if (){
//
//                }
//                else if {
//
//                }
//                angepassteUrlaubsBloecke.remove(u);
//            }
//        }
//        return angepassteUrlaubsBloecke;
//    }

}
