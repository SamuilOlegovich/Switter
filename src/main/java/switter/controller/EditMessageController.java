package switter.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import switter.model.db.Message;
import switter.model.db.User;
import switter.model.repo.MessageRepo;
import switter.model.service.FileUploader;

import java.io.IOException;
import java.util.Set;




@Controller()
public class EditMessageController {
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private FileUploader fileUploader;


    // страница редактирования сообщений
    @GetMapping("/user-messages/{user}")
    public String userMessages(
            // берет пользователя из сессии
            @AuthenticationPrincipal User currentUser,
            // смотрит какого юзера мы запрашиваем
            @PathVariable(name = "user") User user,
            Model model,
            // для отображения выбраных сообщений
            @RequestParam(required = false) Message message
    ) {
        // получаем список сообщений юзера и кладем их в модель
        Set<Message> messages = user.getMessages();

        // для подписок
        model.addAttribute("userChannel", user);
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        // определяет является  ли текущий пользователь подписчиком того пользователя на чью страницу он зашел
        model.addAttribute("isSubscriber", user.getSubscribers().contains(currentUser));

        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        // отображаем сообщения только если пользователь выбрал свои сообщения
        // обязательно переопределить в юзере иквелс и хашкод
        model.addAttribute("isCurrentUser", currentUser.equals(user));
        return "userMessages";
    }



    // сохраняем отредактированные сообщения
    @PostMapping("/user-messages/{user}")
    public String updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long user,
            @RequestParam("id") Message message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        // чтобы пользователь мог менять только свои сообщения
        if (message.getAuthor().equals(currentUser)) {
            // если поля текст и тег не пустые - тогда обновляем их
            if (!Strings.isEmpty(text)) { message.setText(text); }
            if (!Strings.isEmpty(tag)) { message.setTag(tag); }
            // для загрузки файла
            fileUploader.saveFile(message, file);
            // сохраняем обновления
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + user;
    }
}
