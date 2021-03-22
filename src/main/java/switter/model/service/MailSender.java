package switter.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;



@Service
public class MailSender {
    // надо сделать конфиг для правильной работы JavaMailSender - иначе не работает нормально (MailConfig)
    @Autowired
    private JavaMailSender mailSender;

    // берется из --> application.properties
    @Value("${spring.mail.username}")
    private String userName;

    public void send(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject(subject);
        mailMessage.setFrom(userName);
        mailMessage.setText(message);
        mailMessage.setTo(emailTo);

        mailSender.send(mailMessage);
    }
}
