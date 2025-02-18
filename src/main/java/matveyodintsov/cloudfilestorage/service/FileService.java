package matveyodintsov.cloudfilestorage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.repository.FileRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class FileService {

    private final MinioClient minioClient;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final FolderService folderService;

    @Autowired
    public FileService(MinioClient minioClient, FileRepository fileRepository, UserService userService, FolderService folderService) {
        this.minioClient = minioClient;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.folderService = folderService;
    }

    public List<FileEntity> findByFolder(FolderEntity folder) {
        return fileRepository.findByFolder(folder);
    }

    public List<FileEntity> findByUserLoginAndFolderEqualsNull(String login) {
        return fileRepository.findByUserLoginAndFolderEqualsNull(login);
    }

    public List<FileEntity> findByUserLogin(String user) {
        return fileRepository.findByUserLogin(user);
    }

    public void uploadFile(MultipartFile file, String path) throws Exception {
        if (!isAuthenticated()) {
            throw new IllegalArgumentException("Невозможно определить пользователя!");
        }

        String bucketUserName = getLogin();
        String fileName = file.getOriginalFilename();

        if (isFileNameEmpty(fileName)) {
            throw new IllegalArgumentException("Файл должен иметь имя!");
        }

        try {
            createBucket(bucketUserName);
            try (InputStream inputStream = file.getInputStream()) {

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketUserName)
                                .object(path + fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            FolderEntity folder = folderService.findByPathAndUserLogin(path, bucketUserName);

            FileEntity fileEntity = new FileEntity();
            fileEntity.setUser(userService.findByLogin(bucketUserName));
            fileEntity.setName(fileName);
            fileEntity.setFolder(folder);
            fileEntity.setSize(file.getSize());
            fileRepository.save(fileEntity);

        } catch (MinioException e) {
            throw new RuntimeException("Ошибка при загрузке файла в MinIO: " + e.getMessage(), e);
        }
    }

    private boolean isAuthenticated() {
        return getLogin() != null;
    }

    private String getLogin() {
        return SecurityUtil.getSessionUser();
    }

    private boolean isFileNameEmpty(String fileName) {
        return (fileName == null || fileName.isEmpty());
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

    private void save(FileEntity fileEntity) throws Exception {
        fileRepository.save(fileEntity);
    }

}