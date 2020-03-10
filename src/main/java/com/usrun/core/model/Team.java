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

    private String description;

    public Team(String teamName, String thumbnail, String location, int privacy,Date createTime,String description){
        this.teamName =teamName;
        this.thumbnail = thumbnail;
        this.location = location;
        this.privacy = privacy;
        this.totalMember = 1;
        this.verified = false;
        this.deleted = false;
        this.createTime = createTime;
        this.description = description;
    }

    public Team(Long id, int privacy, int totalMember, String teamName, String thumbnail, boolean verified, boolean deleted, Date createTime, String location, String description){
        this.id = id;
        this.teamName =teamName;
        this.thumbnail = thumbnail;
        this.location = location;
        this.privacy = privacy;
        this.totalMember = totalMember;
        this.verified = verified;
        this.deleted = deleted;
        this.createTime = createTime;
        this.description = description;
    }
}
