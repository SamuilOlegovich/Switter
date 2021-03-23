package switter.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import switter.model.service.UserService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // для того чтобы работал доступ админа
// в классе UserController и анотация к нему @PreAuthorize("hasAuthority('ADMIN')")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                // к главной странице мы разрешаем полный досту, для всех остальных закрываем
//                .antMatchers("/", "/home").permitAll()
                    .antMatchers("/",
                            "/registration",
                            "/static/**",
                            "/activate/*").permitAll()
                    .anyRequest().authenticated()
                .and()
                // включаем форму логин и разрешаем пользоватся всем
                    .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                // для памяти пользователя даже если он куда-то ушел и потом вернулся
                // другими словами самоавторизация
                    .rememberMe()
                .and()
                // включаем форму логоут и разрешаем пользоватся всем
                    .logout()
                    .permitAll();
    }


    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService)
                // шифрует пароль - NoOpPasswordEncoder - только для тестирования
//                .passwordEncoder(NoOpPasswordEncoder.getInstance());
                // в реальности же вставляем --> passwordEncoder
                .passwordEncoder(passwordEncoder);
    }

}