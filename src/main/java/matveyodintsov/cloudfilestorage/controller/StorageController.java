package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/storage")
public class StorageController {

    @GetMapping()
    public String storage(Model model) {
        model.addAttribute("user", SecurityUtil.getSessionUser());
        return "storage/home-storage";
    }
}
