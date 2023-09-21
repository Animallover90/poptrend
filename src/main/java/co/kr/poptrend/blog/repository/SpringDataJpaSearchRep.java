package co.kr.poptrend.blog.repository;

import co.kr.poptrend.blog.entity.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface SpringDataJpaSearchRep extends JpaRepository<Search, Long> {

    Optional<Search> findSearchByTerm(String term);
    List<Search> findTop5ByCreatedDateAfterOrderByCountDesc(LocalDateTime yesterday);
    List<Search> findAllByCreatedDateBeforeOrderByCreatedDateDesc(LocalDateTime yesterday);
    Long deleteSearchById(Long id);

}
