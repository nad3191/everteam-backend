package com.intalio.intaliosyed.controller;

import com.intalio.intaliosyed.dto.BaseResponse;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import com.intalio.intaliosyed.service.ExcelService;
import com.intalio.intaliosyed.util.CommonUtil;
import com.intalio.intaliosyed.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ExcelController.PATH)
public class ExcelController {

    public static final String PATH = "/api/excel-files";
    private final ExcelService excelService;
    private final ExcelUtil excelUtil;

    public ExcelController(ExcelService excelService, ExcelUtil excelUtil) {
        this.excelService = excelService;
        this.excelUtil = excelUtil;
    }

    /**
     * Upload new file which never exists before
     */
    @PostMapping
    public ResponseEntity<BaseResponse<Map<String, Object>>> create(@RequestParam("file") MultipartFile file) {
        Integer fileId = excelUtil.create(file);
        Map<String, Object> m = new HashMap<>(1);
        m.put("fileId", fileId);
        return new ResponseEntity<>(new BaseResponse<>(HttpStatus.CREATED.value(), m, true), HttpStatus.CREATED);
    }

    /**
     * Download the file
     */
    @GetMapping("/{file-id}")
    public ResponseEntity<Resource> download(@PathVariable("file-id") Integer fileId) throws IOException {
        File file = excelService.download(fileId);

        return ResponseEntity
                .ok()
                .header("File-Name", file.getName())
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new FileInputStream(file)));
    }

    /**
     * Fetch paginated files
     */
    @GetMapping
    public ResponseEntity<?> files(@RequestHeader(required = false, defaultValue = "1") int page, @RequestHeader(required = false, defaultValue = "30") int size) {
        Page<ExcelFile> p = excelService.findAll(page - 1, size);
        return ResponseEntity.ok().headers(CommonUtil.headers(p)).body(excelUtil.transform(p.getContent()));
    }

    /**
     * Final Save the file and remove draft
     */
    @PostMapping("{file-id}/save")
    public ResponseEntity<?> save(@PathVariable("file-id") Integer fileId) {
        excelService.saveFromDraft(fileId);
        return files(1, 30);
    }

    /**
     * Delete the file and corresponding rows
     */
    @DeleteMapping(value = "/{file-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable("file-id") Integer fileId) {
        excelService.deleteFile(fileId);
        return ResponseEntity.ok().body(String.format("{\"fileId\": %d}", fileId));
    }

}
