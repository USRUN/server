package com.usrun.core.model;

import com.usrun.core.model.audit.DateAudit;
import com.usrun.core.model.type.EventType;
import com.usrun.core.model.type.LeagueType;
import com.usrun.core.model.type.SportType;
import com.usrun.core.model.type.UserType;
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

    private Long memberCount = 0L;

    @Enumerated(EnumType.ORDINAL)
    private LeagueType leagueType;

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

    @OneToOne(mappedBy = "team")
    private User user;
}
