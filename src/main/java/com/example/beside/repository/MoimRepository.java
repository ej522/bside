package com.example.beside.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MoimRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) {
        // // 모임 생성
        em.persist(moim);
        em.flush();
        // 모임 일정 정보 생성
        for (var moim_date : moim_date_list) {
            em.persist(moim_date);
            moim_date.setMoim(moim);
            em.flush();
        }
        return moim.getId();
    }
}