package id.my.agungdh.bpkadkepegawaian.security;

import id.my.agungdh.bpkadkepegawaian.dto.UserData;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Getter
public class UserAuthentication extends AbstractAuthenticationToken {

    private final UserData userData;
    private final Object principal;

    public UserAuthentication(UserData userData) {
        super(userData.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        this.userData = userData;
        this.principal = userData.getUsername();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public Long getUserId() {
        return userData.getUserId();
    }
}
