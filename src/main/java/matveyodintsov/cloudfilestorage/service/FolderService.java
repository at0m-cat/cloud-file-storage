package matveyodintsov.cloudfilestorage.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    public FolderEntity getFolder(String folderName) {
        return folderRepository.findByName(folderName);
    }

    public List<FolderEntity> getFoldersByUsername(String username) {
        return folderRepository.findByUserLogin(username);
    }


    public void createFolder(String folderName, String parent) {
        String login = SecurityUtil.getSessionUser();
        UserEntity user = userService.findByLogin(login);
        try {

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(login)
                            .object(folderName)
                            .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                            .build()
            );


            FolderEntity folderEntity = new FolderEntity();
            folderEntity.setName(folderName);
            folderEntity.setUser(user);
            folderEntity.setParent(folderRepository.findByName(parent));

            folderRepository.save(folderEntity);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
    }

}
