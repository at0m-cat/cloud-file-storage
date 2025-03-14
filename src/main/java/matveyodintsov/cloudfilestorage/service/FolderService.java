package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

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

    public InputStream download(String folderPath) {
        return cloudService.downloadFolder(folderPath);
    }

//    @Transactional
//    public void insert(List<MultipartFile> files, String path) {
//        Set<String> createdFolders = new HashSet<>();
//
//        for (MultipartFile file : files) {
//            String originalPath = file.getOriginalFilename();
//            String[] pathSegments = originalPath.split("/");
//            String fileName = pathSegments[pathSegments.length - 1];
//
//            String currentPath = path;
//            String currentFolder = null;
//
//            for (int i = 0; i < pathSegments.length - 1; i++) {
//                currentFolder = pathSegments[i];
//                String newPath = currentPath + currentFolder + "/";
//
//                if (!createdFolders.contains(newPath)) {
//                    create(currentPath, currentFolder);
//                    createdFolders.add(newPath);
//                }
//                currentPath = newPath;
//            }
//
//            //todo: придумать как создать файл, нельзя создать fileService
//
//            cloudService.insertFile(file, currentPath + fileName);
//        }
//
//    }

    @Transactional
    public void create(String path, String folderName) {
        String login = SecurityUtil.getSessionUser();
        UserEntity user = userService.findByLogin(login);

        FolderEntity parent = folderRepository.findByPathAndUserLogin(path, SecurityUtil.getSessionUser())
                .orElse(null);
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
    public void delete(String path, String folderName) {
        String login = SecurityUtil.getSessionUser();
        String pathFolder = path + folderName + "/";
        FolderEntity folder = folderRepository.findByPathAndUserLogin(pathFolder, login)
                .orElseThrow(() -> new RuntimeException("Папка не найдена"));

        cloudService.deleteFolder(path + folderName);

        folderRepository.delete(folder);
    }

    public void rename(String oldName, String newName, String path) {
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

    public FolderEntity findByPathAndUserLoginOrElseNull(String path, String login) {
        return folderRepository.findByPathAndUserLogin(path, login).orElse(null);
    }

    public List<FolderEntity> findByNameLikeAndUserLogin(String folderName, String login) {
        return folderRepository.findByNameLikeAndUserLogin(folderName, login);
    }

    public List<FolderEntity> findByUserLoginAndParentEqualsNull(String username) {
        return folderRepository.findByUserLoginAndParentEqualsNull(username);
    }

}
