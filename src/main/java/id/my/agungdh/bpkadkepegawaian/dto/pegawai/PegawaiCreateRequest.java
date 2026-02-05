package id.my.agungdh.bpkadkepegawaian.dto.pegawai;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PegawaiCreateRequest(
        @NotBlank(message = "NIP wajib diisi")
        String nip,

        @NotBlank(message = "Nama wajib diisi")
        String nama,

        @Email(message = "Format email tidak valid")
        String email,

        String skpdUuid,

        String bidangUuid
) {}
