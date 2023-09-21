package co.kr.poptrend.blog.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class CommentResponseDTO {

    private Long id;
    private String commentNickname;
    private List<String> commentContent;
    private String commentProfile;
    private String commentDate;
    private int commentLikes;


    public CommentResponseDTO(BlogComment comments) {
        this.id = comments.getId();
        this.commentNickname = comments.getMember().getNickName();
        this.commentContent = splitString(comments.getContent());
        this.commentProfile = (comments.getMember().getProfileImg() == null) ? "/images/guest.png" : comments.getMember().getProfileImg();
        this.commentDate = comments.getCreatedDate().format(DateTimeFormatter.ofPattern("MM-dd hh:mm"));
        this.commentLikes = comments.getCommentFavorites().size();
    }

    public CommentResponseDTO() {

    }

    private List<String> splitString(String comment) {
        List<String> stringList = new ArrayList<>();
        int splitSize = 25;
        int length = comment.length();
        if (splitSize > length) {
            stringList.add(comment);
            return stringList;
        }
        int count = comment.length() / splitSize;
        int index = 0;
        for (int i = 0; i < count; i ++) {
            stringList.add(comment.substring(index, index+splitSize));
            index += splitSize;
        }
        int elseLength = comment.length() % splitSize;
        stringList.add(comment.substring(comment.length()-elseLength));
        return stringList;
    }
}
