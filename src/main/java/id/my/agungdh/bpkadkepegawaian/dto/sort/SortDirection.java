package id.my.agungdh.bpkadkepegawaian.dto.sort;

public enum SortDirection {
    ASC,
    DESC;

    public static SortDirection fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // Try exact match first
        try {
            return SortDirection.valueOf(value);
        } catch (IllegalArgumentException ignored) {
        }

        // Try case-insensitive match
        for (SortDirection direction : SortDirection.values()) {
            if (direction.name().equalsIgnoreCase(value)) {
                return direction;
            }
        }

        throw new IllegalArgumentException("No enum constant " + SortDirection.class.getName() + "." + value);
    }
}
