package matveyodintsov.cloudfilestorage.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDto userRegisterDto) {
        String login = userRegisterDto.getLogin();
        if (userRepository.existsByLogin(login)) {
            return ResponseEntity.badRequest().body("Login already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(userRegisterDto.getLogin());
        userEntity.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        userRepository.save(userEntity);
        return ResponseEntity.ok("User registered successfully");
    }


}
