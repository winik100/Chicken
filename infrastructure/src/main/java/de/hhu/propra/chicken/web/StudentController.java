package de.hhu.propra.chicken.web;

import de.hhu.propra.chicken.aggregates.BuchungsService;
import de.hhu.propra.chicken.aggregates.Student;
import de.hhu.propra.chicken.aggregates.StudentenService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@Secured({"ROLE_USER", "ROLE_TUTOR", "ROLE_ADMIN"})
public class StudentController {

    private final BuchungsService buchungsService;
    private final StudentenService studentenService;

    public StudentController(BuchungsService buchungsService, StudentenService studentenService) {
        this.buchungsService = buchungsService;
        this.studentenService = studentenService;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal OAuth2User principal, Model model) {
//        model.addAttribute(principal.getAttribute());
        String gitHubHandle = principal.getAttribute("login");
        Student student = studentenService.findeStudentMitHandle(gitHubHandle);
        return "index";
    }
}
