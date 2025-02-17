package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    private final FolderRepository folderRepository;
    private final FileService fileService;

    @Autowired
    public FolderService(FolderRepository folderRepository, FileService fileService) {
        this.folderRepository = folderRepository;
        this.fileService = fileService;
    }


}
