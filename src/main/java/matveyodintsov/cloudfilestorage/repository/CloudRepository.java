package matveyodintsov.cloudfilestorage.repository;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.api.MinioApi;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class CloudRepository extends MinioApi {

    public CloudRepository(MinioClient minioClient) {
        super(minioClient);
    }

    public void insertFile(MultipartFile file, String filePath) {
        try {
            createBucketOrElseVoid();
            putObject(file, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении файла: " + e.getMessage(), e);
        }
    }

    public void createFolder(String folderPath) {
        try {
            createBucketOrElseVoid();
            putObject(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
    }

    public void renameFile(String oldPath, String newPath) {
        try {
            copy(oldPath, newPath);
            deleteFile(oldPath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании файла: " + e.getMessage(), e);
        }
    }

    public void renameFolder(String oldPath, String newPath) {
        try {
            createFolder(newPath);

            for (Result<Item> result : listObjects(oldPath)) {
                String oldObjectPath = result.get().objectName();
                String newObjectPath = oldObjectPath.replace(oldPath, newPath);
                copy(oldObjectPath, newObjectPath);
            }
            deleteFolder(oldPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании папки: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            delete(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {

            for (Result<Item> result : listObjects(folderPath)) {
               delete(result.get().objectName());
            }
            deleteFile(folderPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении папки: " + e.getMessage(), e);
        }
    }

}
