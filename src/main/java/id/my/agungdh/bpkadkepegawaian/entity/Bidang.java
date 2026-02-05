package id.my.agungdh.bpkadkepegawaian.entity;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "bidang")
@Data
@EqualsAndHashCode(callSuper = true)
public class Bidang extends BaseEntity {

    @Column(name = "skpd_id", nullable = false)
    private Long skpdId;

    @Column(name = "nama", nullable = false)
    private String nama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skpd_id", insertable = false, updatable = false)
    private Skpd skpd;
}
