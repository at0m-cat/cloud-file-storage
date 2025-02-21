package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/storage/find")
public class FindController {

    private final FileService fileService;
    private final FolderService folderService;

    public FindController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @GetMapping
    public String find(@RequestParam(value = "file", required = false) String file,
                       @RequestParam(value = "folder", required = false) String folder,
                       Model model) {

        String login = SecurityUtil.getSessionUser();

        List<FileEntity> findFiles = (file != null && !file.isEmpty()) ?
                fileService.findByNameLikeAndUserLogin(file, login) : Collections.emptyList();

        List<FolderEntity> findFolders = (folder != null && !folder.isEmpty()) ?
                folderService.findByNameLikeAndUserLogin(folder, login) : Collections.emptyList();

        model.addAttribute("files", findFiles);
        model.addAttribute("folders", findFolders);
        model.addAttribute("user", login);
        model.addAttribute("cloudSizeByUser", fileService.getSizeRepository(login));

        return "storage/find-storage";
    }

}
