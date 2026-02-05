package id.my.agungdh.bpkadkepegawaian.mapper;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import org.mapstruct.Mapper;

import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CursorMapper {

    // Encode entity ID + timestamp ke cursor (base64)
    default String encodeCursor(Long id, Long updatedAt) {
        String cursorValue = id + ":" + updatedAt;
        return Base64.getEncoder().encodeToString(cursorValue.getBytes());
    }

    // Decode cursor ke ID
    default Long decodeId(String cursor) {
        String decoded = new String(Base64.getDecoder().decode(cursor));
        return Long.parseLong(decoded.split(":")[0]);
    }

    default <T extends BaseEntity, R> CursorPage<R> toCursorPage(
            List<T> entities,
            List<R> responses,
            int limit,
            boolean ascending
    ) {
        String nextCursor = null;
        boolean hasNext = entities.size() == limit;

        if (hasNext && !entities.isEmpty()) {
            T last = entities.get(entities.size() - 1);
            nextCursor = encodeCursor(last.getId(), last.getUpdatedAt());
        }

        return CursorPage.of(responses, nextCursor, hasNext);
    }
}
