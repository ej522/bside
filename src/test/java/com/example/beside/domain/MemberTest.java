package com.example.beside.domain;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Transactional
//@Commit
class MemberTest {

    @Autowired
    EntityManager em;

    @Test
    public void testEntity(){
        // given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        List<Member> memberList = new ArrayList<Member>();
        memberList.add(new Member("member1", 10, teamA));
        memberList.add(new Member("member2", 20, teamA));
        memberList.add(new Member("member3", 30, teamB));
        memberList.add(new Member("member4", 40, teamB));

        // when
        em.persist(memberList.get(0));
        em.persist(memberList.get(1));
        em.persist(memberList.get(2));
        em.persist(memberList.get(3));
        em.flush();
        em.clear();

        // then
        List<Member> members = em.createQuery("select m from Member m order by m.id", Member.class)
                            .getResultList();

        for(int i =0; i< memberList.size(); i++){
            Member member = members.get(i);
            Member testMember = members.get(i);
            Member originMember = memberList.get(i);

            Assertions.assertThat(testMember.getAge()).isEqualTo(originMember.getAge());
            Assertions.assertThat(testMember.getUsername()).isEqualTo(originMember.getUsername());
            Assertions.assertThat(testMember.getTeam().getName()).isEqualTo(originMember.getTeam().getName());
        }
    }
}