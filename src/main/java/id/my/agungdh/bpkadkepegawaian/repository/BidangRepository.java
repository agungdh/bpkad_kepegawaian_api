package id.my.agungdh.bpkadkepegawaian.repository;

import id.my.agungdh.bpkadkepegawaian.entity.Bidang;
import id.my.agungdh.bpkadkepegawaian.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidangRepository extends BaseRepository<Bidang> {

    List<Bidang> findBySkpdIdAndDeletedAtIsNull(Long skpdId);
}
