/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Event;
import com.usrun.core.model.Love;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anhhuy
 */
@Repository
public interface EventRepository {
    Event insert(Event event);
    boolean delete(Event delete);
}
