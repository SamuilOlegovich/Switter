package switter.model.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import switter.model.db.Message;

import java.util.List;



public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByTag(String tag);
}
