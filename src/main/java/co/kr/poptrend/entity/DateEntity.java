package co.kr.poptrend.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass   //상속 받은 클래스에서 부모 값을 컬럼으로 사용할 수 있다.
@EntityListeners(AuditingEntityListener.class) //JPA의 Auditing 기능이고 최상위 실행 클래스에 @EnableJpaAuditing을 선언해야함
public abstract class DateEntity {

    @Column(name = "created_date", nullable = false, updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate    // Entity가 생성될 때 시간
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate   // 변경이 일어날 때 수정되는 값
    private LocalDateTime modifiedDate;
}