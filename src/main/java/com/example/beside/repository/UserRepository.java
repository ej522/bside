package com.example.beside.repository;

import com.example.beside.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;

    public void saveUser(User user) {
        em.persist(user);
    }

    public void deleteUser(User user) {
        User userInfo = em.find(User.class, user.getId());
        if (userInfo == null)
            throw new RuntimeException("해당 유저가 없습니다");

        em.remove(userInfo);
        em.flush();
    }

    public User findUserById(Long id) {
        return em.find(User.class, id);
    }

    public User findUserByEmail(String email) {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList().get(0);
    }

    public List<User> findUserAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

}
