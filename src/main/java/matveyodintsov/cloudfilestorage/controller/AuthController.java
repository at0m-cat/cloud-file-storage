package matveyodintsov.cloudfilestorage.controller;

import matveyodintsov.cloudfilestorage.dto.UserRegisterDto;
import matveyodintsov.cloudfilestorage.security.SecurityUtil;
import matveyodintsov.cloudfilestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller()
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage() {
        if (SecurityUtil.getSessionUser() != null) {
            return "redirect:/";
        } else {
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (SecurityUtil.getSessionUser() != null) {
            return "redirect:/";
        } else {
            model.addAttribute("user", new UserRegisterDto());
            return "auth/register";
        }
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserRegisterDto userRegisterDto) {
        String login = userRegisterDto.getLogin();
        String password = userRegisterDto.getPassword();
        String repeatPassword = userRegisterDto.getRepeatPassword();

        if (!password.equals(repeatPassword)) {
            return "/auth/register-error";
        }

        if (userService.existsByLogin(login)) {
            return "/auth/register-error";
        }

        userService.save(userRegisterDto);

        return "/auth/register-successfully";
    }

}
