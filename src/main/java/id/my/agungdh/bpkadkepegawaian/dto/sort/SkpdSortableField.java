package id.my.agungdh.bpkadkepegawaian.dto.sort;

public enum SkpdSortableField implements SortableField {
    ID("id"),
    NAMA("nama");

    private final String fieldName;

    SkpdSortableField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
