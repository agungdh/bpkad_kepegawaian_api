package id.my.agungdh.bpkadkepegawaian.repository;

import id.my.agungdh.bpkadkepegawaian.entity.UserRole;
import id.my.agungdh.bpkadkepegawaian.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findByUser_Id(Long userId);

    void deleteByUser_Id(Long userId);
}
