package id.my.agungdh.bpkadkepegawaian.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private Long userId;
    private String username;
    private Set<String> roles;
    private Long pegawaiId;
}
