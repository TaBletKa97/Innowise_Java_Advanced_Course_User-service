package com.innowise.userservice.service.interfaces;

import java.util.List;

public interface CardService<S, Q, I> extends BaseService<S, Q, I> {

    List<S> readAllCardsByUserId(I id);

    S activateCard(I id);

    S deactivateCard(I id);
}
