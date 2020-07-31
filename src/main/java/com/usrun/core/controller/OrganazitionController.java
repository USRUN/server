/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Organization;
import com.usrun.core.model.Sponsor;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.sponsor.OrganizationCreateReq;
import com.usrun.core.payload.sponsor.SponsorCreateReq;
import com.usrun.core.repository.OrganizationRepository;
import com.usrun.core.repository.SponsorRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huyna3
 */
@Controller
@RequestMapping("/sponsor")
public class OrganazitionController {

  private static final Logger logger = LoggerFactory.getLogger(OrganazitionController.class);

  private static final OrganazitionController INSTANCE = new OrganazitionController();

  private OrganazitionController() {
  }

  @Autowired
  private OrganizationRepository organizationRepository;

  @Autowired
  private SponsorRepository sponsorRepository;

  @PostMapping("/createOrganization")
  public ResponseEntity<?> createOrganization(@RequestBody OrganizationCreateReq organizationReq) {
    try {
      Organization organization = new Organization(organizationReq.getName());
      int resp = organizationRepository.insert(organization);
      if (resp >= 0) {
        return ResponseEntity.ok(new CodeResponse("add success"));
      } else {
        return ResponseEntity.ok(new CodeResponse(resp));
      }
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

  @PostMapping("/addSponsor")
  public ResponseEntity<?> addSponsor(@RequestBody SponsorCreateReq sponsorCreateReq) {
    try {
      List<Long> organizationId = sponsorCreateReq.getOrganizationId();
      for (Long oId : organizationId) {
        Sponsor sponsor = new Sponsor(sponsorCreateReq.getEventId(), oId);
        sponsorRepository.insert(sponsor);
      }
      return ResponseEntity.ok(new CodeResponse(""));
    } catch (CodeException ex) {
      return ResponseEntity.ok(new CodeResponse(ex.getErrorCode()));
    } catch (Exception ex) {
      logger.error("", ex);
      return ResponseEntity.ok(new CodeResponse(ErrorCode.SYSTEM_ERROR));
    }
  }

}
