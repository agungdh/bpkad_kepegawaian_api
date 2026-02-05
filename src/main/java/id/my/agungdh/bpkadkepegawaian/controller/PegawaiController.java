package id.my.agungdh.bpkadkepegawaian.controller;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiResponse;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.sort.PegawaiSortableField;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortDirection;
import id.my.agungdh.bpkadkepegawaian.service.PegawaiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pegawai")
@RequiredArgsConstructor
@Tag(name = "Pegawai", description = "API untuk manajemen data pegawai")
public class PegawaiController {

    private final PegawaiService pegawaiService;

    @GetMapping
    @Operation(summary = "List pegawai dengan cursor pagination")
    public CursorPage<PegawaiResponse> list(
            @Parameter(description = "Cursor untuk halaman selanjutnya")
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Kolom untuk sorting (id, nip, nama, email, skpdId, bidangId, createdAt, updatedAt)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Arah sorting (ASC, DESC)")
            @RequestParam(required = false) String sortDirection
    ) {
        PegawaiSortableField sortField = sortBy != null ? PegawaiSortableField.valueOf(sortBy) : PegawaiSortableField.ID;
        SortDirection direction = sortDirection != null ? SortDirection.valueOf(sortDirection) : SortDirection.ASC;
        return pegawaiService.list(new CursorRequest(cursor, limit).withDefaults(), sortField, direction);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Detail pegawai berdasarkan UUID")
    public PegawaiResponse getByUuid(
            @Parameter(description = "UUID pegawai")
            @PathVariable String uuid
    ) {
        return pegawaiService.getByUuid(uuid);
    }

    @PostMapping
    @Operation(summary = "Create pegawai baru")
    public PegawaiResponse create(@Valid @RequestBody PegawaiCreateRequest request) {
        return pegawaiService.create(request);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update data pegawai")
    public PegawaiResponse update(
            @Parameter(description = "UUID pegawai")
            @PathVariable String uuid,
            @Valid @RequestBody PegawaiUpdateRequest request
    ) {
        return pegawaiService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft delete pegawai")
    public void delete(
            @Parameter(description = "UUID pegawai")
            @PathVariable String uuid
    ) {
        pegawaiService.softDelete(uuid);
    }
}
