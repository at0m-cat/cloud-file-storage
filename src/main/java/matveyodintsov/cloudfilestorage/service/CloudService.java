package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.repository.CloudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class CloudService {

    private final CloudRepository cloudRepository;

    @Autowired
    public CloudService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public InputStream downloadFile(String filePath) {
        return cloudRepository.downloadFile(filePath);
    }

    public void createFolder(String folderPath) {
        cloudRepository.createFolder(folderPath);
    }

    public void insertFile(MultipartFile file, String filePath) {
        cloudRepository.insertFile(file, filePath);
    }

    public void renameFile(String oldPath, String newPath) {
        cloudRepository.renameFile(oldPath, newPath);
    }

    public void deleteFile(String filePath) {
        cloudRepository.deleteFile(filePath);
    }

    public void deleteFolder(String folderPath) {
        cloudRepository.deleteFolder(folderPath);
    }

}
