package id.my.agungdh.bpkadkepegawaian.repository.base;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CursorPaginationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public <T> List<T> fetchCursor(
            Class<T> entityClass,
            String cursorField,
            Object cursorValue,
            int limit,
            boolean ascending
    ) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(entityClass);
        var root = cq.from(entityClass);

        if (cursorValue != null) {
            var comparison = ascending
                    ? cb.greaterThan(root.get(cursorField), (Comparable) cursorValue)
                    : cb.lessThan(root.get(cursorField), (Comparable) cursorValue);
            cq.where(comparison);
        }

        cq.orderBy(ascending
                ? cb.asc(root.get(cursorField))
                : cb.desc(root.get(cursorField)));

        return entityManager.createQuery(cq)
                .setMaxResults(limit)
                .getResultList();
    }
}
