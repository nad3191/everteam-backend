package com.intalio.intaliosyed.service;

import com.intalio.intaliosyed.common.FileType;
import com.intalio.intaliosyed.dto.ExcelFileDetailDTO;
import com.intalio.intaliosyed.entity.excel.DraftExcelFile;
import com.intalio.intaliosyed.entity.excel.ExcelFile;
import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.row.DraftRow;
import com.intalio.intaliosyed.entity.row.Row;
import com.intalio.intaliosyed.repository.DraftExcelFileRepository;
import com.intalio.intaliosyed.repository.DraftRowRepository;
import com.intalio.intaliosyed.repository.ExcelFileRepository;
import com.intalio.intaliosyed.repository.RowRepository;
import com.intalio.intaliosyed.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

@Slf4j
@Service
@PreAuthorize("hasAnyRole('MIGRATE, READ')")
public class ExcelService {

    private final DraftExcelFileRepository draftExcelFileRepository;
    private final ExcelFileRepository excelFileRepository;
    private final DraftRowRepository draftRowRepository;
    private final RowRepository rowRepository;
    private final SecurityUtil securityUtil;

    public ExcelService(DraftExcelFileRepository draftExcelFileRepository, ExcelFileRepository excelFileRepository, DraftRowRepository draftRowRepository, SecurityUtil securityUtil, RowRepository rowRepository) {
        this.draftExcelFileRepository = draftExcelFileRepository;
        this.excelFileRepository = excelFileRepository;
        this.draftRowRepository = draftRowRepository;
        this.securityUtil = securityUtil;
        this.rowRepository = rowRepository;
    }

    @Transactional
    public ExcelFile save(ExcelFile file) {
        return excelFileRepository.save(file);
    }

