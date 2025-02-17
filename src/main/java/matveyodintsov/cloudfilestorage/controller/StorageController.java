package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/storage")
public class StorageController {

    private final FileService fileService;

    @Autowired
    public StorageController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping()
    public String storage(Model model) {

        String login = SecurityUtil.getSessionUser();

//        model.addAttribute("files", fileService.getUserFiles(login));
        model.addAttribute("user", login);

        return "storage/home-storage";
    }
}
