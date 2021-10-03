package com.intalio.intaliosyed.entity.row;

import com.intalio.intaliosyed.dto.ExcelFileDetailDTO;
import com.intalio.intaliosyed.entity.Auditable;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
@NoArgsConstructor
public class AbstractRow extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "file_location")
    private String fileLocation;
    @Column(name = "file_last_modified_on")
    private String fileLastModifiedOn;
    @Column(name = "file_modified_by")
    private String fileModifiedBy;

    @ManyToOne
    @JoinColumn(name = "file_id")
    private ExcelFile file;

    public ExcelFileDetailDTO.Row toDTO() {
        return ExcelFileDetailDTO.Row.builder().fileName(getFileName()).id(getId()).fileType(getFileType()).fileModifiedBy(getFileModifiedBy()).fileLastModifiedOn(getFileLastModifiedOn()).fileLocation(getFileLocation()).build();
    }

    public Row toRow() {
        final Row row = new Row();
        populate(row);
        return row;
    }

    public DraftRow toDraftRow() {
        final DraftRow row = new DraftRow();
        populate(row);
        return row;
    }

    public void populate(AbstractRow row) {
//        row.setId(getId());
        row.setFileName(getFileName());
        row.setFileType(getFileType());
        row.setFile(getFile());
        row.setFileLocation(getFileLocation());
        row.setFileLastModifiedOn(getFileLastModifiedOn());
        row.setFileModifiedBy(getFileModifiedBy());
    }
}