package swetter.controller;

import antlr.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import swetter.model.db.User;
import swetter.model.service.UserService;

import javax.validation.Valid;
import java.util.Map;


@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;



    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }



    @PostMapping("/registration")
    public String addUser(@RequestParam("passwordTwo") String passwordConfirm,
            @Valid User user,
            BindingResult bindingResult,
            Model model) {
//        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm); // в оригинале так (не работает StringUtils.isEmpty)
        boolean isConfirmEmpty = Strings.isEmpty(passwordConfirm);

        if (isConfirmEmpty) {
            model.addAttribute("passwordTwoError", "Password confirmation cannot be empty");

        }
        // проверяем сзодятсяли два пароля которые юзер ввел при регистрации
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords are different!");
        }

        // проверяем на ошибки валидации
        if (isConfirmEmpty || bindingResult.hasErrors()) {
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if (!userService.addUser(user)) {
            model.addAttribute("usernameError", "User exists!");
            return "registration";
        }

        return "redirect:/login";
    }



    // не забыть открыть мепинг activate для незарегестрированных пользователей
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        // ожет быть как успешная так и нет активация
        // (в зависимости от успеха или нет будет выводится разный цвет сообщения)
        // подробности вывода цвета на бутстремпе в описании
        if (isActivated) {
            model.addAttribute("messageType", "success");
            model.addAttribute("message", "User successfully activated");
        } else {
            model.addAttribute("messageType", "danger");
            model.addAttribute("message", "Activation code is not found!");
        }

        return "login";
    }
}
