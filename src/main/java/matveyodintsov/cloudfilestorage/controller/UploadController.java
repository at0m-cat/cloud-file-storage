package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.FilesAndFoldersChecker;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/storage/upload")
public class UploadController {

    private final FileService fileService;
    private final FolderService folderService;
    private final FilesAndFoldersChecker filesAndFoldersChecker;

    @Autowired
    public UploadController(FileService fileService, FolderService folderService, FilesAndFoldersChecker filesAndFoldersChecker) {
        this.fileService = fileService;
        this.folderService = folderService;
        this.filesAndFoldersChecker = filesAndFoldersChecker;
    }


    @PostMapping("/file")
    public String insertFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);
        String fileName = file.getOriginalFilename();


        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Файл должен содержать имя!");
        }

        filesAndFoldersChecker.checkFileNameOrThrow(decodedPath, fileName);

        fileService.insert(file, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

    @PostMapping("/folder")
    public String insertFolder(@RequestParam("folder") MultipartFile folder, @RequestParam("path") String path) {
        String decodedPath = Validator.Url.decode(path);

        String fileName = folder.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Файл должен содержать имя");
        }

        filesAndFoldersChecker.checkFolderNameOrThrow(decodedPath, fileName);

        // folder service -> insert folder

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + Validator.Url.cross(decodedPath);
    }

}
