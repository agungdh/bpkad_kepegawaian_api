package id.my.agungdh.bpkadkepegawaian.dto.skpd;

import jakarta.validation.constraints.NotBlank;

public record SkpdCreateRequest(
        @NotBlank(message = "Nama wajib diisi")
        String nama
) {}
