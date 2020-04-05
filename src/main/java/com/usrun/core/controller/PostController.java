package com.usrun.core.controller;

import com.usrun.core.exception.CodeException;
import com.usrun.core.model.Post;
import com.usrun.core.payload.CodeResponse;
import com.usrun.core.payload.post.GetPostRequest;
import com.usrun.core.payload.post.GetPostsRequest;
import com.usrun.core.security.CurrentUser;
import com.usrun.core.security.UserPrincipal;
import com.usrun.core.service.PostService;
import org.bson.types.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author phuctt4
 */

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> post (
            @CurrentUser UserPrincipal userPrincipal,
            @RequestBody Post post
            ) {
        Post.User user = post.getUser();
        user.setUserId(userPrincipal.getId());
        user.setName(userPrincipal.getName());
        user.setAvatar(userPrincipal.getAvatar());

        postService.post(post);

        return ResponseEntity.ok(new CodeResponse(post));
    }

    @PostMapping("/getpost")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication, 'MEMBER', #request.teamId)")
    public ResponseEntity<?> getPost(
            @RequestBody GetPostRequest request
            ) {
        try {
            Post post = postService.loadPost(request.getPostId());
            return ResponseEntity.ok(new CodeResponse(post));
        } catch (CodeException ex) {
            return new ResponseEntity<>(new CodeResponse(ex.getErrorCode()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/getposts")
    @PreAuthorize("hasRole('USER') && @teamAuthorization.authorize(authentication, 'MEMBER', #request.teamId)")
    public ResponseEntity<?> getPosts(
            @RequestBody GetPostsRequest request
    ) {
        List<Post> posts = postService.getPosts(request.getTeamId(), request.getCount(), request.getOffset());
        return ResponseEntity.ok(new CodeResponse(posts));
    }

}
