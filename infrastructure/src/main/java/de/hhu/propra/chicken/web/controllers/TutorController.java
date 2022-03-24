package de.hhu.propra.chicken.web.controllers;

import de.hhu.propra.chicken.annotations.AdminAndTutorOnly;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AdminAndTutorOnly
public class TutorController {

    @GetMapping("/tutor")
    @ResponseBody
    public String index(Model model) {
        return "Doesn't feel quite as good to be tutor :_(";
    }

}
