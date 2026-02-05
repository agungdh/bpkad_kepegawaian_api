package id.my.agungdh.bpkadkepegawaian.service;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiResponse;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.sort.PegawaiSortableField;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortDirection;
import id.my.agungdh.bpkadkepegawaian.entity.Pegawai;
import id.my.agungdh.bpkadkepegawaian.entity.Skpd;
import id.my.agungdh.bpkadkepegawaian.entity.Bidang;
import id.my.agungdh.bpkadkepegawaian.mapper.PegawaiMapper;
import id.my.agungdh.bpkadkepegawaian.repository.PegawaiRepository;
import id.my.agungdh.bpkadkepegawaian.repository.SkpdRepository;
import id.my.agungdh.bpkadkepegawaian.repository.BidangRepository;
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
    private final SkpdRepository skpdRepository;
    private final BidangRepository bidangRepository;
    private final PegawaiMapper pegawaiMapper;
    private final CursorPaginationRepository cursorPaginationRepository;

    public CursorPage<PegawaiResponse> list(CursorRequest request, PegawaiSortableField sortBy, SortDirection direction) {
        UUID cursorUuid = null;
        if (request.cursor() != null) {
            cursorUuid = UUID.fromString(new String(java.util.Base64.getDecoder().decode(request.cursor())));
        }

        List<Pegawai> entities = cursorPaginationRepository.fetchCursor(
                Pegawai.class,
                cursorUuid,
                request.limit() + 1,
                sortBy,
                direction
        );

        boolean hasNext = entities.size() > request.limit();
        if (hasNext) {
            entities = entities.subList(0, request.limit());
        }

        // Load relationships
        entities.forEach(this::loadRelationships);

        List<PegawaiResponse> responses = entities.stream()
                .map(pegawaiMapper::toResponse)
                .collect(Collectors.toList());

        String nextCursor = null;
        if (hasNext && !entities.isEmpty()) {
            Pegawai last = entities.get(entities.size() - 1);
            nextCursor = java.util.Base64.getEncoder().encodeToString(last.getUuid().toString().getBytes());
        }

        return CursorPage.of(responses, nextCursor, hasNext);
    }

    public CursorPage<PegawaiResponse> list(CursorRequest request) {
        return list(request, PegawaiSortableField.ID, SortDirection.ASC);
    }

    public PegawaiResponse getByUuid(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));

        loadRelationships(pegawai);
        return pegawaiMapper.toResponse(pegawai);
    }

    @Transactional
    public PegawaiResponse create(PegawaiCreateRequest request) {
        Pegawai pegawai = new Pegawai();
        pegawai.setNip(request.nip());
        pegawai.setNama(request.nama());
        pegawai.setEmail(request.email());

        // Set SKPD
        if (request.skpdUuid() != null && !request.skpdUuid().isBlank()) {
            UUID skpdUuid = UUID.fromString(request.skpdUuid());
            Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(skpdUuid)
                    .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));
            pegawai.setSkpdId(skpd.getId());
        }

        // Set Bidang (must have SKPD)
        if (request.bidangUuid() != null && !request.bidangUuid().isBlank()) {
            UUID bidangUuid = UUID.fromString(request.bidangUuid());
            Bidang bidang = bidangRepository.findByUuidAndDeletedAtIsNull(bidangUuid)
                    .orElseThrow(() -> new RuntimeException("Bidang tidak ditemukan"));

            if (pegawai.getSkpdId() == null) {
                throw new RuntimeException("SKPD wajib diisi jika Bidang diisi");
            }

            if (!bidang.getSkpdId().equals(pegawai.getSkpdId())) {
                throw new RuntimeException("SKPD tidak sesuai dengan Bidang");
            }

            pegawai.setBidangId(bidang.getId());
        }

        Pegawai saved = pegawaiRepository.save(pegawai);
        loadRelationships(saved);
        return pegawaiMapper.toResponse(saved);
    }

    @Transactional
    public PegawaiResponse update(String uuid, PegawaiUpdateRequest request) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));

        // Update basic fields
        if (request.nip() != null) {
            pegawai.setNip(request.nip());
        }
        if (request.nama() != null) {
            pegawai.setNama(request.nama());
        }
        if (request.email() != null) {
            pegawai.setEmail(request.email());
        }

        // Update SKPD
        Long newSkpdId = null;
        if (request.skpdUuid() != null && !request.skpdUuid().isBlank()) {
            UUID skpdUuid = UUID.fromString(request.skpdUuid());
            Skpd skpd = skpdRepository.findByUuidAndDeletedAtIsNull(skpdUuid)
                    .orElseThrow(() -> new RuntimeException("SKPD tidak ditemukan"));
            newSkpdId = skpd.getId();
        }

        // Update Bidang
        Long newBidangId = pegawai.getBidangId();
        if (request.bidangUuid() != null) {
            if (!request.bidangUuid().isBlank()) {
                UUID bidangUuid = UUID.fromString(request.bidangUuid());
                Bidang bidang = bidangRepository.findByUuidAndDeletedAtIsNull(bidangUuid)
                        .orElseThrow(() -> new RuntimeException("Bidang tidak ditemukan"));

                if (newSkpdId == null && pegawai.getSkpdId() == null) {
                    throw new RuntimeException("SKPD wajib diisi jika Bidang diisi");
                }

                Long targetSkpdId = newSkpdId != null ? newSkpdId : pegawai.getSkpdId();
                if (!bidang.getSkpdId().equals(targetSkpdId)) {
                    throw new RuntimeException("SKPD tidak sesuai dengan Bidang");
                }

                newBidangId = bidang.getId();
                // If bidang is set, skpd must also be set
                if (newSkpdId == null) {
                    newSkpdId = bidang.getSkpdId();
                }
            } else {
                // Explicitly clearing bidang
                newBidangId = null;
            }
        }

        // Apply SKPD and Bidang updates
        if (newSkpdId != null) {
            pegawai.setSkpdId(newSkpdId);
        }
        if (request.bidangUuid() != null) {
            pegawai.setBidangId(newBidangId);
        }

        Pegawai updated = pegawaiRepository.save(pegawai);
        loadRelationships(updated);
        return pegawaiMapper.toResponse(updated);
    }

    @Transactional
    public void softDelete(String uuid) {
        UUID uuidObj = UUID.fromString(uuid);
        Pegawai pegawai = pegawaiRepository.findByUuidAndDeletedAtIsNull(uuidObj)
                .orElseThrow(() -> new RuntimeException("Pegawai tidak ditemukan"));

        pegawaiRepository.softDelete(pegawai.getId(), getCurrentUserId(), System.currentTimeMillis());
    }

    private void loadRelationships(Pegawai pegawai) {
        if (pegawai.getSkpdId() != null) {
            skpdRepository.findById(pegawai.getSkpdId()).ifPresent(pegawai::setSkpd);
        }
        if (pegawai.getBidangId() != null) {
            bidangRepository.findById(pegawai.getBidangId()).ifPresent(b -> {
                pegawai.setBidang(b);
                // Load bidang's skpd if needed
                if (b.getSkpdId() != null) {
                    skpdRepository.findById(b.getSkpdId()).ifPresent(b::setSkpd);
                }
            });
        }
    }

    private Long getCurrentUserId() {
        return 1L; // TODO: Implement from security context
    }
}
