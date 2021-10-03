package com.intalio.intaliosyed.repository;

import com.intalio.intaliosyed.entity.excel.DraftExcelFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DraftExcelFileRepository extends JpaRepository<DraftExcelFile, Long> {
    Page<DraftExcelFile> findAllByCreatedBy(Long userId, Pageable pageable);

    Optional<DraftExcelFile> findByIdAndCreatedBy(@NonNull final Integer id, @NonNull final Long userId);

    void deleteByIdAndCreatedBy(Integer id, Long userId);
}
