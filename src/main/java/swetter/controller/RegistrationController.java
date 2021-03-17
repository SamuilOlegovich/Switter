package swetter.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import swetter.model.db.User;
import swetter.model.db.dto.CaptchaResponseDto;
import swetter.model.service.UserService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;



@Controller
public class RegistrationController {
    // для капчи
    private static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    @Autowired
    private UserService userService;
    // берем данные из application.properties
    @Value("${recaptcha.secret}")
    private String secret;
    @Autowired
    private RestTemplate restTemplate;



    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }



    @PostMapping("/registration")
    public String addUser(@RequestParam("password2") String passwordConfirm,
                          // для капчи
                          @RequestParam("g-recaptcha-response") String captchaResponce,
                          @Valid User user,
                          BindingResult bindingResult,
                          Model model) {

        // для капчи готовим URL и делаем ПОСЕ запрос
        String url = String.format(CAPTCHA_URL, secret, captchaResponce);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        // проверяем ответ успешно ли прошли капчу
        if (!response.isSuccess()) {
            model.addAttribute("captchaError", "Fill captcha");
        }

//        boolean isConfirmEmpty = StringUtils.isEmpty(passwordConfirm); // в оригинале так (не работает StringUtils.isEmpty)
        boolean isConfirmEmpty = Strings.isEmpty(passwordConfirm);

        if (isConfirmEmpty) {
            model.addAttribute("password2Error", "Password confirmation cannot be empty");
        }

        // проверяем сзодятсяли два пароля которые юзер ввел при регистрации
        if (user.getPassword() != null && !user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("passwordError", "Passwords are different!");
        }

        // проверяем на ошибки валидации
        if (isConfirmEmpty || bindingResult.hasErrors() || !response.isSuccess()) {
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
        // подробности вывода цвета на бутстремпе - https://getbootstrap.com/docs/4.1/components/buttons/
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
