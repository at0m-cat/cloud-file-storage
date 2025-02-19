package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.exception.FolderNotFoundException;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
    }

    @Transactional
    public void deleteFolder(String path, String folderName) {
        String login = SecurityUtil.getSessionUser();
        String pathFolder = path + folderName + "/";
        FolderEntity folder = folderRepository.findByPathAndUserLogin(pathFolder, login)
                .orElseThrow(() -> new FolderNotFoundException("Папка не найдена"));

        cloudService.deleteFolder(path + folderName);

        folderRepository.delete(folder);
    }

    public void renameFolder(String oldName, String newName, String path) {
        FolderEntity folder;
        String pathFolderOld = path + oldName + "/";
        String pathFolderNew = path + newName + "/";
        if (path.isEmpty()) {
            folder = folderRepository.findByNameAndParentIsNullAndUserLogin(oldName, SecurityUtil.getSessionUser());
        } else {
            folder = folderRepository.findByNameAndPathAndUserLogin(oldName, pathFolderOld, SecurityUtil.getSessionUser());
        }
        cloudService.renameFolder(pathFolderOld, pathFolderNew);
        folder.setPath(pathFolderNew);
        folder.setName(newName);

        Queue<FolderEntity> queue = new LinkedList<>();
        queue.add(folder);

        while (!queue.isEmpty()) {
            FolderEntity currentFolder = queue.poll();
            for (FolderEntity subfolder : currentFolder.getSubfolders()) {
                String updatedPath = subfolder.getPath().replaceFirst("^" + pathFolderOld, pathFolderNew);
                subfolder.setPath(updatedPath);
                queue.add(subfolder);
            }
        }

        folderRepository.save(folder);
    }

    public FolderEntity findByPathAndUserLogin(String path, String login) {
        return folderRepository.findByPathAndUserLogin(path, login).orElse(null);
    }

    public List<FolderEntity> findByUserLogin(String username) {
        return folderRepository.findByUserLogin(username);
    }

    public List<FolderEntity> findByUserLoginAndParentEqualsNull(String username) {
        return folderRepository.findByUserLoginAndParentEqualsNull(username);
    }



}
