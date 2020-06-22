/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.EventParticipant;
import java.util.List;

/**
 * @author anhhuy
 */
public interface EventParticipantRepository {

  int insert(EventParticipant eventParticipant);

  List<EventParticipant> findByUserId(long userId);

  boolean delete(EventParticipant eventParticipant);
}
