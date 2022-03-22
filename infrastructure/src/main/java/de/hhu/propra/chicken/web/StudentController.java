package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
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
    public String index(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String gitHubHandle = principal.getAttribute("login");
        Student student = studentenService.findeStudentMitHandle(gitHubHandle);
        if (student != null) {
            Set<Klausur> klausurenAusDB = klausurService.findeKlausurenMitIds(student.getKlausurAnmeldungen());
            model.addAttribute("klausuren", klausurenAusDB);
            model.addAttribute("urlaube", student.getUrlaube());
            model.addAttribute("student", student);
        } else {
            model.addAttribute("klausuren", Collections.EMPTY_SET);
            model.addAttribute("urlaube", Collections.EMPTY_SET);
        }
        return "index";
    }

    @GetMapping("/klausuranmeldung")
    public String klausuranmeldung() {
        return "klausuranmeldung";
    }

    @GetMapping("/klausurregistrierung")
    public String klausurregistrierung() {
        return "klausurregistrierung";
    }

    @PostMapping("/klausurregistrierung")
    public String klausurregistrierungDurchfuehren(Model model, @RequestParam("veranstaltung") String name,
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
        klausurService.klausurHinzufuegen(lsfId, name, start, ende, praesenz);
        return "redirect:/klausuranmeldung";
    }
}
