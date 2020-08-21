/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Sponsor;
import com.usrun.core.payload.event.EventOrganization;
import java.util.List;

/**
 * @author huyna3
 */
public interface SponsorRepository {

  Sponsor insert(Sponsor sponsor);

  Sponsor findById(long id);
  
  int[] addOrganizers(long eventId, List<Long> organizationIds);
  
  List<EventOrganization> getEventOrganizationWithRole(long eventId, int role);
}
