package switter.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import switter.model.db.Role;
import switter.model.db.User;
import switter.model.service.UserService;

import java.util.Map;





@Controller
@RequestMapping("/user")
//@PreAuthorize("hasAuthority('ADMIN')") // проверяет наличие у пользовотеля права доступа
public class UserController {
    @Autowired
    private UserService userService;




    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    // перенеся сюда эту анатацию мы даем разрешение использовать этот метод всем авторизованным пользователям
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }



    @GetMapping("{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userEditForm(@PathVariable User user, Model model
    ) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "userEdit";
    }



    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    private String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userId") User user
    ) {

        userService.saveUser(user, username, form);
        return "redirect:/user";
    }



    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('ADMIN')")
    private String getProfile(Model model,
            // отдает пользователя из контекста (не приходится лишний раз лезть в базу данных)
            @AuthenticationPrincipal User user
    ) {

        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/profile")
    @PreAuthorize("hasAuthority('ADMIN')")
    private String updateProfile(@AuthenticationPrincipal User user,
                                 @RequestParam String password,
                                 @RequestParam String email
    ) {
        userService.updateProfile(user, password, email);
        return "redirect:/user/profile";
    }


    // дляподписок и подписчиков
    // подписаться
    @GetMapping("/subscribe/{user}")
    public String subscribe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user
    ) {
        userService.subscribe(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    // отписаться
    @GetMapping("/unsubscribe/{user}")
    public String unsubscribe(
            @AuthenticationPrincipal User currentUser,
            @PathVariable User user
    ) {
        userService.unsubscribe(currentUser, user);
        return "redirect:/user-messages/" + user.getId();
    }

    // список подписок или подписчики
    @GetMapping("{type}/{user}/list")
    public String userList(
            Model model,
            @PathVariable User user,
            @PathVariable String type
    ) {
        model.addAttribute("userChannel", user);
        model.addAttribute("type", type);

        if ("subscriptions".equals(type)) {
            // подписки
            model.addAttribute("users", user.getSubscriptions());
        } else {
            // подписчики
            model.addAttribute("users", user.getSubscribers());
        }
        return "subscriptions";
    }
}
