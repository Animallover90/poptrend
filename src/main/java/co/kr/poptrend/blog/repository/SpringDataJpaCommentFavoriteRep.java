package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.CommentFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SpringDataJpaCommentFavoriteRep extends JpaRepository<CommentFavorite, Long> {
    Long deleteCommentFavoriteById(Long id);
    CommentFavorite findCommentFavoriteByMember_IdAndBlogComment_Id(Long memberId, Long commentId);
    Long countCommentFavoritesByBlogComment_Id(Long id);
}
