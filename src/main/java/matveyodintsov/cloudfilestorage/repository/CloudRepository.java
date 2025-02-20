package matveyodintsov.cloudfilestorage.repository;

import io.minio.*;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.*;

@Repository
public class CloudRepository {

    private final MinioClient minioClient;

    @Autowired
    public CloudRepository (MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public InputStream downloadFile(String filePath) {
        try {
            return getObject(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании файла: " + e.getMessage(), e);
        }
    }

    public InputStream downloadSelectedFiles(List<String> filePaths) {
        try {
            File zipFile = File.createTempFile("selected_files", ".zip");

            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos))) {

                for (String filePath : filePaths) {
                    try (InputStream fileStream = getObject(filePath)) {
                        String fileName = new File(filePath).getName();

                        zipOut.putNextEntry(new ZipEntry(fileName));
                        fileStream.transferTo(zipOut);
                        zipOut.closeEntry();

                    } catch (Exception e) {
                        System.err.println("Ошибка при добавлении файла в ZIP архив: " + e.getMessage());
                    }
                }
            }
            return new FileInputStream(zipFile);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании ZIP-архива: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFolder(String folderPath) {
        try {

            File zipFile = File.createTempFile("folder", ".zip");
            try (FileOutputStream fos = new FileOutputStream(zipFile);
                 ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(fos))) {

                Iterable<Result<Item>> objects = listObjects(folderPath);
                for (Result<Item> result : objects) {
                    String objectPath = result.get().objectName();

                    if (objectPath.equals(folderPath)) {
                        continue;
                    }

                    try (InputStream fileStream = getObject(objectPath)) {
                        String relativePath = objectPath.substring(folderPath.length());
                        if (relativePath.isEmpty()) {
                            continue;
                        }

                        zipOut.putNextEntry(new ZipEntry(relativePath));
                        fileStream.transferTo(zipOut);
                        zipOut.closeEntry();
                    }
                }
            }
            return new FileInputStream(zipFile);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании ZIP-файла: " + e.getMessage(), e);
        }
    }

    public void insertFile(MultipartFile file, String filePath) {
        try {
            createBucketOrElseVoid();
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(SecurityUtil.getSessionUser())
                                .object(filePath)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении файла: " + e.getMessage(), e);
        }
    }

    public void createFolder(String folderPath) {
        try {
            createBucketOrElseVoid();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(SecurityUtil.getSessionUser())
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
    }

    public void renameFile(String oldPath, String newPath) {
        try {
            copy(oldPath, newPath);
            deleteFile(oldPath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании файла: " + e.getMessage(), e);
        }
    }

    public void renameFolder(String oldPath, String newPath) {
        try {
            createFolder(newPath);

            for (Result<Item> result : listObjects(oldPath)) {
                String oldObjectPath = result.get().objectName();
                String newObjectPath = oldObjectPath.replace(oldPath, newPath);
                copy(oldObjectPath, newObjectPath);
            }
            deleteFolder(oldPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании папки: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            delete(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {

            for (Result<Item> result : listObjects(folderPath)) {
               delete(result.get().objectName());
            }
            deleteFile(folderPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении папки: " + e.getMessage(), e);
        }
    }

    private void createBucketOrElseVoid() throws Exception {
        String bucketName = SecurityUtil.getSessionUser();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private InputStream getObject(String filePath) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(filePath)
                        .build()
        );
    }

    private void copy(String oldPath, String newPath) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(newPath)
                        .source(
                                CopySource.builder()
                                        .bucket(SecurityUtil.getSessionUser())
                                        .object(oldPath)
                                        .build()
                        )
                        .build()
        );
    }

    private Iterable<Result<Item>> listObjects(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .prefix(path)
                        .recursive(true)
                        .build()
        );
    }

    private void delete(String path) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(path)
                        .build()
        );
    }

}
