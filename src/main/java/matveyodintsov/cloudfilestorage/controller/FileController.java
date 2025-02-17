package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;
    private final UserService<UserRegisterDto, UserEntity> userService;

    //todo: проверять пользователя, сравнивать с пользователем из сессии
    // объединить FileController + StorageController ?

    @Autowired
    public FileController(FileService fileService, UserService<UserRegisterDto, UserEntity> userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

//    @PostMapping("/folder")
//    public String createFolder(@RequestParam("newFolder") String newFolder) {
//        fileService.createFolder(newFolder);
//        return "redirect:/storage";
//    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            fileService.uploadFile(file);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
        return "redirect:/storage";
    }
}
