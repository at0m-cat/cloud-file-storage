package matveyodintsov.cloudfilestorage.api;

import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import matveyodintsov.cloudfilestorage.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public final class MinioApi {

    private final MinioClient minioClient;
    private final String bucket = AppConfig.BUCKET_NAME;

    @Autowired
    public MinioApi(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public InputStream getObject(String filePath) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(filePath)
                        .build()
        );
    }

    public Iterable<Result<Item>> listObjects(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(path)
                        .recursive(true)
                        .build()
        );
    }

    public void copy(String oldPath, String newPath) throws Exception {
        minioClient.copyObject(
                CopyObjectArgs.builder()
                        .bucket(bucket)
                        .object(newPath)
                        .source(
                                CopySource.builder()
                                        .bucket(bucket)
                                        .object(oldPath)
                                        .build()
                        )
                        .build()
        );
    }

    public void putObject(MultipartFile file, String filePath) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(filePath)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        }
    }

    public void putObject(String folderPath) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(folderPath)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build()
        );
    }

    public void delete(String path) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(path)
                        .build()
        );
    }

    @PostConstruct
    private void initialBucket() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucket)
                        .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании корзины: " + e.getMessage());
        }
    }

}
