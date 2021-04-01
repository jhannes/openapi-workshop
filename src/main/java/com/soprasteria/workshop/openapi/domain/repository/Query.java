package com.soprasteria.workshop.openapi.domain.repository;

import java.util.stream.Stream;

public interface Query<T> {
    Stream<T> stream();
}
