package matveyodintsov.cloudfilestorage.repository;

import io.minio.*;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Repository
public class CloudRepository {

    private final MinioClient minioClient;

    @Autowired
    public CloudRepository (MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public InputStream downloadFile(String filePath) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(SecurityUtil.getSessionUser())
                            .object(filePath)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при скачивании файла: " + e.getMessage(), e);
        }
    }

    public void insertFile(MultipartFile file, String filePath) {
        try {
            createBucket();
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
            createBucket();
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
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(SecurityUtil.getSessionUser())
                            .object(filePath)
                            .build()
            );

        }catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {

            for (Result<Item> result : listObjects(folderPath)) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(SecurityUtil.getSessionUser())
                                .object(result.get().objectName())
                                .build()
                );
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(SecurityUtil.getSessionUser())
                            .object(folderPath)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении папки: " + e.getMessage(), e);
        }
    }

    private void createBucket() throws Exception {
        String bucketUserName = SecurityUtil.getSessionUser();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketUserName).build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketUserName).build());
        }
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

}
