package switter.model.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;


// делаем конфиг для правильной работы JavaMailSender - иначе не работает нормально
// следует не забыть добавить настройки в application.properties  -->
// spring.mail.host=smtp.yandex.ru
// spring.mail.username=uw@dru.su
// spring.mail.password=12345678
// spring.mail.port=465
// spring.mail.protocol=smtps
// mail.debug=true
@Configuration
public class MailConfig {
    // получаем данные из application.properties
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String userName;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.protocol}")
    private String protocol;
    @Value("${mail.debug}")
    private String debug;



    @Bean
    public JavaMailSender getMailSender () {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        // далее устанавливаем не явные настройки, вначале получаем Properties у JavaMailSenderImpl
        Properties properties = javaMailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", protocol);
        // не обязательно, но на всякий случай если
        // что-то не так он нам об этом сообщит в логах
        // в продакшине лучше отключать эту перременную
        properties.setProperty("mail.debug", debug);

        return javaMailSender;
    }

}
