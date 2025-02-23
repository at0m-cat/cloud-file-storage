package matveyodintsov.cloudfilestorage;

import matveyodintsov.cloudfilestorage.models.FileEntity;
import matveyodintsov.cloudfilestorage.models.FolderEntity;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.FileRepository;
import matveyodintsov.cloudfilestorage.repository.FolderRepository;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudFileStorageApplicationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15-alpine")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");
        postgres.start();
    }

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    public void fillingDatabase() {
        UserEntity user = new UserEntity();
        user.setLogin("user-1");
        user.setPassword("password");
        userRepository.save(user);

        UserEntity user2 = new UserEntity();
        user2.setLogin("user-2");
        user2.setPassword("password2");
        userRepository.save(user2);

        FolderEntity folder = new FolderEntity();
        folder.setName("secret-folder");
        folder.setUser(user);
        folder.setPath("/");
        folderRepository.save(folder);

        FileEntity file = new FileEntity();
        file.setName("java.txt");
        file.setUser(user);
        file.setFolder(null);
        file.setSize(1252L);
        fileRepository.save(file);

        FileEntity file2 = new FileEntity();
        file2.setName("java.txt");
        file2.setUser(user2);
        file2.setFolder(null);
        file2.setSize(12241L);
        fileRepository.save(file2);

        FileEntity file3 = new FileEntity();
        file3.setName("test.exe");
        file3.setUser(user);
        file3.setFolder(folder);
        file3.setSize(122223L);
        fileRepository.save(file3);
    }

    @AfterEach
    public void clearDatabase() {
        fileRepository.deleteAll();
        folderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testCreateUserWhenUserExists() {
        UserEntity user = new UserEntity();
        user.setLogin("user-1");
        user.setPassword("password");
        assertThrows(RuntimeException.class, () -> userRepository.save(user));
    }

}