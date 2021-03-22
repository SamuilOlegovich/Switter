package switter.model.repo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import switter.model.db.Message;

import java.util.List;



public interface MessageRepo extends JpaRepository<Message, Long> {
    // Pageable - для ускорения загрузки страницы когда много сообщений (пагинация)
    // как итог будет возвращатся не List - a Page
    Page<Message> findByTag(String tag, Pageable pageable);
    // так же следует модицицировать findAll чтобы он принимал Page - аргумент
    Page<Message> findAll(Pageable pageable);

}
