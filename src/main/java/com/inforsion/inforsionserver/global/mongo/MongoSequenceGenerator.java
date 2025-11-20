package com.inforsion.inforsionserver.global.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoSequenceGenerator {

    private final MongoOperations mongoOperations;

    public int getNextSequence(String sequenceName) {
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true)
                .upsert(true);

        DatabaseSequence sequence = mongoOperations.findAndModify(query, update, options, DatabaseSequence.class);
        if (sequence == null) {
            throw new IllegalStateException("시퀀스를 생성하지 못했습니다: " + sequenceName);
        }
        if (sequence.getSeq() > Integer.MAX_VALUE) {
            throw new IllegalStateException("시퀀스가 Integer 범위를 초과했습니다: " + sequenceName);
        }
        return (int) sequence.getSeq();
    }
}
