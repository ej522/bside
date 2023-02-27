package com.example.beside.repository;


import com.example.beside.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final EntityManager em;

    public void saveUser(User user){
        em.persist(user);
    }

    public User findUserById(Long id){
        return em.find(User.class, id);
    }

    public List<User> findUserAll(){
        return em.createQuery("SELECT u FROM User u" , User.class).getResultList();
    }

}
