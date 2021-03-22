package switter.model.service;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import switter.model.db.Role;
import switter.model.db.User;
import switter.model.repo.UserRepo;

import java.util.Collections;


@RunWith(SpringRunner.class)    // указываем окружение которое будет стартовать нажи тесты
@SpringBootTest                 // указывает что мы запускаем тесты в окружении SpringBoot приложения
class UserServiceTest {
    @Autowired
    private UserService userService;
    // создаем для подмены ненужных для тестирования объектов,
    // но которые нужны для правильной работы проверяемого метода (которые используются (вызываются) в нем (методе))
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;



    @Test
    void addUserTest() {
        // создаем нового юзера
        User user = new User();
        // эмитируем отправку почты
        user.setEmail("ss@co.com");
        // добавляем его в базу
        boolean isUserCreated = userService.addUser(user);
        // проверяем на правду
        Assert.assertTrue(isUserCreated);
        // проверяем ято задан активационный код
        Assert.assertNotNull(user.getActivationCode());
        // проверяем что юзеру задана роль (приходит на помощь ханкрест)
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));
        // проверяем была ли отправка почты
        // для наало проверяемсчя что мок объект вызывается 1 раз и сходил по методу save() с аргументом user
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
        // проверяем что мейл сендеру було один раз переданы аргументы в метод send
        Mockito.verify(mailSender, Mockito.times(1))
                .send(
                        // используем аргументматчеры для дополнительных параметров
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }


    @Test
    void addUserFailTest() {
        // проверяем на дублирование пользователя,
        // когда повторно хоти зарегестрировать уже существующего пользователя
        User user = new User();
        user.setUsername("John");
        // теперь надо пояснить мокито что такой пользователь уже существует
        // doReturn - описываем какой нам результат надо получить
        // должны возвращать нового пользователя и указываем когда -
        // в частности когда вызывается у userRepo метод findByUsername() с аргументом "John"
        Mockito.doReturn(new User())
                .when(userRepo)
                .findByUsername("John");

        // приходит false
        boolean isUserCreated = userService.addUser(user);
        Assert.assertFalse(isUserCreated);
        // проверяем что при этом не отправляется ни каких сообщений и ничего не сохраняется в базу
        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }


    @Test
    public void activateUserTest() {
        User user = new User();
        // устанавливаем активационное слово
        user.setActivationCode("bingo!");

        // находим юзера и передаем ему "activate"
        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");

        boolean isUserActivated = userService.activateUser("activate");

        // проверяем что код активировае
        Assert.assertTrue(isUserActivated);
        // проверяем что поле активационного кода пустое пустое
        Assert.assertNull(user.getActivationCode());

        // проверка на сохранение пользователя
        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }


    @Test
    public void activateUserFailTest() {
        // проверяем на неактивацию юзера
        // репозиторий не вернет ни какого пользователя то такому коду
        boolean isUserActivated = userService.activateUser("activate me");

        Assert.assertFalse(isUserActivated);

        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));

    }
}