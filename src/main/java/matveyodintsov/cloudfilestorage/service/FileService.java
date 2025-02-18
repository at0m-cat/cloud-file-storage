package matveyodintsov.cloudfilestorage.service;

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

    private final FileRepository fileRepository;
    private final UserService userService;
    private final FolderService folderService;
    private final MinioService minioService;

    @Autowired
    public FileService(FileRepository fileRepository, UserService userService, FolderService folderService, MinioService minioService) {
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.folderService = folderService;
        this.minioService = minioService;
    }

    public void insertFile(MultipartFile file, String path) {
        String login = SecurityUtil.getSessionUser();
        String filePath = path + file.getOriginalFilename();

        minioService.insertFile(file, filePath);

        FolderEntity folder = folderService.findByPathAndUserLogin(path, login);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setUser(userService.findByLogin(login));
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setFolder(folder);
        fileEntity.setSize(file.getSize());

        fileRepository.save(fileEntity);
    }

    public InputStream download(String filePath) {
        return minioService.downloadFile(filePath);
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

    private void save(FileEntity fileEntity) {
        fileRepository.save(fileEntity);
    }

}