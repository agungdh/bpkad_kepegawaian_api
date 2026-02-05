package id.my.agungdh.bpkadkepegawaian.dto.sort;

public enum PegawaiSortableField implements SortableField {
    ID("id"),
    NIP("nip"),
    NAMA("nama"),
    EMAIL("email"),
    SKPD_ID("skpdId"),
    BIDANG_ID("bidangId");

    private final String fieldName;

    PegawaiSortableField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
