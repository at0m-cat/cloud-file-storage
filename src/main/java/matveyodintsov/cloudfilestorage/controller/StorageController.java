package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import matveyodintsov.cloudfilestorage.models.Breadcrumb;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.BreadcrumbService;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/storage")
public class StorageController {

    private final FileService fileService;
    private final FolderService folderService;
    private final BreadcrumbService breadcrumbService;

    @Autowired
    public StorageController(FileService fileService, FolderService folderService, BreadcrumbService breadcrumbService) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.breadcrumbService = breadcrumbService;
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
        model.addAttribute("files", fileService.findByUserLoginAndFolderEqualsNull(login));
        model.addAttribute("folders", folderService.findByUserLoginAndParentEqualsNull(login));
        model.addAttribute("user", login);
        model.addAttribute("path", "");

        return "storage/home-storage";
    }

    @GetMapping("/my/**")
    public String crossToFolder(HttpServletRequest request, Model model) {
        String fullPath = request.getRequestURI()
                .replace("/storage/my/", "");
        String folderName = fullPath
                .substring(fullPath.lastIndexOf("/") + 1);
        String login = SecurityUtil.getSessionUser();

        if (!folderName.endsWith("/")) {
            folderName += "/";
        }

        System.out.println("FOLDER: " + folderName);
        System.out.println("LOGIN: " + login);
        System.out.println("FULL PATH: " + fullPath);

        //todo: разобраться с breadcrumb -> получать валидные ссылки

        FolderEntity folderEntity = folderService.findByName(folderName);
        List<Breadcrumb> breadcrumbs = breadcrumbService.generateBreadcrumbs(fullPath);
        for (Breadcrumb breadcrumb : breadcrumbs) {
            System.out.println(breadcrumb);
        }

        String path = breadcrumbs.get(breadcrumbs.size() - 1).getName() + "/";


        model.addAttribute("path", path);
        model.addAttribute("breadcrumbs", breadcrumbs);
        model.addAttribute("user", login);
        model.addAttribute("folders", folderEntity.getSubfolders());
        model.addAttribute("files", fileService.findByFolder(folderEntity));
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

        //todo: получать имя папки, в которой создается файл !!

//        folderService.createFolder(folderName, "qwerty/");

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
