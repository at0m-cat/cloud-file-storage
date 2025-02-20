package matveyodintsov.cloudfilestorage.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(Model model) {
        model.addAttribute("error", "Страница не найдена");
        return "error/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(NoResourceFoundException.class)
    public String handleResourceException(Model model) {
        model.addAttribute("error", "Страница не найдена");
        return "error/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ResourceAccessException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("error", "У вас нет доступа к ресурсу");
        return "error/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/error";
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("error", "Ошибка 500");
        model.addAttribute("message", "Произошла ошибка на сервере");
        return "error/error";
    }

}
