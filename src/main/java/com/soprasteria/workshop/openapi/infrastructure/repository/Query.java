package com.soprasteria.workshop.openapi.infrastructure.repository;

import java.util.stream.Stream;

public interface Query<T> {
    Stream<T> stream();
}
