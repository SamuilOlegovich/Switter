package switter.model.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;



@Configuration
public class CustomConfig {

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        // 8 - надежность шифрования
        return new BCryptPasswordEncoder(8);
    }
}
