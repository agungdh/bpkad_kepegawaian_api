package id.my.agungdh.bpkadkepegawaian.dto;

import java.util.List;

public record CursorPage<T>(
        List<T> data,
        String nextCursor,
        String previousCursor,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> CursorPage<T> of(List<T> data, String nextCursor, boolean hasNext) {
        return new CursorPage<>(data, nextCursor, null, hasNext, false);
    }

    public static <T> CursorPage<T> empty() {
        return new CursorPage<>(List.of(), null, null, false, false);
    }
}
