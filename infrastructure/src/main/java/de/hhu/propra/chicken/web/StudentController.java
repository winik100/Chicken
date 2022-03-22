package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.*;
import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (student != null){
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




}
