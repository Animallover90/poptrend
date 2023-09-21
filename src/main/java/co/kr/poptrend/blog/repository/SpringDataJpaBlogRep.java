package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface SpringDataJpaBlogRep extends JpaRepository<Blog, Long> {

    Long deleteBlogById(Long id);

    Page<Blog> findAllByTagOrderByCreatedDateDesc(String tag, Pageable page);

    Page<Blog> findAllByContentContainingIgnoreCaseOrTitleContainingIgnoreCaseOrTagContainingIgnoreCaseOrderByCreatedDateDesc(String content, String title, String tag, Pageable page);

    List<Blog> findAllByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday);

}
