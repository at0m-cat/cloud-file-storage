package matveyodintsov.cloudfilestorage.repository;

import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import matveyodintsov.cloudfilestorage.api.MinioApi;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Repository
public class CloudRepository {

    private final MinioApi minioApi;

    public CloudRepository(MinioClient minioClient) {
        this.minioApi = new MinioApi(minioClient);
    }

    public void insertFile(MultipartFile file, String filePath) {
        try {
            minioApi.createBucketOrElseVoid();
            minioApi.putObject(file, filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении файла: " + e.getMessage(), e);
        }
    }

    public void createFolder(String folderPath) {
        try {
            minioApi.createBucketOrElseVoid();
            minioApi.putObject(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании папки: " + e.getMessage(), e);
        }
    }

    public void renameFile(String oldPath, String newPath) {
        try {
            minioApi.copy(oldPath, newPath);
            deleteFile(oldPath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании файла: " + e.getMessage(), e);
        }
    }

    public void renameFolder(String oldPath, String newPath) {
        try {
            createFolder(newPath);

            for (Result<Item> result : minioApi.listObjects(oldPath)) {
                String oldObjectPath = result.get().objectName();
                String newObjectPath = oldObjectPath.replace(oldPath, newPath);
                minioApi.copy(oldObjectPath, newObjectPath);
            }
            deleteFolder(oldPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при переименовании папки: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String filePath) {
        try {
            minioApi.delete(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла: " + e.getMessage(), e);
        }
    }

    public void deleteFolder(String folderPath) {
        try {

            for (Result<Item> result : minioApi.listObjects(folderPath)) {
                minioApi.delete(result.get().objectName());
            }
            deleteFile(folderPath);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении папки: " + e.getMessage(), e);
        }
    }

    public InputStream getObject(String filePath) throws Exception {
        return minioApi.getObject(filePath);
    }

    public Iterable<Result<Item>> listObjects(String path){
        return minioApi.listObjects(path);
    }

}
