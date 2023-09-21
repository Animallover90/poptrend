package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.BlogFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SpringDataJpaBlogFavoriteRep extends JpaRepository<BlogFavorite, Long> {

    List<BlogFavorite> findBlogFavoritesByMember_Id(Long id);
    List<BlogFavorite> findBlogFavoritesByBlog_Id(Long id);
    Long deleteBlogFavoriteById(Long id);
    Page<BlogFavorite> findAllByMember_IdOrderByBlogDesc(Long id, Pageable page);
    BlogFavorite findBlogFavoriteByMember_IdAndBlog_Id(Long memberId, Long blogId);
    Long countBlogFavoritesByBlog_Id(Long id);
}
