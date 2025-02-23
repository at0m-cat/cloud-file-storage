package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.BreadcrumbService;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
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

    @GetMapping("/")
    public String dashboard(Model model) {
        String login = SecurityUtil.getSessionUser();
        if (login != null) {
            model.addAttribute("user", login);
            model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(login));
        }
        return "home/dashboard";
    }

    @GetMapping("/storage")
    public String storage(Model model) {
        String login = SecurityUtil.getSessionUser();
        model.addAttribute("files", fileService.findByUserLoginAndFolderEqualsNull(login));
        model.addAttribute("folders", folderService.findByUserLoginAndParentEqualsNull(login));
        model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(login));
        model.addAttribute("user", login);
        model.addAttribute("path", "");
        return "storage/home-storage";
    }

    @GetMapping("/storage/my/**")
    public String crossToFolder(HttpServletRequest request, Model model) {
        String path = request.getRequestURI();

        if ("/storage/my".equals(path)) {
            return "redirect:/storage";
        }

        String fullPath = Validator.Url.decode(path.replace("/storage/my/", ""));
        String encodedPath = Validator.Url.encode(fullPath);

        if (encodedPath.isEmpty()) {
            return "redirect:/storage";
        }

        String login = SecurityUtil.getSessionUser();

        FolderEntity folderEntity = folderService.findByPathAndUserLoginOrElseNull(fullPath + "/", login);
        if (folderEntity == null) {
            throw new RuntimeException("Страница не найдена");
        }

        model.addAttribute("folders", folderEntity.getSubfolders());
        model.addAttribute("files", fileService.findByFolder(folderEntity));
        model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(login));
        model.addAttribute("path", encodedPath + "/");
        model.addAttribute("breadcrumbs", breadcrumbService.generateBreadcrumbs(fullPath));
        model.addAttribute("user", login);

        return "storage/folder-storage";
    }

}
