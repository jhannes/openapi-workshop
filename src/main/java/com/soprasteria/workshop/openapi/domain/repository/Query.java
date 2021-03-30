package com.soprasteria.workshop.openapi.domain.repository;

import java.util.List;

public interface Query<T> {
    List<T> list();
}
