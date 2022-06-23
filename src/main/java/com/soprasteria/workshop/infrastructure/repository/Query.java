package com.soprasteria.workshop.infrastructure.repository;

import java.util.stream.Stream;

public interface Query<T> {
    Stream<T> stream();
}
