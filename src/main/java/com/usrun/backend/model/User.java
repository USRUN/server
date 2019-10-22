package com.usrun.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.usrun.backend.model.audit.DateAudit;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Size(max = 100)
    @JsonIgnore
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthType type;

    private String openId;

    private String img;

    private Instant lastLogin;

    private Double weight;

    private Double height;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Instant birthday;

    private String code;

    private boolean isEnabled = true;

    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {}

    public User(@NotBlank @Size(max = 50) String name, @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 100) String password, @NotNull @Size(max = 20) AuthType type, String openId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.openId = openId;
    }
}
