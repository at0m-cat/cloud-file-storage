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
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
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
    public String insertFile(@RequestParam("file") List<MultipartFile> file, @RequestParam("path") String path) {
        String decodedPath = AppConfig.Url.decode(path);

        for (MultipartFile fileEntity : file) {

            String fileName = fileEntity.getOriginalFilename();
            if (fileName == null || fileName.isEmpty()) {
                throw new RuntimeException("Файл должен содержать имя!");
            }

            filesAndFoldersChecker.checkFileNameOrThrow(decodedPath, fileName);

            fileService.insert(fileEntity, decodedPath);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

    @PostMapping("/folder")
    public String insertFolder(@RequestParam("folder") List<MultipartFile> folder, @RequestParam("path") String path) {
        String decodedPath = AppConfig.Url.decode(path);

        String fileName = folder.get(0).getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new RuntimeException("Файл должен содержать имя");
        }

        filesAndFoldersChecker.checkFolderNameOrThrow(decodedPath, folder.get(0).getOriginalFilename());

        // folder service -> insert folder
//        folderService.insert(folder, decodedPath);

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

}
