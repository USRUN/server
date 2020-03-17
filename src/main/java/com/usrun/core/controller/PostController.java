package com.usrun.core.controller;

import com.usrun.core.model.Post;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author phuctt4
 */

@RestController
@RequestMapping("/post")
public class PostController {

    @PostMapping("/")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> post (
            @CurrentUser UserPrincipal userPrincipal,
            Post post
            ) {
        Post.User user = post.getUser();
        user.setUserId(userPrincipal.getId());
        user.setName(userPrincipal.getName());
        user.setAvatar(userPrincipal.getAvatar());

        return ResponseEntity.ok(new CodeResponse(0));
    }

}
