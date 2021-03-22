package switter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import switter.model.db.Message;
import switter.model.db.User;
import switter.model.repo.MessageRepo;
import switter.model.service.FileUploader;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;



@Controller
public class MainController {
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private FileUploader fileUploader;




    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greet";
    }



    @GetMapping("/main")
    public String main(@RequestParam(required = false, defaultValue = "") String filter,
                       Model model,
                       // (пагинация)
                       // делаем выборку и сортировку сообщений (самые первые будут показаны которые самые новые)
                       @PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Message> page;

        if (filter != null && !filter.isEmpty()) {
            page = messageRepo.findByTag(filter, pageable);
        } else {
            page = messageRepo.findAll(pageable);
        }

        model.addAttribute("page", page);
        model.addAttribute("url", "/main");
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
            fileUploader.saveFile(message, file);
            // чтобы форма ввода закрылась
            model.addAttribute("message", null);
            messageRepo.save(message);
        }

        Iterable<Message> messages = messageRepo.findAll();
        model.addAttribute("messages", messages);
        return "main";
    }
}
