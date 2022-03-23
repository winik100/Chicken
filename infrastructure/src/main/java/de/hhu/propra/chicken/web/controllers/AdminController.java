package de.hhu.propra.chicken.web.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Secured({"ROLE_ADMIN"})
public class AdminController {

    @GetMapping("/admin")
    @ResponseBody
    public String index(Model model) {
        return "Feels good to be admin :)";
    }

}
