package com.usrun.core.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@Setter
@Table("team")
public class Team {
    @Id
    private Long id;

    private int privacy;

    private int totalMember;

    private String teamName;

    private String thumbnail;

    private boolean verified;

    private boolean deleted;

    private Date createTime;

    private String location;
}
