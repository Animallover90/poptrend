package co.kr.poptrend.blog.repository;


import co.kr.poptrend.blog.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlogRepository {
    Long saveBlog(Blog blog);

    Optional<Blog> findBlogById(Long id);

    Page<Blog> pagingBlogAllOrderDESC(Pageable page);

    Page<Blog> findAllByTagOrderByCreatedDateDesc(String tag, Pageable page);

    Page<Blog> findAllByContentOrTitleOrTagContainingIgnoreCaseOrderByCreatedDateDesc(String content, String title, String tag, Pageable page);

    List<Blog> findAllByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday);

    Long deleteBlogById(Long id);

    Long saveBlogFavorite(BlogFavorite blogFavorite);

    Optional<BlogFavorite> findBlogFavoriteById(Long id);

    List<BlogFavorite> findBlogFavoritesByBlogId(Long id);

    Page<BlogFavorite> pagingBlogFavoriteAllByMemberIdOrderBlogDESC(Long id, Pageable page);

    List<BlogFavorite> findBlogFavoritesByMemberId(Long id);

    BlogFavorite findBlogFavoriteByMemberIdAndBlogId(Long memberId, Long blogId);

    Long countBlogFavoritesByBlogId(Long id);

    Long deleteBlogFavoriteById(Long id);

    Long saveBlogComment(BlogComment blogComment);

    Optional<BlogComment> findBlogCommentById(Long id);

    Long countBlogCommentByBlogId(Long id);

    Long deleteBlogCommentById(Long id);

    Long saveCommentFavorite(CommentFavorite commentFavorite);

    Optional<CommentFavorite> findCommentFavoriteById(Long id);

    CommentFavorite findCommentFavoriteByMemberIdAndBlogCommentId(Long memberId, Long commentId);

    Long countCommentFavoritesByBlogCommentId(Long id);

    Long deleteCommentFavoriteById(Long id);

    Long saveSearch(Search search);

    Optional<Search> findSearchByTerm(String term);

    List<Search> findTop5ByCreatedDateAfterOrderByCountDesc(LocalDateTime yesterday);

    List<Search> findAllSearchByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday);

    Long deleteSearchById(Long id);
}
