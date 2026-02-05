package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdResponse;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Skpd;
import id.my.agungdh.bpkadkepegawaian.mapper.SkpdMapper;
import id.my.agungdh.bpkadkepegawaian.repository.SkpdRepository;
import id.my.agungdh.bpkadkepegawaian.repository.base.CursorPaginationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkpdService {

    private final SkpdRepository skpdRepository;
    private final SkpdMapper skpdMapper;
    private final CursorPaginationRepository cursorPaginationRepository;

    public CursorPage<SkpdResponse> list(CursorRequest request) {
        Long cursorValue = null;
        if (request.cursor() != null) {
            cursorValue = Long.parseLong(new String(java.util.Base64.getDecoder().decode(request.cursor())).split(":")[0]);
        }

        List<Skpd> entities = cursorPaginationRepository.fetchCursor(
                Skpd.class,
                "id",
                cursorValue,
                request.limit() + 1,
                true
        );

        boolean hasNext = entities.size() > request.limit();
        if (hasNext) {
            entities = entities.subList(0, request.limit());
        }

        List<SkpdResponse> responses = entities.stream()
                .map(skpdMapper::toResponse)
                .toList();

        String nextCursor = null;
        if (hasNext && !entities.isEmpty()) {
            Skpd last = entities.get(entities.size() - 1);
            String cursorValueStr = last.getId() + ":" + last.getUpdatedAt();
            nextCursor = java.util.Base64.getEncoder().encodeToString(cursorValueStr.getBytes());
        }

        return CursorPage.of(responses, nextCursor, hasNext);
    }

    public SkpdResponse getByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));
        return skpdMapper.toResponse(skpd);
    }

    @Transactional
    public SkpdResponse create(SkpdCreateRequest request) {
        Skpd skpd = skpdMapper.toEntity(request);
        Skpd saved = skpdRepository.save(skpd);
        return skpdMapper.toResponse(saved);
    }

    @Transactional
    public SkpdResponse update(String uuid, SkpdUpdateRequest request) {
        UUID uuidObj = UUID.fromString(uuid);
        Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));

        skpdMapper.updateEntityFromDto(request, skpd);
        Skpd updated = skpdRepository.save(skpd);
        return skpdMapper.toResponse(updated);
    }

    @Transactional
    public void softDelete(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));

        skpdRepository.softDelete(skpd.getId(), getCurrentUserId(), System.currentTimeMillis());
    }

    private Long getCurrentUserId() {
        return 1L; // TODO: Implement from security context
    }
}
