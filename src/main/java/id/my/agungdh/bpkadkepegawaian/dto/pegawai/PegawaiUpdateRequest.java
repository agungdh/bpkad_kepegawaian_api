package id.my.agungdh.bpkadkepegawaian.dto.pegawai;

import jakarta.validation.constraints.Email;

public record PegawaiUpdateRequest(
        String nip,
        String nama,
        @Email(message = "Format email tidak valid")
        String email,
        String skpdUuid,
        String bidangUuid
) {}
