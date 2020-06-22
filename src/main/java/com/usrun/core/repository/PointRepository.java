package com.usrun.core.repository;

import com.usrun.core.model.track.Point;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author phuctt4
 */

@Repository
public interface PointRepository extends MongoRepository<Point, Long> {

  List<Point> findAllByTrackId(Long trackId);
}
