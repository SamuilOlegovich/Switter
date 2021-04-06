package switter.model.db;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Table;

@Table(name = "user_role")
public enum Role implements GrantedAuthority {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
