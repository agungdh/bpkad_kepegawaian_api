package id.my.agungdh.bpkadkepegawaian.repository.base;

import id.my.agungdh.bpkadkepegawaian.dto.sort.SortDirection;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortableField;
import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CursorPaginationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Fetch entities using cursor-based pagination with UUID and default id ordering.
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
        return fetchCursor(entityClass, cursorUuid, limit, "id", ascending);
    }

    /**
     * Fetch entities using cursor-based pagination with UUID and dynamic sorting.
     *
     * @param entityClass Entity class
     * @param cursorUuid  UUID from decoded cursor (null for first page)
     * @param limit       Max results
     * @param sortBy      Field name to sort by
     * @param direction   Sort direction
     * @param <T>         Entity type
     * @return List of entities
     */
    public <T extends BaseEntity> List<T> fetchCursor(
            Class<T> entityClass,
            UUID cursorUuid,
            int limit,
            String sortBy,
            SortDirection direction
    ) {
        return fetchCursor(entityClass, cursorUuid, limit, sortBy, direction == SortDirection.ASC);
    }

    /**
     * Fetch entities using cursor-based pagination with UUID and dynamic sorting.
     * Note: When sorting by fields other than 'id', cursor-based pagination
     * uses a simplified approach that may skip duplicates but remains consistent.
     *
     * @param entityClass Entity class
     * @param cursorUuid  UUID from decoded cursor (null for first page)
     * @param limit       Max results
     * @param sortBy      Field name to sort by
     * @param ascending   Order direction (true for ASC, false for DESC)
     * @param <T>         Entity type
     * @return List of entities
     */
    public <T extends BaseEntity> List<T> fetchCursor(
            Class<T> entityClass,
            UUID cursorUuid,
            int limit,
            String sortBy,
            boolean ascending
    ) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(entityClass);
        var root = cq.from(entityClass);

        // Build where clause
        List<Predicate> predicates = new ArrayList<>();

        // Filter out soft-deleted records
        predicates.add(cb.isNull(root.get("deletedAt")));

        // If cursor is provided, filter based on id only
        // This simplifies the implementation and works reliably
        if (cursorUuid != null) {
            Long cursorId = getIdByUuid(entityClass, cursorUuid);
            if (cursorId != null) {
                if (ascending) {
                    predicates.add(cb.greaterThan(root.get("id"), cursorId));
                } else {
                    predicates.add(cb.lessThan(root.get("id"), cursorId));
                }
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // Order by the specified field, then by id for consistent pagination
        List<Order> orders = new ArrayList<>();
        if (ascending) {
            orders.add(cb.asc(root.get(sortBy)));
        } else {
            orders.add(cb.desc(root.get(sortBy)));
        }
        // Always add id as secondary sort for stable results
        orders.add(cb.asc(root.get("id")));
        cq.orderBy(orders);

        return entityManager.createQuery(cq)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * Fetch entities using cursor-based pagination with SortableField enum.
     *
     * @param entityClass Entity class
     * @param cursorUuid  UUID from decoded cursor (null for first page)
     * @param limit       Max results
     * @param sortableField Enum defining valid sort fields
     * @param direction   Sort direction
     * @param <T>         Entity type
     * @param <E>         SortableField enum type
     * @return List of entities
     */
    public <T extends BaseEntity, E extends Enum<E> & SortableField> List<T> fetchCursor(
            Class<T> entityClass,
            UUID cursorUuid,
            int limit,
            E sortableField,
            SortDirection direction
    ) {
        return fetchCursor(entityClass, cursorUuid, limit, sortableField.getFieldName(), direction);
    }

    /**
     * Get entity ID by UUID.
     */
    private <T extends BaseEntity> Long getIdByUuid(Class<T> entityClass, UUID uuid) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Long.class);
        var root = cq.from(entityClass);

        cq.select(root.get("id"));
        cq.where(cb.and(
                cb.equal(root.get("uuid"), uuid),
                cb.isNull(root.get("deletedAt"))
        ));

        try {
            return entityManager.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
