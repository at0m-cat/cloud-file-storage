package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/storage/finded")
public class FindController {

    private final FileService fileService;
    private final FolderService folderService;

    public FindController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @PostMapping("/files")
    public String findFile(@RequestParam("file") String file, Model model) {
        List<FileEntity> findFile = fileService.findByNameLikeAndUserLogin(file, SecurityUtil.getSessionUser());

        if (findFile.isEmpty()) {
            throw new RuntimeException("Файлов с таким именем не найдено");
        }

        model.addAttribute("files", findFile);
        model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(SecurityUtil.getSessionUser()));
        model.addAttribute("user", SecurityUtil.getSessionUser());

        return "storage/find-storage";
    }

    @PostMapping("/folders")
    public String findFolder(@RequestParam("folder") String folder, Model model) {
        List<FolderEntity> findFolder = folderService.findByNameLikeAndUserLogin(folder, SecurityUtil.getSessionUser());

        if (findFolder.isEmpty()) {
            throw new RuntimeException("Папок с таким именем не найдено");
        }

        model.addAttribute("folders", findFolder);
        model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(SecurityUtil.getSessionUser()));
        model.addAttribute("user", SecurityUtil.getSessionUser());

        return "storage/find-storage";
    }

}
