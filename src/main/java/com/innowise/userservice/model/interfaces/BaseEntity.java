package com.innowise.userservice.model.interfaces;

public interface BaseEntity<K> {
    K getId();
    void setId(K id);
}
