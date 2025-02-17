package matveyodintsov.cloudfilestorage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
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
    private final UserService<UserRegisterDto, UserEntity> userService;

    //todo: сохранять файл в базу

    @Autowired
    public FileService(MinioClient minioClient, FileRepository fileRepository, UserService<UserRegisterDto, UserEntity> userService) {
        this.minioClient = minioClient;
        this.fileRepository = fileRepository;
        this.userService = userService;
    }

    public List<FileEntity> getUserFiles(String username) {
        return fileRepository.findByUserLogin(username);
    }

//    public void createFolder(String folderName) {
//        try {
//            if (!folderName.endsWith("/")) {
//                folderName += "/";
//            }
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(getLogin())
//                            .object(folderName)
//                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
//                            .build()
//            );
//        } catch (Exception e) {
//            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
//        }
//    }

    public void uploadFile(MultipartFile file) throws Exception {
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
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            FileEntity fileEntity = mapToFileEntity(file);
            save(fileEntity);

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

    private FileEntity mapToFileEntity(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String userName = SecurityUtil.getSessionUser();
        String filePath = fileName;
        Long fileSize = file.getSize();

        FileEntity fileEntity = new FileEntity();
        fileEntity.setUser(userService.findByLogin(userName));
        fileEntity.setName(fileName);
        fileEntity.setPath(filePath);
        fileEntity.setSize(fileSize);
        return fileEntity;
    }

    private void save(FileEntity fileEntity) throws Exception {
        fileRepository.save(fileEntity);
    }

}