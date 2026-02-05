package id.my.agungdh.bpkadkepegawaian.repository;

import id.my.agungdh.bpkadkepegawaian.entity.Pegawai;
import id.my.agungdh.bpkadkepegawaian.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PegawaiRepository extends BaseRepository<Pegawai> {

    // Find by UUID (exclude soft deleted) - inherited from BaseRepository

    // Additional query methods specific to Pegawai can be added here
}
