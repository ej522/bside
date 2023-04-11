package com.example.beside.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.beside.common.Exception.MoimParticipateException;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.example.beside.util.Encrypt;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application.yml")
public class MoimServiceTest {

    @Value("${spring.secret.algorithm}")
    private String algorithm;
    @Value("${spring.secret.transformation}")
    private String transformation;
    @Value("${spring.secret.key}")
    private String secret_key;

    @Mock
    private Encrypt mockEncrypt;

    @Autowired
    private UserService userService;

    @Autowired
    private MoimService moimService;

    private List<MoimDate> normalMoimDates = new ArrayList<>();
    private List<MoimDate> wrongMoimDates = new ArrayList<>();

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

        normalMoimDates.add(moimDate1);
        normalMoimDates.add(moimDate2);
    }

    @Test
    @DisplayName("모임 생성")
    void testMakeMoim() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, normalMoimDates);

        // then
        System.out.println(encryptMoimID);
        Assertions.assertThat(encryptMoimID).isNotNull();
    }

    @Test
    @DisplayName("중복된 날짜를 가진 모임 생성")
    void testMakeMoimWithWrongDateList() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, wrongMoimDates);

        // then
        System.out.println(encryptMoimID);
        Assertions.assertThat(encryptMoimID).isNotNull();
    }

    @Test
    @DisplayName("모임 참여하기")
    void testParticipateMoim() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);
        User findUserById2 = userService.findUserById((long) 2);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, normalMoimDates);

        // when
        Map<String, Object> participateMoim = moimService.participateMoim(findUserById2, encryptMoimID);

        // then
        Assertions.assertThat(participateMoim.get("moim_name")).isEqualTo("테스트 모임");
        Assertions.assertThat(participateMoim.get("dead_line_hour")).isEqualTo(5);
    }

    @Test
    @DisplayName("모임 주최자가 만든 모임 참여하기")
    void testParticipateMoimByMoimCreator() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, normalMoimDates);

        // when, then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(findUserById, encryptMoimID));
    }

    @Test
    @DisplayName("기존 참여한 모임 다시 참여하기")
    void testParticipateMoimByAlreadyJoinedPeople() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);
        User findUserById2 = userService.findUserById((long) 2);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, normalMoimDates);

        // when
        moimService.participateMoim(findUserById2, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(findUserById2, encryptMoimID));
    }

    @Test
    @DisplayName("11명 이상 모임 참여하기")
    void testParticipateMoimByMoreThanTenPeople() throws Exception {
        // given
        User findUserById = userService.findUserById((long) 1);
        User findUserById2 = userService.findUserById((long) 2);
        User findUserById3 = userService.findUserById((long) 52);
        User findUserById4 = userService.findUserById((long) 53);
        User findUserById5 = userService.findUserById((long) 54);
        User findUserById6 = userService.findUserById((long) 55);
        User findUserById7 = userService.findUserById((long) 56);
        User findUserById8 = userService.findUserById((long) 57);
        User findUserById9 = userService.findUserById((long) 58);
        User findUserById10 = userService.findUserById((long) 59);
        User findUserById11 = userService.findUserById((long) 60);
        User findUserById12 = userService.findUserById((long) 61);

        Moim newMoim = new Moim();
        newMoim.setUser(findUserById);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        String encryptMoimID = moimService.makeMoim(findUserById, newMoim, normalMoimDates);

        // when
        moimService.participateMoim(findUserById2, encryptMoimID);
        moimService.participateMoim(findUserById3, encryptMoimID);
        moimService.participateMoim(findUserById4, encryptMoimID);
        moimService.participateMoim(findUserById5, encryptMoimID);
        moimService.participateMoim(findUserById6, encryptMoimID);
        moimService.participateMoim(findUserById7, encryptMoimID);
        moimService.participateMoim(findUserById8, encryptMoimID);
        moimService.participateMoim(findUserById9, encryptMoimID);
        moimService.participateMoim(findUserById10, encryptMoimID);
        moimService.participateMoim(findUserById11, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(findUserById12, encryptMoimID));
    }
}
