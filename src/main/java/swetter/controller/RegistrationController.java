package swetter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import swetter.model.db.User;
import swetter.model.service.UserService;

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
    public String addUser(User user, Map<String, Object> model) {
        // закомментированый код перенесли в отдельный метод в UserService
        if (!userService.addUser(user)) {
            model.put("message", "User exists!");
            return "registration";
        }
//        user.setActive(true);
//        user.setRoles(Collections.singleton(Role.USER));
//        userRepo.save(user);
        return  "redirect:/login";
    }
}
