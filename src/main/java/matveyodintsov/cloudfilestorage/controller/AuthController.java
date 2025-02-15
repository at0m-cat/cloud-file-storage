package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.models.UserEntity;
import matveyodintsov.cloudfilestorage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegisterDto userRegisterDto) {
        String login = userRegisterDto.getLogin();
        if (userRepository.existsByLogin(login)) {
            return "/auth/register-error";
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setLogin(userRegisterDto.getLogin());
        userEntity.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));

        userRepository.save(userEntity);
        return "/auth/register-successfully";
    }

}
