package com.usrun.core.model;

import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author phuctt4
 */

@Getter
@Setter
@Document(collection = "Post")
public class Post {

  @Getter
  @Setter
  static public class User {

    private long userId;
    private String avatar;
    private String name;
  }

  @Id
  private Long postId;
  private User user = new User();
  private Set<Long> teams;
  private String title;
  private String content;
  private List<String> images;
  private String mapImage;
}
