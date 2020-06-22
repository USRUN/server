/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.repository;

import com.usrun.core.model.Event;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * @author anhhuy
 */
@Repository
public interface EventRepository {

  int insert(Event event);

  Event findById(long id);

  List<Event> findByName(String name);

  boolean delete(Event delete);
}
