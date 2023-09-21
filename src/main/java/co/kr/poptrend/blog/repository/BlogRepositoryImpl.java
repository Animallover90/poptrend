package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BlogRepositoryImpl implements BlogRepository {

    private final SpringDataJpaBlogRep brep;
    private final SpringDataJpaBlogFavoriteRep bfrep;
    private final SpringDataJpaBlogCommentRep bcrep;
    private final SpringDataJpaCommentFavoriteRep cfrep;
    private final SpringDataJpaSearchRep srep;

    @Override
    public Long saveBlog(Blog blog) {
        brep.save(blog);
        return blog.getId();
    }

    @Override
    public Optional<Blog> findBlogById(Long id) {
        return brep.findById(id);
    }

    @Override
    public Page<Blog> pagingBlogAllOrderDESC(Pageable page) {
        return brep.findAll(page);
    }

    @Override
    public Page<Blog> findAllByTagOrderByCreatedDateDesc(String tag, Pageable page) {
        return brep.findAllByTagOrderByCreatedDateDesc(tag, page);
    }

    @Override
    public Page<Blog> findAllByContentOrTitleOrTagContainingIgnoreCaseOrderByCreatedDateDesc(String content, String title, String tag, Pageable page) {
        return brep.findAllByContentContainingIgnoreCaseOrTitleContainingIgnoreCaseOrTagContainingIgnoreCaseOrderByCreatedDateDesc(content, title, tag, page);
    }

    @Override
    public List<Blog> findAllByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday) {
        List<Blog> allByCreatedDateBeforeOrderByCreatedDateDesc = brep.findAllByCreatedDateBeforeOrderByCreatedDateDesc(yesterday);

        return allByCreatedDateBeforeOrderByCreatedDateDesc;
    }

    @Override
    public Long deleteBlogById(Long id) {
        return brep.deleteBlogById(id);
    }

    @Override
    public Long saveBlogFavorite(BlogFavorite blogFavorite) {
        bfrep.save(blogFavorite);
        return blogFavorite.getId();
    }

    @Override
    public Optional<BlogFavorite> findBlogFavoriteById(Long id) {
        return bfrep.findById(id);
    }

    @Override
    public List<BlogFavorite> findBlogFavoritesByBlogId(Long id) {
        return bfrep.findBlogFavoritesByBlog_Id(id);
    }

    @Override
    public Page<BlogFavorite> pagingBlogFavoriteAllByMemberIdOrderBlogDESC(Long id, Pageable page) {
        return bfrep.findAllByMember_IdOrderByBlogDesc(id, page);
    }

    @Override
    public List<BlogFavorite> findBlogFavoritesByMemberId(Long id) {
        return bfrep.findBlogFavoritesByMember_Id(id);
    }

    @Override
    public BlogFavorite findBlogFavoriteByMemberIdAndBlogId(Long memberId, Long blogId) {
        return bfrep.findBlogFavoriteByMember_IdAndBlog_Id(memberId, blogId);
    }

    @Override
    public Long countBlogFavoritesByBlogId(Long id) {
        return bfrep.countBlogFavoritesByBlog_Id(id);
    }

    @Override
    public Long deleteBlogFavoriteById(Long id) {
        return bfrep.deleteBlogFavoriteById(id);
    }

    @Override
    public Long saveBlogComment(BlogComment blogComment) {
        bcrep.save(blogComment);
        return blogComment.getId();
    }

    @Override
    public Optional<BlogComment> findBlogCommentById(Long id) {
        return bcrep.findById(id);
    }

    @Override
    public Long countBlogCommentByBlogId(Long id) {
        return bcrep.countBlogCommentByBlog_Id(id);
    }

    @Override
    public Long deleteBlogCommentById(Long id) {
        return bcrep.deleteBlogCommentById(id);
    }

    @Override
    public Long saveCommentFavorite(CommentFavorite commentFavorite) {
        cfrep.save(commentFavorite);
        return commentFavorite.getId();
    }

    @Override
    public Optional<CommentFavorite> findCommentFavoriteById(Long id) {
        return cfrep.findById(id);
    }

    @Override
    public CommentFavorite findCommentFavoriteByMemberIdAndBlogCommentId(Long memberId, Long commentId) {
        return cfrep.findCommentFavoriteByMember_IdAndBlogComment_Id(memberId, commentId);
    }

    @Override
    public Long countCommentFavoritesByBlogCommentId(Long id) {
        return cfrep.countCommentFavoritesByBlogComment_Id(id);
    }

    @Override
    public Long deleteCommentFavoriteById(Long id) {
        return cfrep.deleteCommentFavoriteById(id);
    }

    @Override
    public Long saveSearch(Search search) {
        Search save = srep.save(search);
        return save.getId();
    }

    @Override
    public Optional<Search> findSearchByTerm(String term) {
        return srep.findSearchByTerm(term);
    }

    @Override
    public List<Search> findTop5ByCreatedDateAfterOrderByCountDesc(LocalDateTime yesterday) {
        return srep.findTop5ByCreatedDateAfterOrderByCountDesc(yesterday);
    }

    @Override
    public List<Search> findAllSearchByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday) {
        return srep.findAllByCreatedDateBeforeOrderByCreatedDateDesc(yesterday);
    }

    @Override
    public Long deleteSearchById(Long id) {
        return srep.deleteSearchById(id);
    }

}

