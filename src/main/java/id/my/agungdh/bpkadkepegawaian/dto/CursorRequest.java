package id.my.agungdh.bpkadkepegawaian.dto;

public record CursorRequest(
        String cursor,
        Integer limit
) {
    public static final Integer DEFAULT_LIMIT = 20;
    public static final Integer MAX_LIMIT = 100;

    public static CursorRequest of(String cursor, Integer limit) {
        // Validate and apply defaults
        if (limit == null || limit < 1 || limit > MAX_LIMIT) {
            limit = DEFAULT_LIMIT;
        }
        return new CursorRequest(cursor, limit);
    }

    public CursorRequest withDefaults() {
        return of(cursor, limit);
    }
}
