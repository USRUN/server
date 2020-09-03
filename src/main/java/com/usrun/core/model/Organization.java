/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huyna3
 */
@Getter
@Setter
public class Organization {

  private long id;
  private String name;
  private String avatar;
  private String website;
  private String description;

  public Organization(long id, String name) {
    this.id = id;
    this.name = name;
  }

  public Organization(long id, String name, String avatar, String website, String description) {
    this.id = id;
    this.name = name;
    this.avatar = avatar;
    this.website = website;
    this.description = description;
  }

  public Organization(String name) {
    this.name = name;
  }
}
