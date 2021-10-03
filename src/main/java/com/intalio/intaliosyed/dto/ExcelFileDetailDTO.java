package com.intalio.intaliosyed.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
@JsonPropertyOrder({"id", "name", "rows"})
public class ExcelFileDetailDTO {

    private Integer Id;
    private String name;
    private Collection<Row> rows;

    @Data
    @Builder
    public static class Row {
        private Long id;
        private String fileName;
        private String fileType;
        private String fileLocation;
        private String fileLastModifiedOn;
        private String fileModifiedBy;

        public com.intalio.intaliosyed.entity.row.Row toRow() {
            final com.intalio.intaliosyed.entity.row.Row r = new com.intalio.intaliosyed.entity.row.Row();
            r.setFileType(getFileType());
            r.setFileName(getFileName());
            r.setFileLocation(getFileLocation());
            r.setFileLastModifiedOn(getFileLastModifiedOn());
            r.setFileModifiedBy(getFileModifiedBy());
            return r;
        }

        public com.intalio.intaliosyed.entity.row.DraftRow toDraftRow() {
            final com.intalio.intaliosyed.entity.row.DraftRow r = new com.intalio.intaliosyed.entity.row.DraftRow();
            r.setFileType(getFileType());
            r.setFileName(getFileName());
            r.setFileLocation(getFileLocation());
            r.setFileLastModifiedOn(getFileLastModifiedOn());
            r.setFileModifiedBy(getFileModifiedBy());
            return r;
        }
    }


}
