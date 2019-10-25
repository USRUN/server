package com.usrun.backend.model;

import com.usrun.backend.model.audit.DateAudit;
import com.usrun.backend.model.type.EventType;
import com.usrun.backend.model.type.SportType;
import com.usrun.backend.model.type.UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
public class Team extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String img;

    private String logo;

    private boolean isActive;

    @Size(max = 50)
    private String slug;

    private boolean isVerified;

    private Long memberCount;

    @Enumerated(EnumType.ORDINAL)
    private SportType sportType;

    @Enumerated(EnumType.ORDINAL)
    private UserType userType;

    @Enumerated(EnumType.ORDINAL)
    private EventType eventType;

    @ManyToMany(cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST
    })
    @JoinTable(
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();
}
