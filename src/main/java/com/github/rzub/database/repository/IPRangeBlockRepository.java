package com.github.rzub.database.repository;

import com.github.rzub.database.entity.IPRangeBlockEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;

public interface IPRangeBlockRepository extends MongoRepository<IPRangeBlockEntity, String> {
    @Query(value = "{startLong: {$gt: ?0}, endLong: {$lt: ?0}}", exists = true)
    boolean existsByRange(long input);
    void deleteAllByExpirationBefore(Date date);
}
