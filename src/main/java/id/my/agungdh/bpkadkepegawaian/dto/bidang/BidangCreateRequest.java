package id.my.agungdh.bpkadkepegawaian.dto.bidang;

import jakarta.validation.constraints.NotBlank;

public record BidangCreateRequest(
        @NotBlank(message = "SKPD wajib diisi")
        String skpdUuid,

        @NotBlank(message = "Nama wajib diisi")
        String nama
) {}
