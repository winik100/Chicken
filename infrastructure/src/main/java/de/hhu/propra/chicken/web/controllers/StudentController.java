package de.hhu.propra.chicken.web.controllers;

import de.hhu.propra.chicken.aggregates.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.time.LocalDateTime;
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

    @PostMapping("/klausurstornierung")
    public RedirectView klausurstornierung(Model model,
                                           @AuthenticationPrincipal OAuth2User principal,
                                           @RequestParam("lsfId") String lsfId,
                                           RedirectAttributes redirectAttributes) throws IOException {
        Student student = studentenService.findeStudentMitHandle(principal.getAttribute("login"));
        Klausur klausur = klausurService.findeKlausurMitLsfId(Long.valueOf(lsfId));
        String klausurstornoerror = buchungsService.klausurStornieren(klausur, student);
        RedirectView redirectView = new RedirectView("/", true);
        redirectAttributes.addFlashAttribute("error", klausurstornoerror);
        return redirectView;
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
    public String klausurregistrierungDurchfuehren(Model model,
                                                   @RequestParam("veranstaltung") String name,
                                                   @RequestParam("lsfid") Long lsfId,
                                                   @RequestParam(value = "vor_ort", required = false) String praesenz,
                                                   @RequestParam("datum") String datum,
                                                   @RequestParam("von") String von,
                                                   @RequestParam("bis") String bis) throws IOException {
        DateTimeFormatter zeitFormatierer = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(datum + " " + von, zeitFormatierer);
        LocalDateTime ende = LocalDateTime.parse(datum + " " + bis, zeitFormatierer);
        if ("true".equals(praesenz)) {
            praesenz = "praesenz";
        }
        else {
            praesenz = "online";
        }
        String error = klausurService.klausurHinzufuegen(new Klausur(lsfId, name, start, ende, praesenz));
        model.addAttribute("error", error);
        if (error.isEmpty()){
            return "redirect:/klausuranmeldung";
        }
        return "klausurregistrierung";
    }

    @PostMapping("/klausuranmeldung")
    public String klausurAnmeldungDurchfuehren(@RequestParam("klausur")String lsfId, @AuthenticationPrincipal OAuth2User principal) throws IOException {
        Student student = studentenService.findeStudentMitHandle(principal.getAttribute("login"));
        Klausur klausur = klausurService.findeKlausurMitLsfId(Long.valueOf(lsfId));
        buchungsService.klausurBuchen(klausur, student);
        return "redirect:/";
    }

    @GetMapping("/urlaubsbuchung")
    public String urlaubsbuchung(){
        return "urlaubsbuchung";
    }

    @PostMapping("/urlaubsbuchung")
    public String urlaubsbuchungDurchfuehren(Model model,
                                             @AuthenticationPrincipal OAuth2User principal,
                                             @RequestParam("datum")String datum,
                                             @RequestParam("von")String von,
                                             @RequestParam("bis")String bis) throws IOException {
        Student student = studentenService.findeStudentMitHandle(principal.getAttribute("login"));
        DateTimeFormatter zeitFormatierer = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime.parse(datum + " " + von, zeitFormatierer);
        LocalDateTime ende = LocalDateTime.parse(datum + " " + bis, zeitFormatierer);
        String error = buchungsService.urlaubBuchen(student, start, ende);
        model.addAttribute("error", error);
        if (error.isEmpty()){
            return "redirect:/";
        }
        return "/urlaubsbuchung";
    }
}
