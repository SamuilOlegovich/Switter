package swetter.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import swetter.model.db.Message;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


@Service
public class FileUploader {
    // ищет и вставляет из проперти
    // --> upload.path=/Users/samuilolegovich/Documents/JAVA/Download
    @Value("${upload.path}")
    private String uploadPath;



    public void saveFile(Message message, MultipartFile file) throws IOException {
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
    }
}
