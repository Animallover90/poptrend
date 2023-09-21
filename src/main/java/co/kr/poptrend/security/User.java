package co.kr.poptrend.security;

import co.kr.poptrend.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class User implements UserDetails {

    private final Long id;
    private final String oauthId;
    private final String role;

    public User(Member member) {
        this.id = member.getId();
        this.oauthId = member.getOauthId();
        this.role = String.valueOf(member.getRole());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", oauthId='" + oauthId + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(this.role));
        return collection;
    }

    @Override
    public String getPassword() {
        return this.oauthId;
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
