package id.my.agungdh.bpkadkepegawaian.mapper;

import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiResponse;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Pegawai;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PegawaiMapper {

    Pegawai toEntity(PegawaiCreateRequest request);

    PegawaiResponse toResponse(Pegawai entity);

    void updateEntityFromDto(PegawaiUpdateRequest request, @MappingTarget Pegawai entity);
}
