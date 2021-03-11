package swetter.model.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import swetter.model.db.Role;
import swetter.model.db.User;
import swetter.model.repo.UserRepo;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user) {
        User userFromDb =  userRepo.findByUsername(user.getUsername());

        // смотрим есть ли пользователь в базе
        // если найден отдадим true
        if (userFromDb != null) {
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        // генерируем уникальное значение ссылки для перехода и подтверждения почты
        user.setActivationCode(UUID.randomUUID().toString());
        userRepo.save(user);

        // проверяем есть указан ли у пользователя мейл (не равен ли он нул и не пуст ли он)
        if (!StringUtils.isEmpty(user.getEmail())) {

        }

        return true;
    }
}
