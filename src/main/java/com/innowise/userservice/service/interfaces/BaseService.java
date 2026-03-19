package com.innowise.userservice.service.interfaces;

import java.util.List;

public interface BaseService<S, Q, I> {

    List<S> readAll();

    S readById(I id);

    S create(Q createRequest);

    S update(I id, Q updateRequest);

    void deleteById(I id);
}
