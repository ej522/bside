package com.example.beside.repository;

import com.example.beside.domain.Moim;
import com.example.beside.domain.QFriend;
import com.example.beside.domain.QMoim;
import com.example.beside.domain.QMoimDate;
import com.example.beside.domain.QMoimMember;
import com.example.beside.domain.QMoimMemberTime;
import com.example.beside.domain.QUser;
import com.example.beside.domain.User;
import com.example.beside.dto.FriendDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public User saveUser(User user) {
        user.setCreated_time(LocalDateTime.now());
        em.persist(user);

        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");
        return queryFactory.select(qUser)
                .from(qUser).where(qUser.email.eq(user.getEmail())).fetchOne();
    }

    public void deleteUser(User user) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = QUser.user;
        QFriend qFriend = QFriend.friend;
        QMoim qMoim = QMoim.moim;
        QMoimDate qMoimDate = QMoimDate.moimDate;
        QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;
        QMoimMember qMoimMember = QMoimMember.moimMember;

        // 모임 삭제
        List<Moim> moimList = queryFactory.selectFrom(qMoim).where(qMoim.user.eq(user)).fetch();
        for (var moim : moimList) {
            // 모임날짜 삭제
            queryFactory.delete(qMoimDate).where(qMoimDate.moim.eq(moim)).execute();
            // 모임멤버 삭제
            queryFactory.delete(qMoimMember).where(qMoimMember.moim.eq(moim)).execute();
            // 모임멤버 시간 삭제
            queryFactory.delete(qMoimMemberTime).where(qMoimMemberTime.moim.eq(moim)).execute();
            // 모임 삭제
            queryFactory.delete(qMoim).where(qMoim.eq(moim)).execute();
        }

        // 친구 삭제
        queryFactory.delete(qFriend).where(qFriend.user.eq(user)).execute();

        // 유저 삭제
        queryFactory.delete(qUser).where(qUser.eq(user)).execute();
        em.flush();
        em.clear();
    }

    public Optional<User> findUserByEmailAndPassword(String email) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");
        User result = queryFactory.selectFrom(qUser)
                .from(qUser)
                .where(qUser.email.eq(email))
                .fetchOne();

        if (result == null)
            return Optional.empty();

        return Optional.ofNullable(result);
    }

    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    public Optional<User> findUserByEmail(String email) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        User result = queryFactory.selectFrom(qUser)
                .from(qUser)
                .where(qUser.email.eq(email))
                .fetchOne();

        if (result == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(result);
    }

    public Optional<User> findUserByEmailAndSocialType(String email, String social_type) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        User result = queryFactory.selectFrom(qUser)
                .from(qUser)
                .where(qUser.email.eq(email)
                        .and(qUser.social_type.eq(social_type)))
                .fetchOne();
        if (result == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(result);
    }

    public List<User> findUserAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User updateNickname(User user) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        queryFactory.update(qUser).set(qUser.name, user.getName()).where(qUser.id.eq(user.getId())).execute();

        return queryFactory.selectFrom(qUser).where(qUser.id.eq(user.getId())).fetchOne();
    }

    public Optional<User> findUserNickname(String nickname) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        User result = queryFactory.selectFrom(qUser)
                .where(qUser.name.eq(nickname))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    public User updateProfileImage(User user) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        queryFactory.update(qUser)
                .set(qUser.profile_image, user.getProfile_image())
                .where(qUser.id.eq(user.getId()))
                .execute();

        return queryFactory.selectFrom(qUser).where(qUser.id.eq(user.getId())).fetchOne();
    }

    public User updatePassword(User user) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = QUser.user;

        queryFactory.update(qUser)
                .set(qUser.password, user.getPassword())
                .where(qUser.id.eq(user.getId()))
                .execute();

        return queryFactory.selectFrom(qUser).where(qUser.id.eq(user.getId())).fetchOne();
    }

    public List<FriendDto> findFriendByUserId(Long user_id) {
        queryFactory = new JPAQueryFactory(em);
        QFriend qFriend = QFriend.friend;
        QUser qUser = QUser.user;

        List<FriendDto> result = queryFactory.select(
                Projections.constructor(FriendDto.class,
                        qFriend.first_moim_id,
                        qFriend.member_id,
                        qUser.name))
                .from(qFriend)
                .leftJoin(qUser)
                .on(qFriend.member_id.eq(qUser.id))
                .where(qFriend.user.id.eq(user_id))
                .fetch();

        return result;
    }

}
