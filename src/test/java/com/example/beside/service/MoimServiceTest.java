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

import com.example.beside.common.Exception.AdjustScheduleException;
import com.example.beside.common.Exception.MoimParticipateException;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMemberTime;
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
    private List<MoimMemberTime> normalMoimMemberTime = new ArrayList<>();
    private List<MoimMemberTime> wrongMoimMemberTime = new ArrayList<>();
    private User user;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private User user7;
    private User user8;
    private User user9;
    private User user10;
    private User user11;
    private User user12;

    @BeforeEach
    public void setUp() {
        // 유저 세팅
        user = new User();
        user.setName("부엉이");
        user.setEmail("test-user@google.com");
        user.setPassword("Moim@0303");

        user2 = new User();
        user2.setName("다람쥐");
        user2.setEmail("test-user2@google.com");
        user2.setPassword("Moim@0303");

        user3 = new User();
        user3.setName("다람쥐1");
        user3.setEmail("test-user21@google.com");
        user3.setPassword("Moim@0303");

        user4 = new User();
        user4.setName("다람쥐2");
        user4.setEmail("test-user22@google.com");
        user4.setPassword("Moim@0303");

        user5 = new User();
        user5.setName("다람쥐3");
        user5.setEmail("test-user23@google.com");
        user5.setPassword("Moim@0303");

        user6 = new User();
        user6.setName("다람쥐4");
        user6.setEmail("test-user24@google.com");
        user6.setPassword("Moim@0303");

        user7 = new User();
        user7.setName("다람쥐5");
        user7.setEmail("test-user25@google.com");
        user7.setPassword("Moim@0303");

        user8 = new User();
        user8.setName("다람쥐6");
        user8.setEmail("test-user26@google.com");
        user8.setPassword("Moim@0303");

        user9 = new User();
        user9.setName("다람쥐7");
        user9.setEmail("test-user27@google.com");
        user9.setPassword("Moim@0303");

        user10 = new User();
        user10.setName("다람쥐8");
        user10.setEmail("test-user28@google.com");
        user10.setPassword("Moim@0303");

        user11 = new User();
        user11.setName("다람쥐9");
        user11.setEmail("test-user29@google.com");
        user11.setPassword("Moim@0303");

        user12 = new User();
        user12.setName("다람쥐10");
        user12.setEmail("test-user210@google.com");
        user12.setPassword("Moim@0303");

        // 주최자 모임 일정 세팅
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
        moimDate2.setEvening(false);

        MoimDate moimDate3 = new MoimDate();
        moimDate3.setSelected_date(LocalDate.parse("2023-03-13",
                formatter).atStartOfDay());
        moimDate3.setMorning(true);
        moimDate3.setAfternoon(true);
        moimDate3.setEvening(true);

        normalMoimDates.add(moimDate1);
        normalMoimDates.add(moimDate2);
        wrongMoimDates.add(moimDate3);

        // 참여자 모임 일정 세팅
        normalMoimMemberTime = new ArrayList<>();
        wrongMoimMemberTime = new ArrayList<>();
        MoimMemberTime moimTime = new MoimMemberTime();
        MoimMemberTime moimTime2 = new MoimMemberTime();

        moimTime.setSelected_date(LocalDate.parse("2023-03-13", formatter).atStartOfDay());
        moimTime.setAm_nine(false);
        moimTime.setAm_ten(false);
        moimTime.setAm_eleven(false);
        moimTime.setNoon(false);
        moimTime.setPm_one(true);
        moimTime.setPm_two(true);
        moimTime.setPm_three(false);
        moimTime.setPm_four(false);
        moimTime.setPm_five(false);
        moimTime.setPm_six(false);
        moimTime.setPm_seven(false);
        moimTime.setPm_eigth(false);
        moimTime.setPm_nine(false);
        normalMoimMemberTime.add(moimTime);

        moimTime2.setPm_nine(true);
        wrongMoimMemberTime.add(moimTime2);
    }

    @Test
    @DisplayName("모임 생성")
    void testMakeMoim() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        User user = userService.findUserById(userId);

        Moim newMoim = new Moim();
        newMoim.setUser(user);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when
        String encryptMoimID = moimService.makeMoim(user, newMoim, normalMoimDates);

        // then
        Assertions.assertThat(encryptMoimID).isNotNull();
    }

    @Test
    @DisplayName("중복된 날짜를 가진 모임 생성")
    void testMakeMoimWithWrongDateList() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        User user = userService.findUserById(userId);
        Moim newMoim = new Moim();
        newMoim.setUser(user);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when, then
        assertThrows(MoimParticipateException.class, () -> moimService.makeMoim(user, newMoim, wrongMoimDates));
    }

    @Test
    @DisplayName("모임 참여하기")
    void testParticipateMoim() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        User user1 = userService.findUserById(userId);
        Long userId2 = userService.saveUser(user2);
        User user2 = userService.findUserById(userId2);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(user1, newMoim, normalMoimDates);

        // when
        Map<String, Object> participateMoim = moimService.participateMoim(user2, encryptMoimID);

        // then
        Assertions.assertThat(participateMoim.get("moim_name")).isEqualTo("테스트 모임");
        Assertions.assertThat(participateMoim.get("dead_line_hour")).isEqualTo(5);
    }

    @Test
    @DisplayName("모임 주최자가 만든 모임 참여하기")
    void testParticipateMoimByMoimCreator() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        User user = userService.findUserById(userId);
        Moim newMoim = new Moim();
        newMoim.setUser(user);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(user, newMoim, normalMoimDates);

        // when, then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(user, encryptMoimID));
    }

    @Test
    @DisplayName("기존 참여한 모임 다시 참여하기")
    void testParticipateMoimByAlreadyJoinedPeople() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        User user1 = userService.findUserById(userId);
        Long userId2 = userService.saveUser(user2);
        User user2 = userService.findUserById(userId2);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(user1, newMoim, normalMoimDates);

        // when
        moimService.participateMoim(user2, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(user2, encryptMoimID));
    }

    @Test
    @DisplayName("11명 이상 모임 참여하기")
    void testParticipateMoimByMoreThanTenPeople() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        Long userId2 = userService.saveUser(user2);
        Long userId3 = userService.saveUser(user3);
        Long userId4 = userService.saveUser(user4);
        Long userId5 = userService.saveUser(user5);
        Long userId6 = userService.saveUser(user6);
        Long userId7 = userService.saveUser(user7);
        Long userId8 = userService.saveUser(user8);
        Long userId9 = userService.saveUser(user9);
        Long userId10 = userService.saveUser(user10);
        Long userId11 = userService.saveUser(user11);
        Long userId12 = userService.saveUser(user12);

        User user1 = userService.findUserById(userId);
        User user2 = userService.findUserById(userId2);
        User user3 = userService.findUserById(userId3);
        User user4 = userService.findUserById(userId4);
        User user5 = userService.findUserById(userId5);
        User user6 = userService.findUserById(userId6);
        User user7 = userService.findUserById(userId7);
        User user8 = userService.findUserById(userId8);
        User user9 = userService.findUserById(userId9);
        User user10 = userService.findUserById(userId10);
        User user11 = userService.findUserById(userId11);
        User user12 = userService.findUserById(userId12);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        String encryptMoimID = moimService.makeMoim(user1, newMoim, normalMoimDates);

        // when
        moimService.participateMoim(user2, encryptMoimID);
        moimService.participateMoim(user3, encryptMoimID);
        moimService.participateMoim(user4, encryptMoimID);
        moimService.participateMoim(user5, encryptMoimID);
        moimService.participateMoim(user6, encryptMoimID);
        moimService.participateMoim(user7, encryptMoimID);
        moimService.participateMoim(user8, encryptMoimID);
        moimService.participateMoim(user9, encryptMoimID);
        moimService.participateMoim(user10, encryptMoimID);
        moimService.participateMoim(user11, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateMoim(user12, encryptMoimID));
    }

    @Test
    @DisplayName("참여자 일정 정하기")
    void testAdjustSchedule() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        Long userId2 = userService.saveUser(user2);
        User user1 = userService.findUserById(userId);
        User user2 = userService.findUserById(userId2);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        var encryptedId = moimService.makeMoim(user1, newMoim, normalMoimDates);
        // 모임 참여
        moimService.participateMoim(user2, encryptedId);

        // when
        Map<String, Object> adjustSchedule = moimService.adjustSchedule(user2, encryptedId, normalMoimMemberTime);

        // then
        Assertions.assertThat(adjustSchedule.get("moim_name")).isEqualTo("테스트 모임");
        Assertions.assertThat(adjustSchedule.get("moim_maker")).isEqualTo("부엉이");
    }

    @Test
    @DisplayName("참여하지 않은 모임의 유저가 모임 일정 정하기")
    void testAdjustScheduleWithNotParticipate() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        Long userId2 = userService.saveUser(user2);
        User user1 = userService.findUserById(userId);
        User user2 = userService.findUserById(userId2);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        var encryptedId = moimService.makeMoim(user1, newMoim, normalMoimDates);

        // when, then
        assertThrows(AdjustScheduleException.class,
                () -> moimService.adjustSchedule(user2, encryptedId, normalMoimMemberTime));
    }

    @Test
    @DisplayName("주최자가 선택하지 않은 일자로 모임 일정 정하기")
    void testAdjustScheduleWithNotAuthorizedDate() throws Exception {
        // given
        Long userId = userService.saveUser(user);
        Long userId2 = userService.saveUser(user2);
        User user1 = userService.findUserById(userId);
        User user2 = userService.findUserById(userId2);

        Moim newMoim = new Moim();
        newMoim.setUser(user1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        var encryptedId = moimService.makeMoim(user1, newMoim, normalMoimDates);
        // 모임 참여
        moimService.participateMoim(user2, encryptedId);

        // when, then
        assertThrows(AdjustScheduleException.class,
                () -> moimService.adjustSchedule(user2, encryptedId, wrongMoimMemberTime));
    }
}
