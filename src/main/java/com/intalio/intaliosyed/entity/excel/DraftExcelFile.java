package com.intalio.intaliosyed.entity.excel;

import com.intalio.intaliosyed.dto.ExcelFileDTO;
import com.intalio.intaliosyed.entity.row.Row;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "draft_excel_files")
@Data
public class DraftExcelFile extends AbstractExcelFile {
    private boolean hasDraft;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private Collection<Row> details;

    public ExcelFileDTO toDTO() {
        return ExcelFileDTO.builder().id(getId()).status(isHasDraft() ? "In Draft" : "Saved").name(getName()).build();
    }
}
