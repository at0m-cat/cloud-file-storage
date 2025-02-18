package matveyodintsov.cloudfilestorage.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import matveyodintsov.cloudfilestorage.exception.FolderNotFoundException;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class FolderService {

    private final MinioClient minioClient;
    private final FolderRepository folderRepository;
    private final UserService userService;

    @Autowired
    public FolderService(MinioClient minioClient, FolderRepository folderRepository, UserService userService) {
        this.minioClient = minioClient;
        this.folderRepository = folderRepository;
        this.userService = userService;
    }

    public FolderEntity findByPathAndUserLogin(String path, String login) {
        return folderRepository.findByPathAndUserLogin(path, login).orElse(null);
    }

    public FolderEntity findByName(String folderName) {
        return folderRepository.findByName(folderName).orElse(null);
    }

    public List<FolderEntity> findByUserLogin(String username) {
        return folderRepository.findByUserLogin(username);
    }

    public List<FolderEntity> findByUserLoginAndParentEqualsNull(String username) {
        return folderRepository.findByUserLoginAndParentEqualsNull(username);
    }


    public void createFolder(String folderName, FolderEntity parent) {
        String login = SecurityUtil.getSessionUser();
        UserEntity user = userService.findByLogin(login);

        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(login).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(login).build());
            }

            String folderPath = (parent != null ? parent.getPath() : "") + folderName + "/";

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(login)
                            .object(folderPath)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );

            FolderEntity folderEntity = new FolderEntity();
            folderEntity.setName(folderName);
            folderEntity.setUser(user);
            folderEntity.setPath(folderPath);
            folderEntity.setParent(parent);

            folderRepository.save(folderEntity);
            System.out.println("Папка создана: " + folderPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
    }

}
