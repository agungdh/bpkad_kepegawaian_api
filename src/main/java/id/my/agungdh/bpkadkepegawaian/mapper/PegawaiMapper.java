package id.my.agungdh.bpkadkepegawaian.mapper;

import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiCreateRequest;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiResponse;
import id.my.agungdh.bpkadkepegawaian.dto.pegawai.PegawaiUpdateRequest;
import id.my.agungdh.bpkadkepegawaian.entity.Pegawai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PegawaiMapper {

    @Mapping(target = "skpdId", ignore = true)
    @Mapping(target = "bidangId", ignore = true)
    Pegawai toEntity(PegawaiCreateRequest request);

    @Mapping(target = "skpdUuid", expression = "java(entity.getSkpd() != null ? entity.getSkpd().getUuid().toString() : null)")
    @Mapping(target = "skpdNama", expression = "java(entity.getSkpd() != null ? entity.getSkpd().getNama() : null)")
    @Mapping(target = "bidangUuid", expression = "java(entity.getBidang() != null ? entity.getBidang().getUuid().toString() : null)")
    @Mapping(target = "bidangNama", expression = "java(entity.getBidang() != null ? entity.getBidang().getNama() : null)")
    PegawaiResponse toResponse(Pegawai entity);

    @Mapping(target = "skpdId", ignore = true)
    @Mapping(target = "bidangId", ignore = true)
    void updateEntityFromDto(PegawaiUpdateRequest request, @MappingTarget Pegawai entity);
}
