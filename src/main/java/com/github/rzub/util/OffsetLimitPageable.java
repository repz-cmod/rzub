package com.github.rzub.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class OffsetLimitPageable extends PageRequest {
    private int offset;

    public OffsetLimitPageable(int offset, int limit) {
        super(offset, limit, Sort.by(Sort.Direction.ASC, "id"));
        this.offset = offset;
    }

    public OffsetLimitPageable(int offset, int limit, Sort sort) {
        super(offset, limit, sort);
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return this.offset;
    }
}
