package id.my.agungdh.bpkadkepegawaian.mapper;

import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangResponse;
import id.my.agungdh.bpkadkepegawaian.dto.bidang.BidangUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Bidang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BidangMapper {

    @Mapping(target = "skpdId", ignore = true)
    @Mapping(target = "nama", source = "nama")
    Bidang toEntity(BidangCreateRequest request);

    @Mapping(target = "skpdUuid", expression = "java(entity.getSkpd() != null ? entity.getSkpd().getUuid().toString() : null)")
    @Mapping(target = "skpdNama", expression = "java(entity.getSkpd() != null ? entity.getSkpd().getNama() : null)")
    BidangResponse toResponse(Bidang entity);

    @Mapping(target = "skpdId", ignore = true)
    @Mapping(target = "nama", source = "nama")
    void updateEntityFromDto(BidangUpdateRequest request, @MappingTarget Bidang entity);
}
