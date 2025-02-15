package matveyodintsov.cloudfilestorage.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import matveyodintsov.cloudfilestorage.repository.FileRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {

    private final MinioClient minioClient;
    private final FileRepository fileRepository;

    //todo: сохранять файл в базу

    @Autowired
    public FileService(MinioClient minioClient, FileRepository fileRepository) {
        this.minioClient = minioClient;
        this.fileRepository = fileRepository;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        if (!isAuthenticated()) {
            throw new IllegalArgumentException("Невозможно определить пользователя!");
        }

        String bucketUserName = getLogin();

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("Файл должен иметь имя!");
        }
        try {
            createBucket(bucketUserName);
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketUserName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketUserName)
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (MinioException e) {
            throw new RuntimeException("Ошибка при загрузке файла в MinIO: " + e.getMessage(), e);
        }
    }

    private boolean isAuthenticated() {
        return getLogin() != null;
    }

    private String getLogin(){
        return SecurityUtil.getSessionUser();
    }

    private void createBucket(String bucketName) throws Exception {
        String bucketUserName = getLogin();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketUserName).build()
        );

        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketUserName).build());
        }
    }

}