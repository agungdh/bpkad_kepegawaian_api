package id.my.agungdh.bpkadkepegawaian.repository;

import id.my.agungdh.bpkadkepegawaian.entity.Role;
import id.my.agungdh.bpkadkepegawaian.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role> {

    Optional<Role> findByNameAndDeletedAtIsNull(String name);
}
