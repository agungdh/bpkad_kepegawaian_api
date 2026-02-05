package id.my.agungdh.bpkadkepegawaian.dto.pegawai;

public record PegawaiResponse(
        String uuid,
        String nip,
        String nama,
        String email,
        String skpdUuid,
        String skpdNama,
        String bidangUuid,
        String bidangNama
) {}
