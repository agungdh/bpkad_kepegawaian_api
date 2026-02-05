package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangResponse;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Bidang;
import id.my.agungdh.bpkadkepegawaian.entity.Skpd;
import id.my.agungdh.bpkadkepegawaian.mapper.BidangMapper;
import id.my.agungdh.bpkadkepegawaian.repository.BidangRepository;
import id.my.agungdh.bpkadkepegawaian.repository.SkpdRepository;
import id.my.agungdh.bpkadkepegawaian.repository.base.CursorPaginationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidangService {

    private final BidangRepository bidangRepository;
    private final SkpdRepository skpdRepository;
    private final BidangMapper bidangMapper;
    private final CursorPaginationRepository cursorPaginationRepository;

    public CursorPage<BidangResponse> list(CursorRequest request) {
        Long cursorValue = null;
        if (request.cursor() != null) {
            cursorValue = Long.parseLong(new String(java.util.Base64.getDecoder().decode(request.cursor())).split(":")[0]);
        }

        var entities = cursorPaginationRepository.fetchCursor(
                Bidang.class,
                "id",
                cursorValue,
                request.limit() + 1,
                true
        );

        boolean hasNext = entities.size() > request.limit();
        if (hasNext) {
            entities = entities.subList(0, request.limit());
        }

        // Load Skpd for each Bidang
        entities.forEach(b -> {
            if (b.getSkpdId() != null) {
                skpdRepository.findById(b.getSkpdId()).ifPresent(b::setSkpd);
            }
        });

        var responses = entities.stream()
                .map(bidangMapper::toResponse)
                .toList();

        String nextCursor = null;
        if (hasNext && !entities.isEmpty()) {
            Bidang last = entities.get(entities.size() - 1);
            String cursorValueStr = last.getId() + ":" + last.getUpdatedAt();
            nextCursor = java.util.Base64.getEncoder().encodeToString(cursorValueStr.getBytes());
        }

        return CursorPage.of(responses, nextCursor, hasNext);
    }

    public BidangResponse getByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Bidang bidang = bidangRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Bidang tidak ditemukan"));

        if (bidang.getSkpdId() != null) {
            skpdRepository.findById(bidang.getSkpdId()).ifPresent(bidang::setSkpd);
        }

        return bidangMapper.toResponse(bidang);
    }

    @Transactional
    public BidangResponse create(BidangCreateRequest request) {
        UUID skpdUuid = UUID.fromString(request.skpdUuid());
        Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(skpdUuid)
                .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));

        Bidang bidang = new Bidang();
        bidang.setSkpdId(skpd.getId());
        bidang.setNama(request.nama());

        Bidang saved = bidangRepository.save(bidang);
        saved.setSkpd(skpd);

        return bidangMapper.toResponse(saved);
    }

    @Transactional
    public BidangResponse update(String uuid, BidangUpdateRequest request) {
        UUID uuidObj = UUID.fromString(uuid);
        Bidang bidang = bidangRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Bidang tidak ditemukan"));

        UUID skpdUuid = UUID.fromString(request.skpdUuid());
        Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(skpdUuid)
                .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));

        bidang.setSkpdId(skpd.getId());
        if (request.nama() != null) {
            bidang.setNama(request.nama());
        }

        Bidang updated = bidangRepository.save(bidang);
        updated.setSkpd(skpd);

        return bidangMapper.toResponse(updated);
    }

    @Transactional
    public void softDelete(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Bidang bidang = bidangRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Bidang tidak ditemukan"));

        bidangRepository.softDelete(bidang.getId(), getCurrentUserId(), System.currentTimeMillis());
    }

    private Long getCurrentUserId() {
        return 1L; // TODO: Implement from security context
    }
}
