package com.usrun.core.security;

import com.usrun.core.model.User;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

  private Long id;
  private String email;
  private String password;
  private boolean isEnabled;
  private boolean hcmus;
  private String avatar;
  private String name;
  private Collection<? extends GrantedAuthority> authorities;

  public UserPrincipal(Long id, String email, String password, boolean isEnabled, boolean hcmus,
      String avatar, String name, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.isEnabled = isEnabled;
    this.hcmus = hcmus;
    this.authorities = authorities;
    this.avatar = avatar;
    this.name = name;
  }

  public static UserPrincipal create(User user) {
//        List<GrantedAuthority> authorities = Collections.
//                singletonList(new SimpleGrantedAuthority("ROLE_USER"));

    List<GrantedAuthority> authorities = user.getRoles().stream().map(role ->
        new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toList());

    return new UserPrincipal(
        user.getId(),
        user.getEmail(),
        user.getPassword(),
        user.isEnabled(),
        user.isHcmus(),
        user.getAvatar(),
        user.getName(),
        authorities
    );
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
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
    return isEnabled;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public boolean isHcmus() {
    return hcmus;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getName() {
    return name;
  }
}
