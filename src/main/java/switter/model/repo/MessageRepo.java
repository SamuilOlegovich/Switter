package switter.model.repo;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import switter.model.db.Message;
import switter.model.db.User;
import switter.model.db.dto.MessageDto;



public interface MessageRepo extends CrudRepository<Message, Long> {
//public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("select new switter.model.db.dto.MessageDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from Message m left join m.likes ml " +
            "group by m")
        // так же следует модицицировать findAll чтобы он принимал Page - аргумент
    Page<MessageDto> findAll(Pageable pageable, @Param("user") User user);


    @Query("select new switter.model.db.dto.MessageDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from Message m left join m.likes ml " +
            "where m.tag = :tag " +
            "group by m")
        // Pageable - для ускорения загрузки страницы когда много сообщений (пагинация)
        // как итог будет возвращатся не List - a Page
    Page<MessageDto> findByTag(@Param("tag") String tag, Pageable pageable, @Param("user") User user);


    @Query("select new switter.model.db.dto.MessageDto(" +
            "   m, " +
            "   count(ml), " +
            "   sum(case when ml = :user then 1 else 0 end) > 0" +
            ") " +
            "from Message m left join m.likes ml " +
            "where m.author = :author " +
            "group by m")
    Page<MessageDto> findByUser(Pageable pageable, @Param("author") User author, @Param("user") User user);

//    Message findById(long id);

}
