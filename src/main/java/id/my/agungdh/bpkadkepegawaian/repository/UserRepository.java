package id.my.agungdh.bpkadkepegawaian.repository;

import id.my.agungdh.bpkadkepegawaian.entity.User;
import id.my.agungdh.bpkadkepegawaian.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    boolean existsByUsernameAndDeletedAtIsNull(String username);
}
