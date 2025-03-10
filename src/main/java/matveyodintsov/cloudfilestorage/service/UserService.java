package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserRegisterDto userRegisterDto) throws RuntimeException {
        if (existsByLogin(userRegisterDto.getLogin())) {
            throw new RuntimeException("Пользователь уже зарегистрирован");
        }

        UserEntity userEntity = mapToEntity(userRegisterDto);
        userRepository.save(userEntity);
    }

    public boolean existsByLogin(String login) {
        return userRepository.existsByLoginIgnoreCase(login);
    }

    public UserEntity findByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserEntity mapToEntity(UserRegisterDto userRegisterDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(userRegisterDto.getLogin());
        userEntity.setPassword(encryptPassword(userRegisterDto.getPassword()));
        return userEntity;
    }

    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }


}
