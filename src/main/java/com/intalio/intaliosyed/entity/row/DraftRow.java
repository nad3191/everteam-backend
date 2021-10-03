package com.intalio.intaliosyed.entity.row;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "draft_rows")
public class DraftRow extends AbstractRow {

    @Column(name = "row_id")
    private Long rowId;

}