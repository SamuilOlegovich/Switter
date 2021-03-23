package switter.model.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Configuration
public class RestTemplateConfig {
    // для капчи под рест и не только
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
