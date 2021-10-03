package com.intalio.intaliosyed.entity.excel;

import com.intalio.intaliosyed.entity.row.DraftRow;
import com.intalio.intaliosyed.entity.row.Row;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Table
@Entity(name = "excel_files")
@Data
public class ExcelFile extends AbstractExcelFile {

    @Column(name = "draft_file_id")
    private Integer draftFileId;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private Collection<Row> rows;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL)
    private Collection<DraftRow> draftRows;

}
