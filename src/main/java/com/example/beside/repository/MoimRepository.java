package com.example.beside.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.beside.domain.Friend;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.QFriend;
import com.example.beside.domain.QMoim;
import com.example.beside.domain.QMoimDate;
import com.example.beside.domain.QMoimMember;
import com.example.beside.domain.QUser;
import com.example.beside.domain.User;
import com.example.beside.dto.MoimOveralDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MoimRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public Moim getMoimInfo(Long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = new QMoim("moim");
        Moim result = queryFactory.selectFrom(qMoim)
                .from(qMoim)
                .where(qMoim.id.eq(moimId))
                .fetchOne();

        return result;
    }

    public List<MoimMember> getMoimMembers(Long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;
        List<MoimMember> result = queryFactory
                .selectFrom(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId))
                .fetch();

        return result;
    }

    public Boolean alreadyJoinedMoim(Long moimId, Long userId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;

        MoimMember fetchOne = queryFactory.selectFrom(qMoimMember)
                .from(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId)
                        .and(qMoimMember.member_id.eq(userId)))
                .fetchOne();

        if (fetchOne == null)
            return false;

        return true;
    }

    public List<MoimOveralDto> getMoimOveralInfo(Long moinId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimDate qMoimDate = QMoimDate.moimDate;
        QMoim qMoim = QMoim.moim;
        QUser qUser = QUser.user;

        List<MoimOveralDto> result = queryFactory.select(Projections.constructor(MoimOveralDto.class,
                qMoim.id, qMoim.user.id, qUser.name, qMoim.moim_name, qMoim.dead_line_hour,
                qMoimDate.morning, qMoimDate.afternoon, qMoimDate.evening, qMoimDate.selected_date))
                .from(qMoim)
                .leftJoin(qMoimDate).on(qMoim.id.eq(qMoimDate.moim.id))
                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                .where(qMoim.id.eq(moinId))
                .fetch();

        return result;
    }

    public long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) {
        // 모임 생성
        moim.setCreated_time(LocalDateTime.now());
        em.persist(moim);

        em.flush();
        // 모임 일정 정보 생성
        for (var moim_date : moim_date_list) {
            em.persist(moim_date);
            moim_date.setMoim(moim);
        }
        em.flush();
        return moim.getId();
    }

    public long makeMoimMember(User user, Moim moim) {
        MoimMember moimMember = new MoimMember();
        moimMember.setMember_id(user.getId());
        moimMember.setMember_name(user.getName());
        moimMember.setMoim(moim);

        em.persist(moimMember);
        em.flush();
        return moimMember.getId();
    }

    public long makeFriend(User user, Moim moim) {
        Long user_id = moim.getUser().getId();
        Long friend_id = user.getId();

        queryFactory = new JPAQueryFactory(em);
        QFriend qFriend = QFriend.friend;
        List<Friend> result = queryFactory.selectFrom(qFriend)
                .where(qFriend.user.id.eq(user_id)
                        .and(qFriend.member_id.eq(friend_id)))
                .fetch();

        // 기존 친구 확인
        if (result.size() > 0)
            return -1;

        Friend friend = new Friend();

        friend.setFirst_moim_id(moim.getId());
        friend.setUser(moim.getUser());
        friend.setMember_id(user.getId());
        friend.setCreate_time(LocalDateTime.now());

        em.persist(friend);
        em.flush();
        return friend.getId();
    }
}