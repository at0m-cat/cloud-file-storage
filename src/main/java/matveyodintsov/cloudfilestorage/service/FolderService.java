package matveyodintsov.cloudfilestorage.service;

import io.minio.MinioClient;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserService userService;
    private final CloudService cloudService;

    @Autowired
    public FolderService(FolderRepository folderRepository, UserService userService, CloudService cloudService) {
        this.folderRepository = folderRepository;
        this.userService = userService;
        this.cloudService = cloudService;
    }

    @Transactional
    public void createFolder(String folderName, FolderEntity parent) {
        String login = SecurityUtil.getSessionUser();
        UserEntity user = userService.findByLogin(login);

        String folderPath = (parent != null ? parent.getPath() : "") + folderName + "/";

        cloudService.createFolder(folderPath);

        FolderEntity folderEntity = new FolderEntity();
        folderEntity.setName(folderName);
        folderEntity.setUser(user);
        folderEntity.setPath(folderPath);
        folderEntity.setParent(parent);
        folderRepository.save(folderEntity);
        System.out.println("Папка создана: " + folderPath);
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



}
