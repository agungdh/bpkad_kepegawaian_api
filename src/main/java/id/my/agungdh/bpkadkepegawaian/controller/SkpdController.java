package id.my.agungdh.bpkadkepegawaian.controller;

import id.my.agungdh.bpkadkepegawaian.dto.CursorPage;
import id.my.agungdh.bpkadkepegawaian.dto.CursorRequest;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdResponse;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortDirection;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SkpdSortableField;
import id.my.agungdh.bpkadkepegawaian.dto.sort.SortableField;
import id.my.agungdh.bpkadkepegawaian.service.SkpdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skpd")
@RequiredArgsConstructor
@Tag(name = "SKPD", description = "API untuk manajemen data SKPD")
public class SkpdController {

    private final SkpdService skpdService;

    @GetMapping
    @Operation(summary = "List SKPD dengan cursor pagination")
    public CursorPage<SkpdResponse> list(
            @Parameter(description = "Cursor untuk halaman selanjutnya")
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") Integer limit,
            @Parameter(description = "Kolom untuk sorting (id, nama, createdAt, updatedAt)")
            @RequestParam(required = false) String sortBy,
            @Parameter(description = "Arah sorting (ASC, DESC)")
            @RequestParam(required = false) String sortDirection
    ) {
        SkpdSortableField sortField = SortableField.fromString(SkpdSortableField.class, sortBy);
        if (sortField == null) sortField = SkpdSortableField.ID;
        SortDirection direction = SortDirection.fromString(sortDirection);
        if (direction == null) direction = SortDirection.ASC;
        return skpdService.list(CursorRequest.of(cursor, limit), sortField, direction);
    }

    @GetMapping("/{uuid}")
    @Operation(summary = "Detail SKPD berdasarkan UUID")
    public SkpdResponse getByUuid(
            @Parameter(description = "UUID SKPD")
            @PathVariable String uuid
    ) {
        return skpdService.getByUuid(uuid);
    }

    @PostMapping
    @Operation(summary = "Create SKPD baru")
    public SkpdResponse create(@Valid @RequestBody SkpdCreateRequest request) {
        return skpdService.create(request);
    }

    @PutMapping("/{uuid}")
    @Operation(summary = "Update data SKPD")
    public SkpdResponse update(
            @Parameter(description = "UUID SKPD")
            @PathVariable String uuid,
            @Valid @RequestBody SkpdUpdateRequest request
    ) {
        return skpdService.update(uuid, request);
    }

    @DeleteMapping("/{uuid}")
    @Operation(summary = "Soft delete SKPD")
    public void delete(
            @Parameter(description = "UUID SKPD")
            @PathVariable String uuid
    ) {
        skpdService.softDelete(uuid);
    }
}
