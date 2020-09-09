/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.usrun.core.controller.admin;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Organization;
import com.usrun.core.model.Sponsor;
import com.usrun.core.model.type.SponsorType;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.sponsor.OrganizationCreateReq;
import com.usrun.core.payload.sponsor.OrganizationListReq;
import com.usrun.core.payload.sponsor.SponsorCreateReq;
import com.usrun.core.repository.OrganizationRepository;
import com.usrun.core.repository.SponsorRepository;
import com.usrun.core.service.ImageClient;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huyna3
 */
@Controller
@RequestMapping("/admin/sponsor")
public class AdminOrganizationController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrganizationController.class);

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private SponsorRepository sponsorRepository;

    @Autowired
    private ImageClient imageClient;

    @PostMapping("/createOrUpdate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationCreateReq organizationReq) {
        try {
            String newLinkAvatar = "";
            if (!organizationReq.getAvatar().isEmpty()) {
                newLinkAvatar = imageClient.uploadFile(organizationReq.getAvatar());
            }
            int resp = -1;
            if (organizationReq.getId() == 0) {
                Organization organization = new Organization(organizationReq.getName(), newLinkAvatar, organizationReq.getWebsite(), organizationReq.getDescription());
                resp = organizationRepository.insert(organization);
            } else {
                Organization organization = organizationRepository.findById(organizationReq.getId());
                organization.setDescription(organizationReq.getDescription());
                organization.setName(organizationReq.getName());
                organization.setWebsite(organizationReq.getWebsite());
                if (!newLinkAvatar.isEmpty()) {
                    organization.setAvatar(newLinkAvatar);
                }
                resp = organizationRepository.update(organization);
            }

            if (resp >= 0) {
                return ResponseEntity.ok(new CodeResponse("add success"));
            } else {
                return new ResponseEntity<>(new CodeResponse(resp), HttpStatus.BAD_REQUEST);
            }
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addSponsor")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSponsor(@RequestBody SponsorCreateReq sponsorCreateReq) {
        try {
            if (sponsorCreateReq.getRole() > 5 || sponsorCreateReq.getRole() < 1) {
                return ResponseEntity.ok(new CodeResponse(ErrorCode.ROLE_SPONSOR_INVALID));
            }
            List<Long> organizationId = sponsorCreateReq.getOrganizationId();
            for (Long oId : organizationId) {
                Sponsor sponsor = new Sponsor(sponsorCreateReq.getEventId(), oId,
                        SponsorType.getSponsor(sponsorCreateReq.getRole()));
                sponsorRepository.insert(sponsor);
            }
            return ResponseEntity.ok(new CodeResponse(ErrorCode.ROLE_SPONSOR_INVALID));
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/listOrganization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listOrganization() {
        try {
            List<Organization> result = organizationRepository
                    .listAllOrganization();
            return new ResponseEntity<>(new CodeResponse(result), HttpStatus.OK);
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }
}
