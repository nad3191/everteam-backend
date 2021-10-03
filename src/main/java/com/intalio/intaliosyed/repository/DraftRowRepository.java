package com.intalio.intaliosyed.repository;

import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.row.DraftRow;
import com.intalio.intaliosyed.entity.row.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DraftRowRepository extends JpaRepository<DraftRow, Long> {
    Page<DraftRow> findByFileId(Integer fileId, Pageable pageable);

    void deleteByIdAndFileIdAndCreatedBy(Long rowId, Integer fileId, Long userId);

    Optional<DraftRow> findByIdAndFileIdAndCreatedBy(Long id, Integer fileId, Long userId);

    Page<AbstractRow> findByFileIdAndCreatedBy(Integer fileId, Long userId, Pageable pageable);

    void deleteAllByFileId(Integer fileId);

    @Query("SELECT d FROM DraftRow d WHERE d.rowId IS NULL")
    Collection<DraftRow> findByRowIdNull();

    Optional<DraftRow> findByRowId(Long rowId);

    Collection<DraftRow> findByRowIdNotIn(Collection<Long> rowIds);
}
