/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Event;
import com.usrun.core.model.EventParticipant;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhhuy
 */
@Repository
public interface EventParticipantRepostitory {
    
    int insert(EventParticipant eventParticipant);
    List<EventParticipant> findByUserId(long userId);
    boolean delete(EventParticipant eventParticipant);
}
