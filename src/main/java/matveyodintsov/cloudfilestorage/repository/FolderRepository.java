package matveyodintsov.cloudfilestorage.repository;

import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories(basePackageClasses = FolderEntity.class)
@Repository
public interface FolderRepository extends JpaRepository<FileEntity, Long> {


}
