package com.intalio.intaliosyed.entity.excel;

import com.intalio.intaliosyed.dto.ExcelFileDTO;
import com.intalio.intaliosyed.entity.Auditable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class AbstractExcelFile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    @Lob
    private byte[] content;
    private String path;

    @Column(name = "has_draft")
    private boolean hasDraft;

    public ExcelFileDTO toDTO() {
        return ExcelFileDTO.builder().id(getId()).status(isHasDraft() ? "In Draft" : "Saved").name(getName()).createdOn(getCreatedOn().toLocalDate().toString()).modifiedOn(getModifiedOn().toString()).build();
    }
}
