package de.hhu.propra.chicken.web;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Secured({"ROLE_TUTOR", "ROLE_ADMIN"})
public class TutorController {

    @GetMapping("/tutor")
    @ResponseBody
    public String index(Model model) {
        return "Doesn't feel quite as good to be tutor :_(";
    }

}
