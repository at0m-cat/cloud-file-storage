package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/storage")
public class StorageController {

    private final FileService fileService;
    private final FolderService folderService;

    @Autowired
    public StorageController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @GetMapping()
    public String storage(Model model) {

        String login = SecurityUtil.getSessionUser();

//        model.addAttribute("files", fileService.getUserFiles(login));
        model.addAttribute("user", login);

        return "storage/home-storage";
    }

    @GetMapping("/my/{folderName}")
    public String myStorage(@PathVariable String folderName, Model model) {
        String login = SecurityUtil.getSessionUser();
        model.addAttribute("user", login);
        return "storage/home-storage";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
        return "redirect:/storage";
    }


}
