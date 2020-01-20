//package com.usrun.core.model;
//
//import com.usrun.core.model.type.EventType;
//import com.usrun.core.model.type.SportType;
//import com.usrun.core.model.type.UserType;
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.*;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.Size;
//import java.time.Instant;
//import java.util.HashSet;
//import java.util.Set;
//
//@Entity
//@Table(name = "events")
//@Getter
//@Setter
//public class Event {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotBlank
//    @Size(max = 50)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    private Instant startDate;
//
//    private Instant endDate;
//
//    private String img;
//
//    private String logo;
//
//    private boolean isActive;
//
//    private Long memberCount;
//
//    private Long teamCount;
//
//    private Long distance;
//
//    @Enumerated(EnumType.ORDINAL)
//    private EventType eventType;
//
//    @Enumerated(EnumType.ORDINAL)
//    private SportType sportType;
//
//    @Enumerated(EnumType.ORDINAL)
//    private UserType userType;
//
//    @ManyToMany(cascade = {
//            CascadeType.MERGE,
//            CascadeType.PERSIST
//    })
//    @JoinTable(
//            joinColumns = @JoinColumn(name = "event_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private Set<User> users = new HashSet<>();
//}