    @Transactional(readOnly = true)
    public Page<ExcelFile> findAll(int page, int size) {
        return excelFileRepository.findAllByCreatedBy(securityUtil.userDetails().orElseThrow(() -> new RuntimeException("User Not Found")).getId(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<AbstractRow> findRows(Integer fileId, int page, int size) {
        ExcelFile file = getFile(fileId);
        if (file.isHasDraft()) {
            return draftRowRepository.findByFileIdAndCreatedBy(fileId, securityUtil.getUserId(), PageRequest.of(page, size));
        }
        return rowRepository.findByFileIdAndCreatedBy(file.getId(), securityUtil.getUserId(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Optional<ExcelFile> findById(@NonNull Integer fileId) {
        return excelFileRepository.findById(fileId);
    }

    public File download(@NonNull Integer fileId) throws IOException {
        try {
            final Long userId = securityUtil.getUserId();
            ExcelFile file = getFile(fileId);
            if (file.isHasDraft()) {
                final DraftExcelFile draft = draftExcelFileRepository.findByIdAndCreatedBy(file.getDraftFileId(), userId).orElseThrow(() -> new RuntimeException("Draft Not Found"));
                FileUtils.writeByteArrayToFile(getFile(draft.getId(), draft.getName(), FileType.DRAFT), draft.getContent());
                return getFile(fileId, file.getName(), FileType.DRAFT);
            }
            FileUtils.writeByteArrayToFile(getFile(file.getId(), file.getName(), FileType.ORIGINAL), file.getContent());
            return getFile(fileId, file.getName(), FileType.ORIGINAL);
        } finally {
            removeFile(fileId);
        }
    }

    private void removeFile(Integer fileId) {
//        getFile(fileId).delete();
    }

    @Transactional
    public void upload(@NonNull final Integer fileId, MultipartFile excel, boolean isDraft) throws IOException {
        log.info("uploading file...");
        final Long userId = securityUtil.getUserId();
        //uploaded as draft
        ExcelFile mainFile = getFile(fileId);
        if (isDraft) {
            DraftExcelFile file = Optional.ofNullable(mainFile.getDraftFileId()).map(integer -> draftExcelFileRepository.findByIdAndCreatedBy(fileId, userId).orElseThrow(() -> new RuntimeException("Draft Not Found"))).orElse(new DraftExcelFile());
            mainFile.setHasDraft(true);
            try {
                file.setContent(excel.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Could not update file");
            }
            String path = getPath(fileId, FileType.DRAFT);
            file.setPath(path);
            try {
                writeByteArrayToFile(new File(path + mainFile.getName()), file.getContent());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            mainFile.setDraftFileId(draftExcelFileRepository.save(file).getId());
        } else {
            Optional.ofNullable(mainFile.getDraftFileId()).ifPresent(e -> {
                draftExcelFileRepository.deleteByIdAndCreatedBy(e, userId);
                mainFile.setHasDraft(false);
                mainFile.setDraftFileId(null);
            });
            try {
                mainFile.setContent(excel.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Could not update file");
            }
            String path = getPath(fileId, FileType.ORIGINAL);
            mainFile.setPath(path);
            try {
                writeByteArrayToFile(new File(path + mainFile.getName()), mainFile.getContent());
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw e;
            }
        }
        excelFileRepository.save(mainFile);
    }

    public String getPath(Integer fileId, FileType type) {
        return new StringJoiner("/").add("/tmp").add(securityUtil.getUserId().toString()).add(type.name().toLowerCase()).add(fileId.toString()).toString();
    }

    public File getFile(Integer fileId, String name, FileType type) {
        return new File(getPath(fileId, type) + "/" + name);
    }

    @Transactional
    public void deleteRow(@NonNull final Integer fileId, @NonNull final Long rowId) {
        final ExcelFile file = getFile(fileId);
        file.setHasDraft(true);

        draftRowRepository.deleteByIdAndFileIdAndCreatedBy(rowId, fileId, securityUtil.getUserId());
    }

    @Transactional
    public void addRow(@NonNull final Integer fileId, ExcelFileDetailDTO.Row row) {
        final ExcelFile file = getFile(fileId);
        file.setHasDraft(true);

        final DraftRow excelRow = row.toDraftRow();
        excelRow.setFile(file);
        draftRowRepository.save(excelRow);
    }

    @Transactional
    public void updateRow(@NonNull final Integer fileId, @NonNull final Long rowId, @NonNull final ExcelFileDetailDTO.Row row) {
        final ExcelFile file = getFile(fileId);
        file.setHasDraft(true);
        final DraftRow excelRow = draftRowRepository.findByIdAndFileIdAndCreatedBy(rowId, fileId, securityUtil.getUserId()).orElseThrow(() -> new RuntimeException("Row Not Found"));

        excelRow.setFileName(row.getFileName());
        excelRow.setFileType(row.getFileType());
        excelRow.setFileLocation(row.getFileLocation());
        excelRow.setFileLastModifiedOn(row.getFileLastModifiedOn());
        excelRow.setFileModifiedBy(row.getFileModifiedBy());

        draftRowRepository.save(excelRow);
    }

    @Transactional(readOnly = true)
    public ExcelFile getFile(@NonNull Integer fileId) {
        return getFile(fileId, true);
    }

    @Transactional(readOnly = true)
    public ExcelFile getFile(@NonNull Integer fileId, boolean checkUser) {
        if (checkUser) {
            return excelFileRepository.findByIdAndCreatedBy(fileId, securityUtil.getUserId()).orElseThrow(() -> new RuntimeException("File Not Found"));
        }
        return excelFileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File Not Found"));
    }

    @PreAuthorize("hasRole('MIGRATE')")
    @Transactional
    public void saveFromDraft(Integer fileId) {
        final ExcelFile file = getFile(fileId);

        final Collection<Row> rows = file.getRows();
        final Collection<DraftRow> draftRows = file.getDraftRows();

        final Collection<Row> toDeleteFromOriginal = rowRepository.findByIdNotIn(draftRows.stream().map(DraftRow::getRowId).filter(Objects::nonNull).collect(Collectors.toList()));
        final Collection<DraftRow> toAddInOriginal = draftRowRepository.findByRowIdNull();

        rowRepository.deleteAll(toDeleteFromOriginal);

        for (DraftRow draftRow : toAddInOriginal) {
            draftRow.setRowId(rowRepository.save(draftRow.toRow()).getId());
            draftRowRepository.save(draftRow);
        }

        for (Row row : rowRepository.findByFileId(fileId)) {
            draftRowRepository.findByRowId(row.getId())
                    .ifPresent(draftRow -> {
                        row.setFileName(draftRow.getFileName());
                        row.setFileType(draftRow.getFileType());
                        row.setFileLocation(draftRow.getFileLocation());
                        row.setFileModifiedBy(draftRow.getFileModifiedBy());
                        row.setFileLastModifiedOn(draftRow.getFileLastModifiedOn());
                        rowRepository.save(row);
                    });
        }

        file.setHasDraft(false);

        excelFileRepository.save(file);

    }

    @Transactional
    public void deleteFile(Integer fileId) {
        excelFileRepository.deleteByIdAndCreatedBy(fileId, securityUtil.getUserId());
    }
}
