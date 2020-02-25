package com.usrun.core.payload.dto;

import com.usrun.core.model.type.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

import java.util.Date;

/**
 * @author phuctt4
 */

@Getter
@Setter
public class UserFilterDTO {
    private Long id;
    private String name;
    private String email;
    private String code;
    private Gender gender;
    private Date birthday;

    public UserFilterDTO() {
    }

    public UserFilterDTO(Long id, String name, String email, String code, Gender gender, Date birthday) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.code = code;
        this.gender = gender;
        this.birthday = birthday;
    }
}
