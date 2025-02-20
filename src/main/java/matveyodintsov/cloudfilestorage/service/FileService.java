package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.repository.FileRepository;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserService userService;
    private final FolderService folderService;
    private final CloudService cloudService;

    @Autowired
    public FileService(FileRepository fileRepository, UserService userService, FolderService folderService, CloudService cloudService) {
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.folderService = folderService;
        this.cloudService = cloudService;
    }

    @Transactional
    public void insert(MultipartFile file, String path) {
        String login = SecurityUtil.getSessionUser();
        String filePath = path + file.getOriginalFilename();

        cloudService.insertFile(file, filePath);

        FolderEntity folder = folderService.findByPathAndUserLoginOrElseNull(path, login);

        FileEntity fileEntity = new FileEntity();
        fileEntity.setUser(userService.findByLogin(login));
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setFolder(folder);
        fileEntity.setSize(file.getSize());

        fileRepository.save(fileEntity);
    }

    public InputStream download(String filePath) {
        return cloudService.downloadFile(filePath);
    }

    public InputStream downloadSelected(List<String> filePaths) {
        return cloudService.downloadSelectedFiles(filePaths);
    }

    public void rename(String oldName, String newName, String path) {
        FileEntity file;
        if (path.isEmpty()) {
            file = fileRepository.findByNameAndFolderIsNullAndUserLogin(oldName, SecurityUtil.getSessionUser());
        } else {
            file = fileRepository.findByNameAndFolderPathAndUserLogin(oldName, path, SecurityUtil.getSessionUser());
        }

        String oldPath = path + oldName;
        String newPath = path + newName;
        cloudService.renameFile(oldPath, newPath);

        file.setName(newName);
        fileRepository.save(file);
    }

    @Transactional
    public void delete(String path, String filename) {
        cloudService.deleteFile(path + filename);

        FileEntity file;
        if (path.isEmpty()) {
            file = fileRepository.findByNameAndFolderIsNullAndUserLogin(filename, SecurityUtil.getSessionUser());
        } else {
            file = fileRepository.findByNameAndFolderPathAndUserLogin(filename, path, SecurityUtil.getSessionUser());
        }

        fileRepository.delete(file);
    }

    public List<FileEntity> findByFolder(FolderEntity folder) {
        return fileRepository.findByFolder(folder);
    }

    public List<FileEntity> findByUserLoginAndFolderEqualsNull(String login) {
        return fileRepository.findByUserLoginAndFolderEqualsNull(login);
    }

    public BigDecimal getSizeRepository(String login) {
        Long bytes =  fileRepository.getCloudSizeByUserLogin(login);
        BigDecimal bigDecimal = new BigDecimal(bytes);
        return bigDecimal.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
    }

}