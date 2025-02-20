package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FileService fileService;

    @Autowired
    public HomeController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        String login = SecurityUtil.getSessionUser();
        if (login != null) {
            model.addAttribute("user", login);
            model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(login));
        }
        return "/home/dashboard";
    }

}
