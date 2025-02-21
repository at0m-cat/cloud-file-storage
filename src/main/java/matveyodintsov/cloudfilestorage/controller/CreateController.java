package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.FilesAndFoldersChecker;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/storage/create")
public class CreateController {

    private final FolderService folderService;
    private final FilesAndFoldersChecker filesAndFoldersChecker;

    @Autowired
    public CreateController(FolderService folderService, FilesAndFoldersChecker filesAndFoldersChecker) {
        this.folderService = folderService;
        this.filesAndFoldersChecker = filesAndFoldersChecker;
    }

    @PostMapping("/folder")
    public String createFolder(@RequestParam("folder") String folder, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);
        String filename = Validator.ContentName.getValidFoldername(folder);

        filesAndFoldersChecker.checkFolderNameOrThrow(decodedPath, filename);

        folderService.create(decodedPath, filename);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

}
