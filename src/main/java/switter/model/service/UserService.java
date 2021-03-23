package switter.model.service;


import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import switter.model.db.Role;
import switter.model.db.User;
import switter.model.repo.UserRepo;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private MailSender mailSender;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${hostname}")
    private String hostname;




    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }



    public boolean addUser(User user) {
        User userFromDb =  userRepo.findByUsername(user.getUsername());
        // смотрим есть ли пользователь в базе
        // если найден отдадим true
        if (userFromDb != null) return false;

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        // генерируем уникальное значение ссылки для перехода и подтверждения почты
        user.setActivationCode(UUID.randomUUID().toString());
        // шифруем пароль при регистрации
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
//        sendMessage(user);
        return true;
    }



    public boolean activateUser(String code) {
        User user = userRepo.findByActivationCode(code);
        if (user == null) return false;
        // ставим нул что значит - пользователь подтвердился
        user.setActivationCode(null);
        userRepo.save(user);
        return true;
    }



    public List<User> findAll() { return userRepo.findAll(); }



    public void saveUser(User user, String username, Map<String, String> form) {
        user.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(user);
    }



    public void updateProfile(User user, String password, String email) {
        String userEmail = user.getEmail();
        // делаем так вместо ифа чтобы не словить нулл
        boolean isEmailChanged = ((email != null && !user.equals(userEmail))
                || (userEmail != null && !userEmail.equals(email)));

        if (isEmailChanged) {
            user.setEmail(email);
            // смотрим если мыло изменилось, то генерируем новый код активации
            if (!Strings.isEmpty(email)) {
                user.setActivationCode(UUID.randomUUID().toString());
            }
        }

        // проверяем установил ли пользователь новый пароль
        if (!Strings.isEmpty(password)) {
            user.setPassword(password);
        }
        // сохраняем обновленные данные юзера
        userRepo.save(user);
        // отсылаем повторно код активации нового мыла
        if (isEmailChanged) {
            sendMessage(user);
        }
    }


    private void sendMessage(User user) {
        // проверяем есть указан ли у пользователя мейл (не равен ли он нул и не пуст ли он)
        if (!Strings.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
//                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                            "Welcome to Sweater. Please, visit next link: http://%s/activate/%s",
                    user.getUsername(),
                    hostname,
                    user.getActivationCode()
            );
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }


    // для подписчиков и подписок
    // добавление
    public void subscribe(User currentUser, User user) {
        user.getSubscribers().add(currentUser);
        userRepo.save(user);
    }
    // удаление
    public void unsubscribe(User currentUser, User user) {
        user.getSubscribers().remove(currentUser);
        userRepo.save(user);
    }
}
