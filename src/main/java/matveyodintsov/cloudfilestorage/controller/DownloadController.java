package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletResponse;
import matveyodintsov.cloudfilestorage.config.AppConfig;
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
import java.util.ArrayList;
import java.util.List;

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
    public void downloadFile(@RequestParam("file") List<String> file, @RequestParam("path") String path, HttpServletResponse response) {

        InputStream inputStream;
        String filename;

        if (file.size() > 1) {
            List<String> listDecodedPath = new ArrayList<>();
            for (String fileName : file) {

                String filePathDecode = AppConfig.Url.decode(path + fileName);
                listDecodedPath.add(filePathDecode);

            }
            inputStream = fileService.downloadSelected(listDecodedPath);

            filename = "selected_files.zip";

        } else {

            String decodedPath = AppConfig.Url.decode(path + file.get(0));
            inputStream = fileService.download(decodedPath);
            filename = AppConfig.Url.encode(file.get(0));

        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename*=UTF-8''" + filename);


        try {
            OutputStream out = response.getOutputStream();
            inputStream.transferTo(out);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при скачивании файла: " + e.getMessage());
        }
    }

    @GetMapping("/folder")
    public void downloadFolder(@RequestParam("folder") String folder, @RequestParam("path") String path, HttpServletResponse response) {
        String decodedPath = AppConfig.Url.decode(path + folder + "/");
        String encodedFilename = AppConfig.Url.encode(folder);

        InputStream folderStream = folderService.download(decodedPath);

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
