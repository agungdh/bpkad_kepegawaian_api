package id.my.agungdh.bpkadkepegawaian.controller;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangResponse;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.sort.BidangSortableField;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortDirection;
import id.my.agungdh.bpkadkepegawaian.service.BidangService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bidang")
@RequiredArgsConstructor
@Tag(name = "Bidang", description = "API untuk manajemen data Bidang")
public class BidangController {

    private final BidangService bidangService;

    @GetMapping
    @Operation(summary = "List Bidang dengan cursor pagination")
    public CursorPage<BidangResponse> list(
            @Parameter(description = "Cursor untuk halaman selanjutnya")
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Kolom untuk sorting (id, skpdId, nama, createdAt, updatedAt)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Arah sorting (ASC, DESC)")
            @RequestParam(required = false) String sortDirection
    ) {
        BidangSortableField sortField = sortBy != null ? BidangSortableField.valueOf(sortBy) : BidangSortableField.ID;
        SortDirection direction = sortDirection != null ? SortDirection.valueOf(sortDirection) : SortDirection.ASC;
        return bidangService.list(CursorRequest.of(cursor, limit), sortField, direction);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Detail Bidang berdasarkan UUID")
    public BidangResponse getByUuid(
            @Parameter(description = "UUID Bidang")
            @PathVariable String uuid
    ) {
        return bidangService.getByUuid(uuid);
    }

    @PostMapping
    @Operation(summary = "Create Bidang baru")
    public BidangResponse create(@Valid @RequestBody BidangCreateRequest request) {
        return bidangService.create(request);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update data Bidang")
    public BidangResponse update(
            @Parameter(description = "UUID Bidang")
            @PathVariable String uuid,
            @Valid @RequestBody BidangUpdateRequest request
    ) {
        return bidangService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft delete Bidang")
    public void delete(
            @Parameter(description = "UUID Bidang")
            @PathVariable String uuid
    ) {
        bidangService.softDelete(uuid);
    }
}
