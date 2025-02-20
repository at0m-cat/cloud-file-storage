package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletResponse;
import matveyodintsov.cloudfilestorage.config.Validator;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/storage/download")
public class DownloadController {

    private final FolderService folderService;
    private final FileService fileService;

    @Autowired
    public DownloadController(FolderService folderService, FileService fileService) {
        this.folderService = folderService;
        this.fileService = fileService;
    }

    @GetMapping("/file")
    public void downloadFile(@RequestParam("file") String file, @RequestParam("path") String path, HttpServletResponse response) {
        String decodedPath = Validator.Url.decode(path + file);
        String encodedFilename = Validator.Url.encode(file);

        InputStream fileStream = fileService.download(decodedPath);

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFilename);
        try {
            OutputStream out = response.getOutputStream();
            fileStream.transferTo(out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при скачивании файла: " + e.getMessage());
        }
    }

    @GetMapping("/folder")
    public void downloadFolder(@RequestParam("folder") String folder, @RequestParam("path") String path, HttpServletResponse response) {
        String decodedPath = Validator.Url.decode(path + folder + "/");
        String encodedFilename = Validator.Url.encode(folder);

        InputStream folderStream = folderService.download(decodedPath, folder);

        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + encodedFilename + ".zip");
        try {
            OutputStream out = response.getOutputStream();
            folderStream.transferTo(out);
            out.flush();
        } catch (
                IOException e) {
            throw new RuntimeException("Ошибка при скачивании ZIP-файла: " + e.getMessage(), e);
        }
    }
}
