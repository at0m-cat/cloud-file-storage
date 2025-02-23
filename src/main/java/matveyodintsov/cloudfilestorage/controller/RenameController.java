package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.FilesAndFoldersChecker;
import matveyodintsov.cloudfilestorage.config.AppConfig;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/storage/rename")
public class RenameController {

    private final FileService fileService;
    private final FolderService folderService;
    private final FilesAndFoldersChecker filesAndFoldersChecker;

    @Autowired
    public RenameController(FileService fileService, FolderService folderService, FilesAndFoldersChecker filesAndFoldersChecker) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.filesAndFoldersChecker = filesAndFoldersChecker;
    }

    @PostMapping("/file")
    public String renameFile(@RequestParam("path") String path,
                             @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {
        String decodedPath = AppConfig.Url.decode(path);
        String filename = AppConfig.ContentName.getValidFilename(oldName, newName);

        filesAndFoldersChecker.checkFileNameOrThrow(decodedPath, filename);

        fileService.rename(oldName, filename, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

    @PostMapping("/folder")
    public String renameFolder(@RequestParam("path") String path,
                               @RequestParam("oldName") String oldName, @RequestParam("newName") String newName) {
        String decodedPath = AppConfig.Url.decode(path);
        String folderName = AppConfig.ContentName.getValidFolderName(newName);

        filesAndFoldersChecker.checkFolderNameOrThrow(decodedPath, folderName);

        folderService.rename(oldName, folderName, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }


}
