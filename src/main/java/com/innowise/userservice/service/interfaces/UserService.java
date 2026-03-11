package com.innowise.userservice.service.interfaces;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService<RES, REQ, ID> extends BaseService<RES, REQ, ID> {

    Page<RES> readAll(REQ requestedNameAndSurname, Pageable pageable);

    RES activateUser(ID id);

    RES deactivateUser(ID id);
}
