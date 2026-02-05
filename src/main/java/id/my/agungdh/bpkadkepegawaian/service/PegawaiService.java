package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiResponse;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Pegawai;
import id.my.agungdh.bpkadkepegawaian.mapper.PegawaiMapper;
import id.my.agungdh.bpkadkepegawaian.repository.PegawaiRepository;
import id.my.agungdh.bpkadkepegawaian.repository.base.CursorPaginationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PegawaiService {

    private final PegawaiRepository pegawaiRepository;
    private final PegawaiMapper pegawaiMapper;
    private final CursorPaginationRepository cursorPaginationRepository;

    public CursorPage<PegawaiResponse> list(CursorRequest request) {
        Long cursorValue = null;
        if (request.cursor() != null) {
            cursorValue = Long.parseLong(new String(java.util.Base64.getDecoder().decode(request.cursor())).split(":")[0]);
        }

        List<Pegawai> entities = cursorPaginationRepository.fetchCursor(
                Pegawai.class,
                "id",
                cursorValue,
                request.limit() + 1, // Fetch one extra to check if there's a next page
                true
        );

        boolean hasNext = entities.size() > request.limit();
        if (hasNext) {
            entities = entities.subList(0, request.limit());
        }

        List<PegawaiResponse> responses = entities.stream()
                .map(pegawaiMapper::toResponse)
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext && !entities.isEmpty()) {
            Pegawai last = entities.get(entities.size() - 1);
            String cursorValueStr = last.getId() + ":" + last.getUpdatedAt().toString();
            nextCursor = java.util.Base64.getEncoder().encodeToString(cursorValueStr.getBytes());
        }

        return CursorPage.of(responses, nextCursor, hasNext);
    }

    public PegawaiResponse getByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));
        return pegawaiMapper.toResponse(pegawai);
    }

    @Transactional
    public PegawaiResponse create(PegawaiCreateRequest request) {
        Pegawai pegawai = pegawaiMapper.toEntity(request);
        Pegawai saved = pegawaiRepository.save(pegawai);
        return pegawaiMapper.toResponse(saved);
    }

    @Transactional
    public PegawaiResponse update(String uuid, PegawaiUpdateRequest request) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));

        pegawaiMapper.updateEntityFromDto(request, pegawai);
        Pegawai updated = pegawaiRepository.save(pegawai);
        return pegawaiMapper.toResponse(updated);
    }

    @Transactional
    public void softDelete(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));

        pegawaiRepository.softDelete(pegawai.getId(), getCurrentUserId());
    }

    private Long getCurrentUserId() {
        return 1L; // TODO: Implement from security context
    }
}
