package matveyodintsov.cloudfilestorage.repository;

import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories(basePackageClasses = FileEntity.class)
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findByUserLogin(String login);

    List<FileEntity> findByFolder(FolderEntity folder);

    @Query("SELECT f FROM FileEntity f WHERE f.user.login =:login AND f.folder IS NULL")
    List<FileEntity> findByUserLoginAndFolderEqualsNull(@Param("login") String login);

    FileEntity findByNameAndFolderPathAndUserLogin(String filename, String path, String login);

    FileEntity findByNameAndFolderIsNullAndUserLogin(String filename, String login);

}
