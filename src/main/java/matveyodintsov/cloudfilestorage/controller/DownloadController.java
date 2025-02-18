package matveyodintsov.cloudfilestorage.controller;

import jakarta.servlet.http.HttpServletResponse;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @GetMapping()
    public void downloadFile(@RequestParam("file") String file, @RequestParam("path") String path, HttpServletResponse response) {
        try {
            String decodePath = URLDecoder.decode(path + file, StandardCharsets.UTF_8);

            InputStream fileStream = fileService.download(decodePath);

            String encodedFilename = URLEncoder.encode(file, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename*=UTF-8''" + encodedFilename);

            OutputStream out = response.getOutputStream();
            fileStream.transferTo(out);
            out.flush();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка скачивания файла: " + e.getMessage(), e);
        }
    }
}
