/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.payload.sponsor;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author huyna3
 */
@Getter
@Setter
@Data
public class OrganizationListReq {

  int offset;
  int limit;
  String keyword;
}
