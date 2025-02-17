package test.shop.domain;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    public String getCreatedDate() {
        return createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public LocalDate getLocalDate() {
        return LocalDate.parse(getCreatedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public String getLastModifiedDate() {
        return lastModifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
