package com.intalio.intaliosyed.repository;

import com.intalio.intaliosyed.entity.excel.ExcelFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExcelFileRepository extends JpaRepository<ExcelFile, Integer> {
    Page<ExcelFile> findAllByCreatedBy(Long userId, Pageable pageable);
    Optional<ExcelFile> findByIdAndCreatedBy(@NonNull final Integer id, @NonNull final Long userId);

    void deleteByIdAndCreatedBy(Integer fileId, Long userId);
}
