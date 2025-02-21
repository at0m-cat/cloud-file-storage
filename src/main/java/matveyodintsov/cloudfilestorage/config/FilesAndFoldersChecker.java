package matveyodintsov.cloudfilestorage.config;

import matveyodintsov.cloudfilestorage.config.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.service.FileService;
import matveyodintsov.cloudfilestorage.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilesAndFoldersChecker {

    private FileService fileService;
    private FolderService folderService;

    @Autowired
    public FilesAndFoldersChecker(FileService fileService, FolderService folderService) {
        this.fileService = fileService;
        this.folderService = folderService;
    }

    public void checkFileNameOrThrow(String path, String filename) {
        FolderEntity folder = folderService.findByPathAndUserLoginOrElseNull(path, SecurityUtil.getSessionUser());

        if (folder == null) {
            List<FileEntity> filesNoFolder = fileService.findByUserLoginAndFolderEqualsNull(SecurityUtil.getSessionUser());
            for (FileEntity file : filesNoFolder) {
                if (filename.equals(file.getName())) {
                    throw new RuntimeException("Файл с таким именем уже существует!");
                }
            }
        } else {
            for (FileEntity filesFolder : folder.getFiles()) {
                if (filename.equals(filesFolder.getName())) {
                    throw new RuntimeException("Файл с таким именем уже существует!");
                }
            }
        }
    }

    public void checkFolderNameOrThrow(String path, String foldername) {
        FolderEntity parentFolder = folderService.findByPathAndUserLoginOrElseNull(path, SecurityUtil.getSessionUser());
        if (parentFolder == null) {
            List<FolderEntity> foldersNoParent = folderService.findByUserLoginAndParentEqualsNull(SecurityUtil.getSessionUser());
            for (FolderEntity folders : foldersNoParent) {
                if (foldername.equals(folders.getName())) {
                    throw new RuntimeException("Папка с таким именем уже существует!");
                }
            }

        } else {
            for (FolderEntity subFolders : parentFolder.getSubfolders()) {
                if (foldername.equals(subFolders.getName())) {
                    throw new RuntimeException("Папка с таким именем уже существует!");
                }
            }
        }
    }

}
