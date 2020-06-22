package com.usrun.core.repository;

import com.usrun.core.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author phuctt4
 */

public interface PostRepository extends MongoRepository<Post, Long> {

}
