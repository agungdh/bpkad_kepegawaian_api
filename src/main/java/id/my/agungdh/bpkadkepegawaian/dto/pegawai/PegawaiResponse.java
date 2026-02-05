package id.my.agungdh.bpkadkepegawaian.dto.pegawai;

import java.time.LocalDateTime;

public record PegawaiResponse(
        String uuid,  // Hanya UUID, ID serial tidak di-expose
        String nip,
        String nama,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
