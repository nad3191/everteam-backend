package com.intalio.intaliosyed.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @Column(name = "created_on")
    @CreatedDate
    private LocalDateTime createdOn;
    @Column(name = "last_modified_on")
    @LastModifiedDate
    private LocalDateTime modifiedOn;
    @Column(name = "created_by")
    @CreatedBy
    private Long createdBy;
    @Column(name = "last_modified_by")
    @LastModifiedBy
    private Long modifiedBy;
}
