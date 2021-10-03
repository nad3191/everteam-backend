package com.intalio.intaliosyed.controller;


import com.intalio.intaliosyed.dto.ExcelFileDetailDTO;
import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.row.Row;
import com.intalio.intaliosyed.service.ExcelService;
import com.intalio.intaliosyed.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.intalio.intaliosyed.util.CommonUtil.headers;

@Slf4j
@RestController
@RequestMapping(RowController.PATH)
@CrossOrigin(origins="*", maxAge=3600)
public class RowController {
    public static final String PATH = "/api/excel-files/{file-id}/rows";

    private final ExcelService excelService;
    private final ExcelUtil excelUtil;

    public RowController(ExcelService excelService, ExcelUtil excelUtil) {
        this.excelService = excelService;
        this.excelUtil = excelUtil;
    }

    /**
     * Fetch paginated rows
     */
    @GetMapping
    public ResponseEntity<ExcelFileDetailDTO> fetchRows(@RequestHeader(required = false, defaultValue = "1", value = "page") int page,
                                                        @RequestHeader(required = false, defaultValue = "30", value = "size") int size,
                                                        @PathVariable("file-id") final Integer fileId) {
        Page<AbstractRow> p = excelService.findRows(fileId, page - 1, size);
        return ResponseEntity.ok().headers(headers(p)).body(excelUtil.transformWithRows(p.getContent()));
    }

    /**
     * Fetch paginated rows
     */
    @CrossOrigin
    @RequestMapping(value = {"", "/"}, method = RequestMethod.OPTIONS)
    public ResponseEntity<ExcelFileDetailDTO> rows(@RequestHeader(required = false, defaultValue = "1", value = "page") int page,
                                                        @RequestHeader(required = false, defaultValue = "30", value = "size") int size,
                                                        @PathVariable("file-id") final Integer fileId) {
        return fetchRows(page, size, fileId);
    }

    /**
     * Add a row
     */
    @PostMapping
    public ResponseEntity<ExcelFileDetailDTO> addRow(@RequestHeader(required = false, defaultValue = "1") int page,
                                                     @RequestHeader(required = false, defaultValue = "30") int size,
                                                     @PathVariable("file-id") final Integer fileId,
                                                     @RequestBody ExcelFileDetailDTO.Row row) {

        excelService.addRow(fileId, row);
        return fetchRows(page, size, fileId);
    }

    /**
     * Update a row
     */
    @PostMapping("/{row-id}/update")
    public ResponseEntity<ExcelFileDetailDTO> update(@RequestHeader(required = false, defaultValue = "1") int page,
                                                     @RequestHeader(required = false, defaultValue = "30") int size,
                                                     @PathVariable("file-id") final Integer fileId,
                                                     @PathVariable("row-id") Long rowId,
                                                     @RequestBody ExcelFileDetailDTO.Row row) {

        excelService.updateRow(fileId, rowId, row);
        return fetchRows(page, size, fileId);
    }

    /**
     * Delete a row
     */
    @DeleteMapping("/{row-id}")
    public ResponseEntity<ExcelFileDetailDTO> deleteRow(@RequestHeader(required = false, defaultValue = "1") int page,
                                                        @RequestHeader(required = false, defaultValue = "30") int size,
                                                        @PathVariable("file-id") final Integer fileId,
                                                        @PathVariable("row-id") final Long rowId) {
        excelService.deleteRow(fileId, rowId);
        return fetchRows(page, size, fileId);
    }
}
