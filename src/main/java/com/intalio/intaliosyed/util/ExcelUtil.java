package com.intalio.intaliosyed.util;

import com.intalio.intaliosyed.common.FileType;
import com.intalio.intaliosyed.dto.ExcelFileDTO;
import com.intalio.intaliosyed.dto.ExcelFileDetailDTO;
import com.intalio.intaliosyed.entity.excel.AbstractExcelFile;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.row.DraftRow;
import com.intalio.intaliosyed.entity.row.Row;
import com.intalio.intaliosyed.service.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ExcelUtil {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = {"Name", "Age", "Gender", "Mobile", "Email"};

    private final ExcelService excelService;

    public ExcelUtil(ExcelService excelService) {
        this.excelService = excelService;
    }

    public boolean hasExcelFormat(@NonNull final MultipartFile file) {
        boolean valid = TYPE.equals(file.getContentType());
        log.info("input has valid format:[{}]", valid);
        return valid;
    }

    @Transactional
    public Integer create(@NonNull MultipartFile file) {

        if (!hasExcelFormat(file)) {
            throw new RuntimeException("Invalid File");
        }

        try {
            return buildAndSave(file);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Could not process the request");
        }
    }

    private Integer buildAndSave(MultipartFile file) throws IOException {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setName(file.getOriginalFilename());
        excelFile.setHasDraft(false);

        final Collection<Row> rows = CommonUtil.transform(file.getInputStream(), excelFile);
        excelFile.setRows(rows);

        final ExcelFile saved = excelService.save(excelFile);

        final List<DraftRow> draftRows = saved.getRows().stream().map(row -> {
            final DraftRow d = row.toDraftRow();
            d.setRowId(row.getId());
            return d;
        }).collect(Collectors.toList());
        excelFile.setDraftRows(draftRows);

        excelFile.setContent(file.getBytes());

        final Integer id = saved.getId();
        excelFile.setPath(excelService.getPath(id, FileType.ORIGINAL));

        return id;
    }

    public Collection<ExcelFileDTO> transform(Collection<ExcelFile> files) {
        return files.stream().map(ExcelFile::toDTO).collect(Collectors.toList());
    }

    public ExcelFileDetailDTO transformWithRows(Collection<AbstractRow> details) {

        AbstractExcelFile file = details.stream().findAny().map(AbstractRow::getFile).orElseThrow(() -> new RuntimeException("Data Not Found"));

        return ExcelFileDetailDTO.builder()
                .Id(file.getId())
                .name(file.getName())
                .rows(details.stream().map(AbstractRow::toDTO).collect(Collectors.toList()))
                .build();

    }
}
