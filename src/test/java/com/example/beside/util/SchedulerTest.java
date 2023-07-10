package com.example.beside.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.service.FcmPushService;
import com.example.beside.service.MoimService;
import com.example.beside.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.repository.MoimRepositoryImpl;
import com.example.beside.repository.UserRepositoryImpl;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class SchedulerTest {

    @Autowired
    private MoimRepositoryImpl moimRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private  UserService userService;

    @Autowired
    private FcmPushService fcmPushService;

    @Autowired
    private MoimService moimService;

    private User user;
    private User user2;
    private Moim newMoim;
    private List<MoimDate> moimdate1 = new ArrayList<>();
    private List<MoimMemberTime> normalMoimMemberTime = new ArrayList<>();

    @BeforeEach
    void settingEntity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 유저 세팅
        user = new User();
        user.setName("부엉이");
        user.setEmail("test-user@google.com");
        user.setPassword("Moim@0303");
        user.setPush_alarm(false);

        user2 = new User();
        user2.setName("강아지");
        user2.setEmail("test-user@2google.com");
        user2.setPassword("Moim@0303");
        user.setPush_alarm(false);

        // 모임 세팅
        newMoim = new Moim();
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임일정 세팅
        MoimDate moimDate = new MoimDate();
        moimDate.setSelected_date(LocalDate.parse("2023-03-10", formatter).atStartOfDay());
        moimDate.setMorning(false);
        moimDate.setAfternoon(false);
        moimDate.setEvening(true);
        moimdate1.add(moimDate);

        // 참여자 모임 일정 세팅
        normalMoimMemberTime = new ArrayList<>();
        MoimMemberTime moimTime = new MoimMemberTime();
        moimTime.setSelected_date(LocalDate.parse("2023-03-10", formatter).atStartOfDay());
        moimTime.setAm_nine(false);
        moimTime.setAm_ten(false);
        moimTime.setAm_eleven(false);
        moimTime.setNoon(false);
        moimTime.setPm_one(false);
        moimTime.setPm_two(false);
        moimTime.setPm_three(false);
        moimTime.setPm_four(false);
        moimTime.setPm_five(false);
        moimTime.setPm_six(false);
        moimTime.setPm_seven(false);
        moimTime.setPm_eigth(true);
        moimTime.setPm_nine(true);
        normalMoimMemberTime.add(moimTime);
    }

    @Test
    void testFixMoimSchedulering() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);
        User findUser2 = userRepository.saveUser(user2);

        newMoim.setUser(findUser);
        newMoim.setDead_line_hour(0);

        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());
        // 상세 모임 일정 등록
        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);
        Scheduler scheduler = new Scheduler(moimRepository, userService, fcmPushService, moimService);

        // when
        scheduler.fixMoimSchedulering();

        // then
        Moim moimInfo = moimRepository.getMoimInfo(newMoim.getId());
        Assertions.assertThat(moimInfo.getFixed_date()).isNotEmpty();
    }

    @Test
    void  testDeleteNotFixedMoim() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);
        User findUser2 = userRepository.saveUser(user2);

        newMoim.setUser(findUser);
        newMoim.setCreated_time(LocalDateTime.now().minusDays(1));
        newMoim.setDead_line_hour(0);
        newMoim.setNobody_schedule_selected(true);

        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);

        Scheduler scheduler = new Scheduler(moimRepository, userService, fcmPushService, moimService);

        // when
        scheduler.deleteNotFixedMoim();

        // then
        Moim moimInfo = moimRepository.getMoimInfo(newMoim.getId());
        Assertions.assertThat(moimInfo).isNull();

    }
}
