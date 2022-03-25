package de.hhu.propra.chicken.web.controllers;

import de.hhu.propra.chicken.stereotypes.AdminOnly;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AdminOnly
public class AdminController {

    @GetMapping("/admin")
    @ResponseBody
    public String index(Model model) {
        return "Feels good to be admin :)";
    }

}
