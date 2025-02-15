package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class dashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("user", SecurityUtil.getSessionUser());
        return "dashboard";
    }

}
