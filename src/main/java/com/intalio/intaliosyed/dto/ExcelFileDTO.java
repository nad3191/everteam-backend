package com.intalio.intaliosyed.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ExcelFileDTO {
    private Integer id;
    private String name;
    private String status;
    private String createdOn;
    private String modifiedOn;
}
