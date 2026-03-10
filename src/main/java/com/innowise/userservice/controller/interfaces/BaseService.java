package com.innowise.userservice.controller.interfaces;

import java.util.List;

public interface BaseService<RES, REQ, ID> {

    List<RES> readAll();

    RES readById(ID id);

    RES create(REQ createRequest);

    RES update(REQ updateRequest);

    void deleteById(ID id);
}
