package com.innowise.userservice.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService<S, Q, I> extends BaseService<S, Q, I> {

    Page<S> readAll(Q requestedNameAndSurname, Pageable pageable);

    S activateUser(I id);

    S deactivateUser(I id);
}
