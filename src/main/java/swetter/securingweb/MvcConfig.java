package swetter.securingweb;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class MvcConfig implements WebMvcConfigurer {
    // ищет и вставляет из проперти
    // --> upload.path=/Users/samuilolegovich/Documents/JAVA/Download
    @Value("${upload.path}")
    private String uploadPath;




    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/home").setViewName("home");
//        registry.addViewController("/").setViewName("home");
//        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
    }


    // для раздачи файла
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file://" + uploadPath + "/");
        // чтобы раздавать статический контент без авторизации
        registry.addResourceHandler("/static/**")
                // classpath --> ищет папку в дереве проекта
                .addResourceLocations("classpath:/static/");
    }
}
