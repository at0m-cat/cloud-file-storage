package matveyodintsov.cloudfilestorage.repository;

import matveyodintsov.cloudfilestorage.models.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories(basePackageClasses = FileEntity.class)
@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

}
