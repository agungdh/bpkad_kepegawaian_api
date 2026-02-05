package id.my.agungdh.bpkadkepegawaian.dto.sort;

public enum BidangSortableField implements SortableField {
    ID("id"),
    SKPD_ID("skpdId"),
    NAMA("nama");

    private final String fieldName;

    BidangSortableField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
