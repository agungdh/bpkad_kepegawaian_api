package id.my.agungdh.bpkadkepegawaian.repository.base;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {

    // Find by UUID (exclude soft deleted)
    Optional<T> findByUuidAndDeletedAtIsNull(java.util.UUID uuid);

    // Find by UUID including soft deleted
    @Query("SELECT e FROM #{#entityName} e WHERE e.uuid = :uuid")
    Optional<T> findByUuidIncludingDeleted(@Param("uuid") java.util.UUID uuid);

    // Soft delete
    @Query("UPDATE #{#entityName} e SET e.deletedAt = CURRENT_TIMESTAMP, e.deletedBy = :userId WHERE e.id = :id")
    void softDelete(@Param("id") Long id, @Param("userId") Long userId);

    // Override default delete to soft delete
    @Override
    default void deleteById(Long id) {
        softDelete(id, getCurrentUserId());
    }

    default Long getCurrentUserId() {
        // Implementasi ambil current user ID
        return 1L; // Temporary
    }
}
