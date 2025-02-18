package matveyodintsov.cloudfilestorage.repository;

import matveyodintsov.cloudfilestorage.models.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories(basePackageClasses = FolderEntity.class)
@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Long> {

    List<FolderEntity> findByUserLogin(String login);

    Optional<FolderEntity> findByName(String folderName);

    @Query("SELECT f FROM FolderEntity f WHERE f.user.login = :login AND f.parent IS NULL")
    List<FolderEntity> findByUserLoginAndParentEqualsNull(@Param("login") String login);

    Optional<FolderEntity> findByPathAndUserLogin(String path, String login);

}
