package id.my.agungdh.bpkadkepegawaian.mapper;

import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdResponse;
import id.my.agungdh.bpkadkepegawaian.dto.skpd.SkpdUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Skpd;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SkpdMapper {

    Skpd toEntity(SkpdCreateRequest request);

    SkpdResponse toResponse(Skpd entity);

    void updateEntityFromDto(SkpdUpdateRequest request, @MappingTarget Skpd entity);
}
