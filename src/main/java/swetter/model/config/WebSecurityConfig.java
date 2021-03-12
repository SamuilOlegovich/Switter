package swetter.model.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import swetter.model.service.UserService;



@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // для того чтобы работал доступ админа
// в классе UserController и анотация к нему @PreAuthorize("hasAuthority('ADMIN')")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
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
                // включаем форму логоут и разрешаем пользоватся всем
                    .logout()
                    .permitAll();
    }

//    @Override
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.jdbcAuthentication()
//                // позволяет ходить в бд и искать пользователей их роли
//                .dataSource(dataSource)
//                // шифрует пароль - NoOpPasswordEncoder - только для тестирования
//                .passwordEncoder(NoOpPasswordEncoder.getInstance())
//                // запросы - чтобы система могла найти пользователя по его имени
//                .usersByUsernameQuery("select username, password, active from usr where username=?")
//                // помогает получить список пользователей с их ролями
//                .authoritiesByUsernameQuery("select u.username, ur.roles from usr u inner join user_role ur on u.id = ur.user_id where u.username=?");
//    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService)
                // шифрует пароль - NoOpPasswordEncoder - только для тестирования
                .passwordEncoder(NoOpPasswordEncoder.getInstance());
    }


    //    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        // ничего не шифрует ни чего не хранит, нужен для отладки
//        // заново при каждом входе создает пользователя
//        UserDetails user =
//                User.withDefaultPasswordEncoder()
//                        .username("u")
//                        .password("i")
//                        .roles("USER")
//                        .build();
//
//        return new InMemoryUserDetailsManager(user);
//    }
}