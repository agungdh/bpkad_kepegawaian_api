package id.my.agungdh.bpkadkepegawaian.dto.sort;

public interface SortableField {
    String getFieldName();

    /**
     * Find enum constant by case-insensitive name.
     * Handles both camelCase (nama) and UPPER_CASE (NAMA) inputs.
     */
    static <T extends Enum<T> & SortableField> T fromString(Class<T> enumType, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // Try exact match first (for UPPER_CASE like "NAMA")
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException ignored) {
        }

        // Try case-insensitive match (for camelCase like "nama")
        String upperValue = value.toUpperCase();
        for (T field : enumType.getEnumConstants()) {
            if (field.name().equals(upperValue)) {
                return field;
            }
        }

        throw new IllegalArgumentException("No enum constant " + enumType.getName() + "." + value);
    }
}
