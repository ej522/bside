package com.example.beside.repository;

import com.example.beside.domain.Alarm;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FcmPushRepositoryImpl implements FcmPushRepository{
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @Override
    public void insertAlarm(Alarm alarm) {
        em.persist(alarm);
        em.flush();
    }

}
