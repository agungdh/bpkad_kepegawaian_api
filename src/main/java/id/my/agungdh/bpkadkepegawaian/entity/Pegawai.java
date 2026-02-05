package id.my.agungdh.bpkadkepegawaian.entity;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "pegawai")
@Data
@EqualsAndHashCode(callSuper = true)
public class Pegawai extends BaseEntity {

    @Column(name = "nip", nullable = false, length = 50)
    private String nip;

    @Column(name = "nama", nullable = false)
    private String nama;

    @Column(name = "email")
    private String email;
}
