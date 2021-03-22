package switter.controller;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;




@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails(value = "dru")                          // указывает под каким пользователем нам авторизоваться
@TestPropertySource("/application-test.properties")      // указывает где брать настройки для запуска
// чтобы тесты не упали, так как база (указанная в новых настройках) пуста нам нужно выполнить некоторые @SQL команды,
// а точнее файлы (create-user-before.sql, create-user-after.sql) которые накатываются в базу
// тоже делаем и для данных сообщений (messages-list-before.sql, messages-list-after.sql)
// при старте тестов выполняется очистка и заполнение базы данными (накатку лучше начинать с очистки, а то мало ли что там...)
@Sql(value = {"/create-user-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
// после завершения тестов выполняется очистка базы данныз
@Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MainControllerTest {
    @Autowired
    private MainController controller;
    @Autowired
    private MockMvc mockMvc;



    @Test
    public void mainPageTest() throws Exception {
        this.mockMvc.perform(get("/main"))
                .andDo(print())
                // проверяет что пользователь был корректно аутетифицирован
                // для начала пользователя нужно авторизовать иначе тест упадет
                // для этого используем анатацию - @WithUserDetails
                // в которую передаем имя пользователя под которым будем выполнять данный тест
                .andExpect(authenticated())
                // в ответе ожидаем получить xpath с именем пользователя
                // одним словом вставляем "адрес" где мы ожидаем строку с именем
                .andExpect(xpath("//*[@id='navbarSupportedContent']/div").string("dru"));
    }

    @Test
    public void messageListTest() throws Exception {
        // проверяем корректное отображения списка сообщений
        this.mockMvc.perform(get("/main"))
                // выводим все в консоль
                .andDo(print())
                // проверяет что пользователь был корректно аутетифицирован
                .andExpect(authenticated())
                // в данном случаи ожидаем что xpath будет возвращать не строку, а количество узлов
                // во избежание всяких нюансов ... создадим тестовую (дополнительную) базу данных - swittertest
                // далее скопируем application-dev.properties из реальной папки в тестовую
                // и назовем его application-test.properties
                // об этих изменениях даем знать в анотации @TestPropertySource() и заполняем базы @Sql
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(4));
    }

    @Test
    public void filterMessageTest() throws Exception {
        // тестируем параметр фильтр поэтому добавляем гег запросу доп параметры - param()
        this.mockMvc.perform(get("/main").param("filter", "my-tag"))
                .andDo(print())
                // пользователь авторизован
                .andExpect(authenticated())
                // из наполнения базы мы знаем что таких элемента должно быть два - это и проверяем
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(2))
                // так же проверяем правильным ли айдишникам (юзерам пренадлежат эти сообщения с тегами)
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='1']").exists())
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='3']").exists());
    }

    @Test
    public void addMessageToListTest() throws Exception {
        // тестируем добавление элементов - multipart() - можем передать на страницу значения - заполним их
        MockHttpServletRequestBuilder multipart = multipart("/main")
                // вместо файла передадим строку в байтах
                .file("file", "123".getBytes())
                // далее передаем текст и тег
                .param("text", "fifth")
                .param("tag", "new one")
                // обязательно - это вместо токина авторизации
                .with(csrf());

        // теперь выполняем проверку
        this.mockMvc.perform(multipart)
                .andDo(print())
                // проверяем что пользователь авторизован
                .andExpect(authenticated())
                // после добавления у нас станет 5 сообщений - проверяем это
                .andExpect(xpath("//*[@id='message-list']/div").nodeCount(5))
                // айди будет 10, так как ранее мы дали знать это базе в sql запросах при наполнении базы
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']").exists())
                // так же сверяем текст сообщения и тег
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/span").string("fifth"))
                .andExpect(xpath("//*[@id='message-list']/div[@data-id='10']/div/i").string("#new one"));
    }
}
