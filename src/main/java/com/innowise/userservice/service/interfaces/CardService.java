package com.innowise.userservice.service.interfaces;

import java.util.List;

public interface CardService<RES, REQ, ID> extends BaseService<RES, REQ, ID> {

    List<RES> readAllCardsByUserId(ID id);

    RES activateCard(ID id);

    RES deactivateCard(ID id);
}
