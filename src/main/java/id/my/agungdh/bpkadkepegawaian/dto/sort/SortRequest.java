package id.my.agungdh.bpkadkepegawaian.dto.sort;

public record SortRequest(
        String sortBy,
        SortDirection direction
) {
    public static final SortDirection DEFAULT_DIRECTION = SortDirection.ASC;
    public static final String DEFAULT_SORT_BY = "id";

    public SortRequest withDefaults(String defaultSortField) {
        return new SortRequest(
                sortBy != null ? sortBy : defaultSortField,
                direction != null ? direction : DEFAULT_DIRECTION
        );
    }
}
