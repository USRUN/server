package com.usrun.core.service;

import com.usrun.core.config.ErrorCode;
import com.usrun.core.exception.CodeException;
import com.usrun.core.exception.PostException;
import com.usrun.core.model.Post;
import com.usrun.core.model.User;
import com.usrun.core.repository.PostRepository;
import com.usrun.core.utility.CacheClient;
import com.usrun.core.utility.SequenceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author phuctt4
 */

@Service
public class PostService {
    public static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private SequenceGenerator sequenceGenerator;
//
//    @Autowired
//    private CacheClient cacheClient;
//
//    @Autowired
//    private AmazonClient amazonClient;
//
//
//    public Post post(Post post) {
//        long userId = post.getUser().getUserId();
//        User user = userService.loadUser(userId);
//
//        Set<Long> teams = user.getTeams();
//        if(teams.isEmpty()) {
//            String msg = String.format("User %s have not team", userId);
//            LOGGER.warn(msg);
//            throw new PostException(msg, ErrorCode.ACTIVITY_ADD_FAIL);
//        }
//
//        post.setTeams(teams);
//
//        long postId = sequenceGenerator.nextId();
//        post.setPostId(postId);
////        post.getImages().forEach(image -> amazonClient.uploadFile(image, "post-" + (indexImage++) +));
//
//        postRepository.save(post);
//        cacheClient.setPost(postId, post);
//
//        return post;
//    }
//
//    public Post loadPost(long postId) {
//        Post post = cacheClient.getPost(postId);
//        if(post == null) {
//            post = postRepository.findById(postId).orElseThrow(
//                    () -> new CodeException(ErrorCode.ACTIVITY_NOT_FOUND)
//            );
//        }
//        return post;
//    }
//
//    public List<Post> getPosts(long teamId, int count, int offset) {
//        List<Long> postIds = cacheClient.getPostByTeam(teamId, count , offset);
//        return postIds.stream().map(id -> loadPost(id)).collect(Collectors.toList());
//    }
}
