package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.config.AppConfig;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/storage/delete")
public class DeleteController {

    private final FileService fileService;
    private final FolderService folderService;

    @Autowired
    public DeleteController(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    @PostMapping("/file")
    public String deleteFile(@RequestParam("file") List<String> file, @RequestParam("path") String path) {
        String decodedPath = AppConfig.Url.decode(path);

        for (String files : file) {
            fileService.delete(decodedPath, files);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

    @PostMapping("/folder")
    public String deleteFolder(@RequestParam("folder") List<String> folder, @RequestParam("path") String path) {
        String decodedPath = AppConfig.Url.decode(path);

        for (String folders : folder) {
            folderService.delete(decodedPath, folders);
        }

        return path.isEmpty() ? "redirect:/storage" : "redirect:/storage/my/" + AppConfig.Url.cross(decodedPath);
    }

}
