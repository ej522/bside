package com.example.beside.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MoimServiceTest {

    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;

    @Autowired
    private MoimService moimService;

    private String moimName = "테스트 모임";
    private int deadLineHour = 5;
    private List<MoimDate> moimDates = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        MoimDate moimDate1 = new MoimDate();
        moimDate1.setSelected_date(LocalDate.parse("2023-03-10", formatter).atStartOfDay());
        moimDate1.setMorning(false);
        moimDate1.setAfternoon(false);
        moimDate1.setEvening(true);

        MoimDate moimDate2 = new MoimDate();
        moimDate2.setSelected_date(LocalDate.parse("2023-03-13", formatter).atStartOfDay());
        moimDate2.setMorning(true);
        moimDate2.setAfternoon(true);
        moimDate2.setEvening(true);

        moimDates.add(moimDate1);
        moimDates.add(moimDate2);
    }

    @Test
    @DisplayName("모임 생성")
    void testMakeMoim() throws Exception {
        // given
        User user = new User();
        user.setId((long) 1);

        Moim newMoim = new Moim();
        newMoim.setUser(user);
        newMoim.setMoim_name(moimName);
        newMoim.setDead_line_hour(deadLineHour);

        // when
        String encryptMoimID = moimService.makeMoim(user, newMoim, moimDates);

        // then
        System.out.println(encryptMoimID);
        Assertions.assertThat(encryptMoimID).isNotNull();
    }
}
