package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.FilesAndFoldersChecker;
import matveyodintsov.cloudfilestorage.config.AppConfig;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        String decodedPath = AppConfig.Url.decode(path);
        String filename = AppConfig.ContentName.getValidFolderName(folder);

        filesAndFoldersChecker.checkFolderNameOrThrow(decodedPath, filename);

        folderService.create(decodedPath, filename);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

}
