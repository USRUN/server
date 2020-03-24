/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.dao;

import com.usrun.core.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**List
 *
 * @author huyna 
 */
@Repository
public interface ActivityDAO extends JpaRepository<UserActivity, Long> {
}
