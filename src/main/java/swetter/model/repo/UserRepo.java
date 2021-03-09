package swetter.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import swetter.model.db.User;



public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
