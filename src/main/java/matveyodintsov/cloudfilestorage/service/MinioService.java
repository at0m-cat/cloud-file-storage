package matveyodintsov.cloudfilestorage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Autowired
    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
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

    private void createBucket() throws Exception {
        String bucketUserName = SecurityUtil.getSessionUser();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketUserName).build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketUserName).build());
        }
    }
}
