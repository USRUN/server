package com.usrun.core.service;

import com.usrun.core.model.Post;
import com.usrun.core.model.User;
import com.usrun.core.repository.PostRepository;
import com.usrun.core.utility.SequenceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author phuctt4
 */

@Service
public class PostService {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SequenceGenerator sequenceGenerator;

    public Post post(Post post) {
        long userId = post.getUser().getUserId();
        User user = userService.loadUser(userId);
        long postId = sequenceGenerator.nextId();
        Set<Long> teams = user.getTeams();
//        if(teams.isEmpty()) {
//            throw new
//        }
        post.setPostId(postId);
        post.setTeams(teams);

        postRepository.save(post);
        return null;
    }
}
