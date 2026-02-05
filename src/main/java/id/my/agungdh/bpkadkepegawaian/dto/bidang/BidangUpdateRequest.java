package id.my.agungdh.bpkadkepegawaian.dto.bidang;

import jakarta.validation.constraints.NotBlank;

public record BidangUpdateRequest(
        @NotBlank(message = "SKPD wajib diisi")
        String skpdUuid,

        String nama
) {}
