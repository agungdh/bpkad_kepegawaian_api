package id.my.agungdh.bpkadkepegawaian.entity;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "users")
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "pegawai_id", nullable = false)
    private Long pegawaiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pegawai_id", insertable = false, updatable = false)
    private Pegawai pegawai;
}
