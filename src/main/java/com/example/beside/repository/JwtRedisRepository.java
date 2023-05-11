package com.example.beside.repository;

import com.example.beside.domain.JwtRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JwtRedisRepository extends CrudRepository<JwtRedis, String> {
    @Override
    Optional<JwtRedis> findById(String key);
}
