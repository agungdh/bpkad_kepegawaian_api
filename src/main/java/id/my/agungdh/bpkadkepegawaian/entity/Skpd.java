package id.my.agungdh.bpkadkepegawaian.entity;

import id.my.agungdh.bpkadkepegawaian.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "skpd")
@Data
@EqualsAndHashCode(callSuper = true)
public class Skpd extends BaseEntity {

    @Column(name = "nama", nullable = false)
    private String nama;
}
