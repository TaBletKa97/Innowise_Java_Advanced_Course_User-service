package com.innowise.userservice.repository.interfaces;

public interface BaseEntity<K> {
    K getId();
    void setId(K id);
}
