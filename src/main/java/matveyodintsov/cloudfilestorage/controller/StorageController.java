package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        String fullPath = request.getRequestURI().replace("/storage/my/", "");
        String login = SecurityUtil.getSessionUser();
        String decodePath = URLDecoder.decode(fullPath, StandardCharsets.UTF_8);

        FolderEntity folderEntity = folderService.findByPathAndUserLogin(decodePath + "/", login);

        model.addAttribute("folders", folderEntity.getSubfolders());
        model.addAttribute("files", fileService.findByFolder(folderEntity));
        model.addAttribute("path", fullPath + "/");
        model.addAttribute("breadcrumbs", breadcrumbService.generateBreadcrumbs(decodePath));
        model.addAttribute("user", login);

        return "storage/folder";
    }

    @PostMapping("/new-folder")
    public String createFolder(@RequestParam("folder") String folder, @RequestParam("path") String path) {
        String login = SecurityUtil.getSessionUser();
        String decodePath = URLDecoder.decode(path, StandardCharsets.UTF_8);

        FolderEntity parentFolder = folderService.findByPathAndUserLogin(decodePath, login);
        folderService.createFolder(folder, parentFolder);

        String url = path.substring(0, decodePath.lastIndexOf("/"));
        String encodeURL = URLEncoder.encode(url, StandardCharsets.UTF_8);

        return "redirect:/storage/my/" + encodeURL;
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
