package switter.model.db.dto;


import switter.model.db.Message;
import switter.model.db.User;
import switter.model.db.util.MessageHelper;



public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String filename;
    private Long likes;
    private Boolean meLiked;



    public MessageDto(Message message, Long likes, Boolean meLiked) {
        this.filename = message.getFilename();
        this.author = message.getAuthor();
        this.text = message.getText();
        this.tag = message.getTag();
        this.id = message.getId();
        this.meLiked = meLiked;
        this.likes = likes;
    }



    public String getAuthorName() {
        return MessageHelper.getAuthorName(author);
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public User getAuthor() {
        return author;
    }

    public String getFilename() {
        return filename;
    }

    public Long getLikes() {
        return likes;
    }

    public Boolean getMeLiked() {
        return meLiked;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", author=" + author +
                ", likes=" + likes +
                ", meLiked=" + meLiked +
                '}';
    }
}
