package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.*;
import de.hhu.propra.chicken.util.KlausurReferenz;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        Set<Klausur> klausuren = new HashSet<>();
        if (student != null){
            Set<Long> ids = student.getKlausurAnmeldungen().stream().map(KlausurReferenz::id).collect(Collectors.toSet());
            klausuren.addAll(klausurService.findeKlausurenMitIds(ids));
        }

        model.addAttribute("klausuren", klausuren);
        model.addAttribute("handle", gitHubHandle);
        return "index";
    }
}
