package matveyodintsov.cloudfilestorage.service;

import io.minio.Result;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.repository.CloudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    @Autowired
    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public InputStream downloadFile(String filePath) {
        try {
            return cloudRepository.getObject(currentUserFolder(filePath));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании файла: " + e.getMessage(), e);
        }
    }

    public InputStream downloadSelectedFiles(List<String> filePaths) {
        return zipFiles(filePaths.stream().map(this::currentUserFolder).toList());
    }

    public InputStream downloadFolder(String folderPath) {
        return zipFolder(currentUserFolder(folderPath));
    }

    public void createFolder(String folderPath) {
        cloudRepository.createFolder(currentUserFolder(folderPath));
    }

    public void insertFile(MultipartFile file, String filePath) {
        if (!file.getOriginalFilename().matches("^(?![-.])")) {
            cloudRepository.insertFile(file, currentUserFolder(filePath));
        }
    }

    public void renameFile(String oldPath, String newPath) {
        cloudRepository.renameFile(currentUserFolder(oldPath), currentUserFolder(newPath));
    }

    public void renameFolder(String oldPath, String newPath) {
        cloudRepository.renameFolder(currentUserFolder(oldPath), currentUserFolder(newPath));
    }

    public void deleteFile(String filePath) {
        cloudRepository.deleteFile(currentUserFolder(filePath));
    }

    public void deleteFolder(String folderPath) {
        cloudRepository.deleteFolder(currentUserFolder(folderPath));
    }

    private InputStream zipFiles(List<String> filePaths) {
        try {
            File zipFile = File.createTempFile("selected_files", ".zip");
            zipFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos))) {

                for (String filePath : filePaths) {
                    String relativePath = filePath.substring(filePath.indexOf("/") + 1);
                    addFileToZip(filePath, zipOut, relativePath);
                }
            }
            return new FileInputStream(zipFile);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании ZIP-архива: " + e.getMessage(), e);
        }
    }

    private InputStream zipFolder(String folderPath) {
        try {
            File zipFile = File.createTempFile("folder", ".zip");
            zipFile.deleteOnExit();

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos))) {

                Iterable<Result<Item>> objects = cloudRepository.listObjects(folderPath);
                for (Result<Item> result : objects) {
                    String objectPath = result.get().objectName();

                    if (objectPath.equals(folderPath)) {
                        continue;
                    }

                    String relativePath = objectPath.substring(folderPath.length());
                    addFileToZip(objectPath, zipOut, relativePath);
                }
            }
            return new FileInputStream(zipFile);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании ZIP-файла: " + e.getMessage(), e);
        }
    }

    private void addFileToZip(String filePath, ZipOutputStream zipOut, String zipEntryName) {
        try (InputStream fileStream = cloudRepository.getObject(filePath)) {
            zipOut.putNextEntry(new ZipEntry(zipEntryName));
            fileStream.transferTo(zipOut);
            zipOut.closeEntry();
        } catch (Exception e) {
            System.err.println("Ошибка при добавлении файла в ZIP архив: " + e.getMessage());
        }
    }

    private String currentUserFolder(String folderPath) {
        return SecurityUtil.getSessionUser() + "/" + folderPath;
    }
}