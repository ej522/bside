package com.example.beside.repository;

import com.example.beside.domain.QUser;
import com.example.beside.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    public long saveUser(User user) {
        queryFactory = new JPAQueryFactory(em);

        QUser qUser = new QUser("u");
        queryFactory.insert(qUser)
                .columns(qUser.social_type, qUser.name, qUser.email, qUser.password, qUser.profile_image)
                .values(user.getSocial_type(), user.getName(), user.getEmail(), user.getPassword(),
                        user.getProfile_image())
                .execute();

        return queryFactory.select(qUser.id)
                .from(qUser).where(qUser.email.eq(user.getEmail())).fetchOne();

    }

    public void deleteUser(User user) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        queryFactory.delete(qUser)
                .where(qUser.email.eq(user.getEmail())
                        .and(qUser.password.eq(user.getPassword())))
                .execute();
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

    public User findUserByEmail(String email) {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        User result = queryFactory.selectFrom(qUser)
                .from(qUser)
                .where(qUser.email.eq(email))
                .fetchOne();

        if (result == null) {
            return null;
        }

        return result;
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

}
