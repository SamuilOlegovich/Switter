package swetter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import swetter.model.db.Message;
import swetter.model.db.User;
import swetter.model.repo.MessageRepo;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;
    // ищет и вставляет из проперти
    // --> upload.path=/Users/samuilolegovich/Documents/JAVA/Download
    @Value("${upload.path}")
    private String uploadPath;




    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greet";
    }



    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        Iterable<Message> messages = messageRepo.findAll();

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }

        model.addAttribute("messages", messages);
        model.addAttribute("filter", filter);
        return "main";
    }



    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            // список аргументов и сообщенией ошибок валидаций
            // (всегда должен идти перед аргуметом Model)
            BindingResult bindingResult,
            Model model,
            // для загрузки файла
            @RequestParam("file") MultipartFile file) throws IOException {
        message.setAuthor(user);

        // если имеются ошибки то выполняем обработку этого кейса
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        } else {
            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);
                // проверяем есть ли директория, если нет то создаем ее
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                // создаем уникальное имя файла
                String uuidFile = UUID.randomUUID().toString();
                // и к имени добавляем оригинальное название файла
                // которое изначально загрузил пользователь
                String resultFileName = uuidFile + "." + file.getOriginalFilename();
                // загружаем сам файл
                file.transferTo(new File(uploadPath + "/" + resultFileName));
                message.setFilename(resultFileName);
            }
            // чтобы форма ввода закрылась
            model.addAttribute("message", null);
            messageRepo.save(message);
        }

        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }



    // работал для проверки под mustache
//    @PostMapping("filter")
//    public String filter(@RequestParam String filter, Map<String, Object> model) {
//        Iterable<Message> messages;
//        if (filter != null && !filter.isEmpty()) {
//            messages = messageRepo.findByTag(filter);
//        } else {
//            messages = messageRepo.findAll();
//        }
//        model.put("messages", messages);
//        return "main";
//    }
}
