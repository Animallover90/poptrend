package co.kr.poptrend.blog.entity;

import lombok.Getter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
public class BlogResponseDTO {

    private Long id;
    private String blogTitle;
    private String[] blogContent;
    private String blogSource;
    private String blogTag;
    private String blogDate;
    private int blogLikes;
    private List<CommentResponseDTO> comments;

    public BlogResponseDTO(Blog blog) {
        this.id = blog.getId();
        this.blogTitle = blog.getTitle();
        this.blogContent = spitContent(blog.getContent());
        this.blogSource = blog.getSource();
        this.blogTag = blog.getTag();
        this.blogDate = blog.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.blogLikes = blog.getBlogFavorites().size();
        this.comments = convertDTO(blog.getBlogComments());
    }

    public BlogResponseDTO(BlogFavorite blogFavorite) {
        this.id = blogFavorite.getBlog().getId();
        this.blogTitle = blogFavorite.getBlog().getTitle();
        this.blogContent = spitContent(blogFavorite.getBlog().getContent());
        this.blogSource = blogFavorite.getBlog().getSource();
        this.blogTag = blogFavorite.getBlog().getTag();
        this.blogDate = blogFavorite.getBlog().getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm"));
        this.blogLikes = blogFavorite.getBlog().getBlogFavorites().size();
        this.comments = convertDTO(blogFavorite.getBlog().getBlogComments());
    }

    public BlogResponseDTO() {

    }

    public BlogResponseDTO(String blogTitle, String[] blogContent) {
        this.blogTitle = blogTitle;
        this.blogContent = blogContent;
    }

    private List<CommentResponseDTO> convertDTO(List<BlogComment> blogComments) {
        List<CommentResponseDTO> commentResponseDTOList = new ArrayList<>();
        for (BlogComment blogComment : blogComments) {
            commentResponseDTOList.add(new CommentResponseDTO(blogComment));
        }
        return commentResponseDTOList;
    }

    private String[] spitContent(String content) {
        String[] split = content.split("/");
        return split;
    }
}
