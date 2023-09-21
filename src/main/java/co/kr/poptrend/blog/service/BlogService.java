package co.kr.poptrend.blog.service;

import co.kr.poptrend.bigquery.BigqueryService;
import co.kr.poptrend.blog.entity.*;
import co.kr.poptrend.blog.repository.BlogRepositoryImpl;
import co.kr.poptrend.error.CustomException;
import co.kr.poptrend.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Transactional // try catch로 잡아서 RuntimeException 이외의 것은 반환하지 말 것
@RequiredArgsConstructor
@Service
public class BlogService {

    private final BlogRepositoryImpl blogRepository;
    private final BigqueryService bigqueryService;
    private final int POST_COUNT_BY_PAGE = 6;

    public Blog saveBlog(Blog blog) {
        blogRepository.saveBlog(blog);
        return blog;
    }

    public Blog findBlogById(Long id) {
        Optional<Blog> blog = blogRepository.findBlogById(id);
        return blog.orElse(null);
    }

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> pagingBlogAllOrderDESC(int startPage) {
        startPage = Math.max(startPage - 1, 0);
        PageRequest pageRequest = PageRequest.of(startPage, POST_COUNT_BY_PAGE, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Blog> returnData = blogRepository.pagingBlogAllOrderDESC(pageRequest);
        return returnData.map(BlogResponseDTO::new).getContent();
    }

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> findAllByTagOrderByCreatedDateDesc(int startPage, String tag) {
        startPage = Math.max(startPage - 1, 0);
        PageRequest pageRequest = PageRequest.of(startPage, POST_COUNT_BY_PAGE);
        Page<Blog> returnData = blogRepository.findAllByTagOrderByCreatedDateDesc(tag, pageRequest);
        return returnData.map(BlogResponseDTO::new).getContent();
    }

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> findAllBySearchParamOrderByCreatedDateDesc(int startPage, String content, String title, String tag) {
        startPage = Math.max(startPage - 1, 0);
        PageRequest pageRequest = PageRequest.of(startPage, POST_COUNT_BY_PAGE);
        Page<Blog> returnData = blogRepository.
                findAllByContentOrTitleOrTagContainingIgnoreCaseOrderByCreatedDateDesc
                        (content, title, tag, pageRequest);
        return returnData.map(BlogResponseDTO::new).getContent();
    }

    public void delete1dayAgoBlogsDataAndBackup(LocalDateTime yesterday) {
        List<Blog> agoDataList = blogRepository.findAllByCreatedDateBeforeOrderByCreatedDateDesc(yesterday);
        if (agoDataList.isEmpty()) {
            return;
        }

        List<Map<String, Object>> blogMapData = new ArrayList<>();
        List<Map<String, Object>> blogCommentsMapData = new ArrayList<>();
        for (Blog b : agoDataList) {
            List<BlogComment> blogComments = b.getBlogComments();
            for (BlogComment bc : blogComments) {
                blogCommentsMapData.add(bc.convertMap());
            }

            Long aLong = blogRepository.deleteBlogById(b.getId());
            if (aLong != 1) {
                throw new CustomException(ErrorCode.UnExpectedError);
            }

            blogMapData.add(b.convertMap());
        }
        bigqueryService.insertDataInBigquery(blogMapData, "Blog");
        bigqueryService.insertDataInBigquery(blogCommentsMapData, "BlogComment");
    }

    @Transactional(readOnly = true)
    public List<BlogResponseDTO> pagingBlogFavoriteAllByMemberIdOrderBlogDESC(int startPage, Long id) {
        startPage = Math.max(startPage - 1, 0);
        PageRequest pageRequest = PageRequest.of(startPage, POST_COUNT_BY_PAGE);
        Page<BlogFavorite> returnData = blogRepository.pagingBlogFavoriteAllByMemberIdOrderBlogDESC(id, pageRequest);
        return returnData.map(BlogResponseDTO::new).getContent();
    }

    public BlogResponseDTO getBlogResponseDTO(Long id) {
        Optional<Blog> blogById = blogRepository.findBlogById(id);
        return blogById.map(BlogResponseDTO::new).orElse(null);
    }

    public Long deleteBlogById(Long id) {
        return blogRepository.deleteBlogById(id);
    }

    public BlogFavorite saveBlogFavorite(BlogFavorite blogFavorite) {
        blogRepository.saveBlogFavorite(blogFavorite);
        return blogFavorite;
    }

    public BlogFavorite findBlogFavoriteById(Long id) {
        return blogRepository.findBlogFavoriteById(id).orElse(null);
    }

    public List<BlogFavorite> findBlogFavoritesByBlogId(Long id) {
        return blogRepository.findBlogFavoritesByBlogId(id);
    }

    public List<BlogFavorite> findBlogFavoritesByMemberId(Long id) {
        return blogRepository.findBlogFavoritesByMemberId(id);
    }

    public BlogFavorite findBlogFavoriteByMemberIdAndBlogId(Long memberId, Long blogId) {
        return blogRepository.findBlogFavoriteByMemberIdAndBlogId(memberId, blogId);
    }

    public Long countBlogFavoritesByBlogId(Long id) {
        return blogRepository.countBlogFavoritesByBlogId(id);
    }

    public Long deleteBlogFavoriteById(Long id) {
        return blogRepository.deleteBlogFavoriteById(id);
    }

    public BlogComment saveBlogComment(BlogComment blogComment) {
        blogRepository.saveBlogComment(blogComment);
        return blogComment;
    }

    public BlogComment findBlogCommentById(Long id) {
        return blogRepository.findBlogCommentById(id).orElse(null);
    }

    public Long countBlogCommentByBlogId(Long id) {
        return blogRepository.countBlogCommentByBlogId(id);
    }

    public Long deleteBlogCommentById(Long id) {
        return blogRepository.deleteBlogCommentById(id);
    }

    public CommentFavorite saveCommentFavorite(CommentFavorite commentFavorite) {
        blogRepository.saveCommentFavorite(commentFavorite);
        return commentFavorite;
    }

    public CommentFavorite findCommentFavoriteById(Long id) {
        return blogRepository.findCommentFavoriteById(id).orElse(null);
    }

    public CommentFavorite findCommentFavoriteByMemberIdAndBlogCommentId(Long memberId, Long commentId) {
        return blogRepository.findCommentFavoriteByMemberIdAndBlogCommentId(memberId, commentId);
    }

    public Long countCommentFavoritesByBlogCommentId(Long id) {
        return blogRepository.countCommentFavoritesByBlogCommentId(id);
    }

    public Long deleteCommentFavoriteById(Long id) {
        return blogRepository.deleteCommentFavoriteById(id);
    }

    public void saveSearch(String term) {
        Optional<Search> searchByTerm = blogRepository.findSearchByTerm(term);
        if (searchByTerm.isPresent()) {
            searchByTerm.get().setCount(searchByTerm.get().getCount() + 1);
        } else {
            blogRepository.saveSearch(new Search(term));
        }
    }

    public List<Search> findTop5ByCreatedDateAfterOrderByCountDesc(LocalDateTime yesterday) {
        return blogRepository.findTop5ByCreatedDateAfterOrderByCountDesc(yesterday);
    }

    public void delete1dayAgoSearchDataAndBackup(LocalDateTime yesterday) {
        List<Search> searchList = blogRepository.findAllSearchByCreatedDateBeforeOrderByCreatedDateDesc(yesterday);
        if (searchList.isEmpty()) {
            return;
        }

        List<Map<String, Object>> searchMapData = new ArrayList<>();
        for (Search s : searchList) {
            Long aLong = blogRepository.deleteSearchById(s.getId());
            if (aLong != 1) {
                throw new CustomException(ErrorCode.UnExpectedError);
            }

            searchMapData.add(s.convertMap());
        }

        bigqueryService.insertDataInBigquery(searchMapData, "Search");
    }
}
