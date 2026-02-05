package id.my.agungdh.bpkadkepegawaian.repository.base;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CursorPaginationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch entities using cursor-based pagination with UUID.
     *
     * @param entityClass Entity class
     * @param cursorUuid  UUID from decoded cursor (null for first page)
     * @param limit       Max results
     * @param ascending   Order direction
     * @param <T>         Entity type
     * @return List of entities
     */
    public <T extends BaseEntity> List<T> fetchCursor(
            Class<T> entityClass,
            UUID cursorUuid,
            int limit,
            boolean ascending
    ) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(entityClass);
        var root = cq.from(entityClass);

        // Filter out soft-deleted records
        cq.where(cb.isNull(root.get("deletedAt")));

        // If cursor is provided, find the entity by UUID and filter by id
        if (cursorUuid != null) {
            // First, get the entity's id by uuid
            Long cursorId = getIdByUuid(entityClass, cursorUuid);
            if (cursorId != null) {
                var comparison = ascending
                        ? cb.greaterThan(root.get("id"), cursorId)
                        : cb.lessThan(root.get("id"), cursorId);
                cq.where(cb.and(cb.isNull(root.get("deletedAt")), comparison));
            }
        }

        // Always order by id
        cq.orderBy(ascending
                ? cb.asc(root.get("id"))
                : cb.desc(root.get("id")));

        return entityManager.createQuery(cq)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Get entity ID by UUID.
     */
    private <T extends BaseEntity> Long getIdByUuid(Class<T> entityClass, UUID uuid) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var root = cq.from(entityClass);

        cq.select(root.get("id"))
                .where(cb.equal(root.get("uuid"), uuid))
                .where(cb.isNull(root.get("deletedAt")));

        try {
            return entityManager.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
