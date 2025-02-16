package matveyodintsov.cloudfilestorage.service;

import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService<UserRegisterDto> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(UserRegisterDto userRegisterDto) {
        UserEntity userEntity = mapToEntity(userRegisterDto);
        userRepository.save(userEntity);
    }

    @Override
    public boolean existsByLogin(String login) {
        return userRepository.existsByLogin(login);
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
