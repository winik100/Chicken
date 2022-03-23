package de.hhu.propra.chicken.web.controllers;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Set;

@Controller
@Secured({"ROLE_USER", "ROLE_TUTOR", "ROLE_ADMIN"})
public class StudentController {

    private final BuchungsService buchungsService;
    private final StudentenService studentenService;
    private final KlausurService klausurService;

    public StudentController(BuchungsService buchungsService, StudentenService studentenService, KlausurService klausurService) {
        this.buchungsService = buchungsService;
        this.studentenService = studentenService;
        this.klausurService = klausurService;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal, Model model) throws IOException {
        Student student = studentenService.findeStudentMitHandle(principal.getAttribute("login"));
        if (student != null) {
            Set<Klausur> klausurenAusDB = klausurService.findeKlausurenMitIds(student.getKlausurAnmeldungen());
            model.addAttribute("klausuren", klausurenAusDB);
            model.addAttribute("urlaube", student.getUrlaube());
            model.addAttribute("urlaubssumme", student.summeBisherigenUrlaubs());
            model.addAttribute("resturlaub", student.getResturlaubInMin());
        } else {
            student = new Student(principal.getAttribute("login"));
            studentenService.studentHinzufuegen(student);
            model.addAttribute("klausuren", Collections.emptySet());
            model.addAttribute("urlaube", Collections.emptySet());
            model.addAttribute("urlaubssumme", 0L);
            model.addAttribute("resturlaub", 240L);
        }
        return "index";
    }

    @GetMapping("/klausuranmeldung")
    public String klausuranmeldung(Model model) {
        Set<Klausur> klausuren = klausurService.alleKlausuren();
        model.addAttribute("klausuren", klausuren);
        return "klausuranmeldung";
    }

    @GetMapping("/klausurregistrierung")
    public String klausurregistrierung() {
        return "klausurregistrierung";
    }

    @PostMapping("/klausurregistrierung")
    public String klausurregistrierungDurchfuehren(@RequestParam("veranstaltung") String name,
                                                  @RequestParam("lsfid") Long lsfId,
                                                  @RequestParam(value = "vor_ort", required = false) String praesenz,
                                                  @RequestParam("datum") String datum,
                                                  @RequestParam("von") String von,
                                                  @RequestParam("bis") String bis) throws IOException {
        DateTimeFormatter zeitFormatierer = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime start = LocalDateTime.parse(datum + " " + von, zeitFormatierer);
        LocalDateTime ende = LocalDateTime.parse(datum + " " + bis, zeitFormatierer);
        if ("true".equals(praesenz)) {
            praesenz = "praesenz";
        }
        else {
            praesenz = "online";
        }
        klausurService.klausurHinzufuegen(new Klausur(lsfId, name, start, ende, praesenz));
        return "redirect:/klausuranmeldung";
    }

//    @PostMapping("/klausuranmeldung")
//    public String klausurAnmeldungDurchfuehren(@RequestParam("klausur")String lsfId, @ModelAttribute("student")Student student) throws IOException {
//        buchungsService.klausurBuchen(Long.valueOf(lsfId), student.getId());
//        return "redirect:/klausuranmeldung";
//    }
}
