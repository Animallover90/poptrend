package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.BlogComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpringDataJpaBlogCommentRep extends JpaRepository<BlogComment, Long> {

    Long countBlogCommentByBlog_Id(Long id);
    Long deleteBlogCommentById(Long id);

}
