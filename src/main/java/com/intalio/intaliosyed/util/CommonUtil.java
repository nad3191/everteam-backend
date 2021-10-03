package com.intalio.intaliosyed.util;

import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import com.intalio.intaliosyed.entity.row.DraftRow;
import com.intalio.intaliosyed.entity.row.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CommonUtil {

    static String SHEET = "Sheet1";

    public static HttpHeaders headers(Page<?> p) {
        int totalPages = p.getTotalPages();
        int pageNumber = p.getPageable().getPageNumber() + 1;
        int pageSize = p.getPageable().getPageSize();
        HttpHeaders header = new HttpHeaders();
        header.add("X-Total-Pages", String.valueOf(totalPages));
        header.add("X-Page-Number", String.valueOf(pageNumber));
        header.add("X-Page-Size", String.valueOf(pageSize));
        return header;
    }

    public static Collection<Row> transform(InputStream is, ExcelFile file) {
        try {
            final List<Row> abstractRows = new ArrayList<>();
            try (Workbook workbook = new XSSFWorkbook(is)) {

                Sheet sheet = workbook.getSheet(SHEET);
                Iterator<org.apache.poi.ss.usermodel.Row> rows = sheet.iterator();

                int rowNumber = 0;
                while (rows.hasNext()) {
                    org.apache.poi.ss.usermodel.Row currentRow = rows.next();

                    // skip header
                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cellsInRow = currentRow.iterator();

                    final Row row = new Row();
                    row.setFile(file);

                    int cellIdx = 0;
                    while (cellsInRow.hasNext()) {
                        Cell currentCell = cellsInRow.next();

                        switch (cellIdx) {
                            case 0:
                                if (StringUtils.hasLength(currentCell.getStringCellValue())) {
                                    row.setFileName(currentCell.getStringCellValue());
                                }
                                break;

                            case 1:
                                if (StringUtils.hasLength(currentCell.getStringCellValue())) {
                                    row.setFileType(currentCell.getStringCellValue());
                                }
                                break;

                            case 2:
                                if (StringUtils.hasLength(currentCell.getStringCellValue())) {
                                    row.setFileLocation(currentCell.getStringCellValue());
                                }
                                break;

                            case 3:
                                if (StringUtils.hasLength(currentCell.getStringCellValue())) {
                                    row.setFileLastModifiedOn(currentCell.getStringCellValue());
                                }
                                break;

                            case 4:
                                if (StringUtils.hasLength(currentCell.getStringCellValue())) {
                                    row.setFileModifiedBy(currentCell.getStringCellValue());
                                }
                                break;

                            default:
                                break;
                        }

                        cellIdx++;
                    }

                    abstractRows.add(row);
                }

            }

            return abstractRows;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
