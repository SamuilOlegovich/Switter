package switter.controller;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import switter.model.db.Message;
import switter.model.db.User;
import switter.model.db.dto.MessageDto;
import switter.model.repo.MessageRepo;
import switter.model.service.FileUploader;
import switter.model.service.MessageService;

import java.io.IOException;
import java.util.Set;



@Controller()
public class EditMessageController {
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private FileUploader fileUploader;
    @Autowired
    private MessageService messageService;






    // страница редактирования сообщений
    @GetMapping("/user-messages/{author}")
    public String userMessages(
            // берет пользователя из сессии
            @AuthenticationPrincipal User currentUser,
            // смотрит какого юзера мы запрашиваем
            @PathVariable User author,
            Model model,
            // для отображения выбраных сообщений
            @RequestParam(required = false) Message message,
            @PageableDefault(sort = { "id" }, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        System.out.println("1");
        // получаем список сообщений юзера и кладем их в модель
        Page<MessageDto> page = messageService.messageListForUser(pageable, currentUser, author);

        // для подписок
        model.addAttribute("userChannel", author);
        model.addAttribute("subscriptionsCount", author.getSubscriptions().size());
        model.addAttribute("subscribersCount", author.getSubscribers().size());
        // определяет является  ли текущий пользователь подписчиком того пользователя на чью страницу он зашел
        model.addAttribute("isSubscriber", author.getSubscribers().contains(currentUser));

        model.addAttribute("page", page);
        model.addAttribute("message", message);
        // отображаем сообщения только если пользователь выбрал свои сообщения
        // обязательно переопределить в юзере иквелс и хашкод
        model.addAttribute("isCurrentUser", currentUser.equals(author));
        model.addAttribute("url", "/user-messages/" + author.getId());
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
        System.out.println("2");
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



    // удалить сообщение
    @GetMapping("/delete/{author}/{messageId}")
    public String deleteUserMessages(
            // берет пользователя из сессии
            @AuthenticationPrincipal User currentUser,
            // айди юзера и айди сообщения
            @PathVariable long author,
            @PathVariable long messageId
    ) {
        messageRepo.deleteById(messageId);
        return "redirect:/user-messages/" + author;
    }



    // лайки
    @GetMapping("/messages/{message}/like")
    public String like(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Message message,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {
        Set<User> likes = message.getLikes();

        if (likes.contains(currentUser)) {
            likes.remove(currentUser);
        } else {
            likes.add(currentUser);
        }

        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();

        components.getQueryParams()
                .entrySet()
                .forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));
        return "redirect:" + components.getPath();
    }
}
