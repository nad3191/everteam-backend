package com.intalio.intaliosyed.entity.row;

import com.intalio.intaliosyed.dto.ExcelFileDetailDTO;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "file_rows")
@Data
public class Row extends AbstractRow {

}