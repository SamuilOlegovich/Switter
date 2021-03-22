package switter.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@RunWith(SpringRunner.class)    // указываем окружение которое будет стартовать нажи тесты
@SpringBootTest                 // указывает что мы запускаем тесты в окружении SpringBoot приложения
@AutoConfigureMockMvc           // Spring автоматически создает структуру классов которая подменяет слой MVC из фреймворка
                                // данам более удобный метод тестирования приложения (тест происходит в "фейковом" окружении)
                                // это быстрее проще и контролируемей --> нам не надо создавать RestTemplate,
                                // мы просто можем использовать МОКнутую версию нашего MVC слоя
@TestPropertySource("/application-test.properties")
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;



    @Test
    public void contextLoads() throws Exception {
        // вызываем у МОК перформ - указываем что хотим выполнить ГЕТ запрос на главную страницу проэкта
        this.mockMvc.perform(get("/"))
                // print() - выводит полученый результат в консоль
                .andDo(print())
                // andExpect - аналог(обертка) метода assertThat
                // ожидаем что вернется статус http GET запроса 200
                .andExpect(status().isOk())
                // проверяем что у нас вернется какой-то контент - строку
                // и сравниваем, что эта строка содержит в себе подстроку ("Hello, guest")
                .andExpect(content().string(containsString("Hello, guest")))
                // а так же ("Please, login")
                .andExpect(content().string(containsString("Please, login")));
    }


    @Test
    public void accessDeniedTest() throws Exception {
        // проверяем проверку авторизации
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                // ожидаем 300 статус и нас перепрвляет на страницу логина
                .andExpect(status().is3xxRedirection())
                // проверяем что система нам подкинет нужный адресс ("http://localhost/login")
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @Sql(value = {"/create-user-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = {"/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void correctLoginTest() throws Exception {
        // полверяем авторизацию пользователя
        // formLogin() - смотрит как определена ЛогинПайдж
        // и вызывает собственное обращение к этой странице
        // далее вводим user("99") имя пользователя и password("99") пароль
        this.mockMvc.perform(formLogin().user("99").password("99"))
                // смотрим что пришел корректный ответ от сервера
                .andExpect(status().is3xxRedirection())
                // нас переведут на главную страницу сайта redirectedUrl("/")
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void badCredentials() throws Exception {
        // проверяет отбивку на неправильные данные пользователя (логин или пароль неправильный)
        // для примера делаем это не через метод formLogin() - а ручками через методы и адреса
        this.mockMvc.perform(post("/login").param("username", "jonh"))
                .andDo(print())
                // ожидаем 403 статус
                .andExpect(status().isForbidden());
    }

}
