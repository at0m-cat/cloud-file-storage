package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
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


    @GetMapping("/test/{test}")
    public String test(@PathVariable String test, Model model) {
        String login = SecurityUtil.getSessionUser();
        model.addAttribute("user", login);
        System.out.println(test);
        return "storage/folder";
    }

    @GetMapping()
    public String storage(Model model) {
        String login = SecurityUtil.getSessionUser();
        model.addAttribute("files", fileService.getFilesByUsername(login));
        model.addAttribute("folders", folderService.getFoldersByUsername(login));
        model.addAttribute("user", login);

        return "storage/home-storage";
    }

    @GetMapping("/my/{folder}/**")
    public String crossToFolder(@PathVariable String folder, Model model) {
        String folderName = folder;

        if (!folderName.endsWith("/")) {
            folderName += "/";
        }

        FolderEntity folderEntity = folderService.getFolder(folderName);

        String login = SecurityUtil.getSessionUser();
        model.addAttribute("user", login);
        model.addAttribute("folders", folderEntity.getSubfolders());
        return "storage/folder";
    }

    @PostMapping("/new-folder")
    public String newFolder(@RequestParam("folder") String folder,
                            HttpServletRequest request, Model model) {


        String folderName = folder;

        if (!folderName.endsWith("/")) {
            folderName += "/";
        }

        System.out.println(request.getRequestURI() + " -> folder path");
        System.out.println(folderName + " -> folder name");

//        folderService.createFolder(folderName, "secret/");

        String login = SecurityUtil.getSessionUser();
        model.addAttribute("user", login);
        return "redirect:/storage";
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
