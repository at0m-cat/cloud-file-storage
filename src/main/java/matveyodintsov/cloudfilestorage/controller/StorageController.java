package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletRequest;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.models.FileEntity;
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

    //todo: скачать папку, скачать несколько файлов

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
        model.addAttribute("path", encodedPath + "/");
        model.addAttribute("breadcrumbs", breadcrumbService.generateBreadcrumbs(fullPath));
        model.addAttribute("user", login);

        return "storage/folder";
    }

    @PostMapping("/create/folder")
    public String createFolder(@RequestParam("folder") String folder, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);
        String filename = Validator.ContentName.getValidFoldername(folder);

        checkFolderNameOrThrow(decodedPath, filename);

        folderService.create(decodedPath, filename);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/upload/file")
    public String insertFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);
        String fileName = file.getOriginalFilename();

        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Файл должен содержать имя!");
        }

        checkFileNameOrThrow(decodedPath, fileName);

        fileService.insert(file, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/rename/file")
    public String renameFile(@RequestParam("path") String path,
                             @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {
        String decodedPath = Validator.Url.decode(path);
        String filename = Validator.ContentName.getValidFilename(oldName, newName);

        checkFileNameOrThrow(decodedPath, filename);

        fileService.rename(oldName, filename, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/rename/folder")
    public String renameFolder(@RequestParam("path") String path,
                               @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {

        String decodedPath = Validator.Url.decode(path);
        String foldername = Validator.ContentName.getValidFoldername(newName);

        System.out.println("FileName: " + foldername);
        System.out.println("DecodedPath: " + decodedPath);

        checkFolderNameOrThrow(decodedPath, foldername);

        folderService.rename(oldName, foldername, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/delete/file")
    public String deleteFile(@RequestParam("file") List<String> file, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);

        for (String files : file) {
            fileService.delete(decodedPath, files);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/delete/folder")
    public String deleteFolder(@RequestParam("folder") List<String> folder, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);

        for (String folders : folder) {
            folderService.delete(decodedPath, folders);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    private void checkFileNameOrThrow (String path, String filename) {
        FolderEntity folder = folderService.findByPathAndUserLoginOrElseNull(path, SecurityUtil.getSessionUser());

        if (folder == null) {
            List<FileEntity> filesNoFolder = fileService.findByUserLoginAndFolderEqualsNull(SecurityUtil.getSessionUser());
            for (FileEntity file : filesNoFolder) {
                if (filename.equals(file.getName())) {
                    throw new RuntimeException("Файл с таким именем уже существует!");
                }
            }
        } else {
            for (FileEntity filesFolder : folder.getFiles()) {
                if (filename.equals(filesFolder.getName())) {
                    throw new RuntimeException("Файл с таким именем уже существует!");
                }
            }
        }
    }

    private void checkFolderNameOrThrow (String path, String foldername) {
        FolderEntity parentFolder = folderService.findByPathAndUserLoginOrElseNull(path, SecurityUtil.getSessionUser());
        if (parentFolder == null) {
            List<FolderEntity> foldersNoParent = folderService.findByUserLoginAndParentEqualsNull(SecurityUtil.getSessionUser());
            for (FolderEntity folders : foldersNoParent) {
                if (foldername.equals(folders.getName())) {
                    throw new RuntimeException("Папка с таким именем уже существует!");
                }
            }

        } else {
            for (FolderEntity subFolders : parentFolder.getSubfolders()) {
                if (foldername.equals(subFolders.getName())) {
                    throw new RuntimeException("Папка с таким именем уже существует!");
                }
            }
        }
    }
}
