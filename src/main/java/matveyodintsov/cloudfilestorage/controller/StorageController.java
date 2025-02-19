package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.BreadcrumbService;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        String fullPath = Validator.Url.decode(request.getRequestURI().replace("/storage/my/", ""));
        String encodedPath = Validator.Url.encode(fullPath);
        String login = SecurityUtil.getSessionUser();

        FolderEntity folderEntity = folderService.findByPathAndUserLogin(fullPath + "/", login);

        model.addAttribute("folders", folderEntity.getSubfolders());
        model.addAttribute("files", fileService.findByFolder(folderEntity));
        model.addAttribute("path", encodedPath + "/");
        model.addAttribute("breadcrumbs", breadcrumbService.generateBreadcrumbs(fullPath));
        model.addAttribute("user", login);

        return "storage/folder";
    }

    @PostMapping("/new-folder")
    public String createFolder(@RequestParam("folder") String folder, @RequestParam("path") String path) {
        String login = SecurityUtil.getSessionUser();
        String decodedPath = Validator.Url.decode(path);

        FolderEntity parentFolder = folderService.findByPathAndUserLogin(decodedPath, login);
        folderService.createFolder(folder, parentFolder);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/uploadFile")
    public String insertFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Файл должен содержать имя!");
        }

        fileService.insertFile(file, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

//    @PostMapping("/rename")
//    public String renameFile(@RequestParam("oldName") String oldName, @RequestParam("newName") String newName, @RequestParam("path") String path) {
//        String decodedPath = Validator.Url.decode(path);
//
//        System.out.println(oldName);
//        System.out.println(newName);
//
//        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
//    }

    @PostMapping("/delete")
    public String deleteFile(@RequestParam("file") List<String> file, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);

        for (String files : file) {
            fileService.deleteFile(decodedPath, files);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }
}
