package id.my.agungdh.bpkadkepegawaian.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CursorRequest(
        String cursor,
        @NotNull @Min(1) @Max(100) Integer limit
) {
    public static final Integer DEFAULT_LIMIT = 20;

    public CursorRequest withDefaults() {
        return new CursorRequest(
                cursor,
                limit != null ? limit : DEFAULT_LIMIT
        );
    }
}
