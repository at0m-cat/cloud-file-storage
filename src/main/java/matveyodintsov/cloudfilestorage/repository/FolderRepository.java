package matveyodintsov.cloudfilestorage.repository;

import matveyodintsov.cloudfilestorage.models.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;

@EnableJpaRepositories(basePackageClasses = FolderEntity.class)
@Repository
public interface FolderRepository extends JpaRepository<FolderEntity, Long> {

    List<FolderEntity> findByUserLogin(String login);

    FolderEntity findByName(String folderName);

}
