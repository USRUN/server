package com.usrun.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.usrun.backend.model.audit.DateAudit;
import com.usrun.backend.model.type.AuthType;
import com.usrun.backend.model.type.Gender;
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
    @Enumerated(EnumType.ORDINAL)
    private AuthType type;

    private String openId;

    private String img;

    private Instant lastLogin;

    private Double weight;

    private Double height;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;

    private Instant birthday;

    private String code;

    private String deviceToken;

    @Size(max = 50)
    private String nameSlug;

    @JsonProperty("isActive")
    private boolean isEnabled = true;

    private boolean hcmus = false;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<Team> teams = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<Event> events = new HashSet<>();

    public User() {}

    public User(@NotBlank @Size(max = 50) String name, @NotBlank @Size(max = 50) @Email String email, @NotBlank @Size(max = 100) String password, @NotNull @Size(max = 20) AuthType type, String openId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.type = type;
        this.openId = openId;
    }
}
