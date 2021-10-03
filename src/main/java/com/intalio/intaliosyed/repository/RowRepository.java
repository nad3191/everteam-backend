package com.intalio.intaliosyed.repository;

import com.intalio.intaliosyed.entity.row.AbstractRow;
import com.intalio.intaliosyed.entity.row.Row;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RowRepository extends JpaRepository<Row, Long> {
    Page<Row> findByFileId(Integer fileId, Pageable pageable);

    void deleteByIdAndFileIdAndCreatedBy(Long rowId, Integer fileId, Long userId);

    Optional<Row> findByIdAndFileIdAndCreatedBy(Long id, Integer fileId, Long userId);

    Page<AbstractRow> findByFileIdAndCreatedBy(Integer fileId, Long userId, Pageable pageable);

    void deleteAllByFileId(Integer fileId);

    void deleteAllByIdAndFileId(Long id, Integer fileId);

    void deleteAllByIdInAndFileId(Collection<Long> ids, Integer fileId);

    Collection<Row> findByIdNotIn(Collection<Long> ids);

    Collection<Row> findByFileId(Integer fileId);
}
