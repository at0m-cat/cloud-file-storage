package matveyodintsov.cloudfilestorage.api;

import io.minio.*;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
public class MinioApi {

    private final MinioClient minioClient;

    @Autowired
    public MinioApi(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public InputStream getObject(String filePath) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(filePath)
                        .build()
        );
    }

    public Iterable<Result<Item>> listObjects(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .prefix(path)
                        .recursive(true)
                        .build()
        );
    }

    protected void createBucketOrElseVoid() throws Exception {
        String bucketName = SecurityUtil.getSessionUser();
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!bucketExists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    protected void copy(String oldPath, String newPath) throws Exception {
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

    protected void putObject(MultipartFile file, String filePath) throws Exception {
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
    }

    protected void putObject(String folderPath) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(folderPath)
                        .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                        .build()
        );
    }

    protected void delete(String path) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(SecurityUtil.getSessionUser())
                        .object(path)
                        .build()
        );
    }

}
