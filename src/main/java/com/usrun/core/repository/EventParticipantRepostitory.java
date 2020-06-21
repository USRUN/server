/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.EventParticipant;
import org.springframework.stereotype.Repository;

/**
 * @author anhhuy
 */
@Repository
public interface EventParticipantRepostitory {

  EventParticipant insert(EventParticipant eventParticipant);

  boolean delete(EventParticipant eventParticipant);
}
