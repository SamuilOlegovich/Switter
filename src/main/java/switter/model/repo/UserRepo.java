package switter.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import switter.model.db.User;



public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByActivationCode(String code);
}
