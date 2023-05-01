package com.example.beside.repository;

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
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.dto.MoimOveralDateDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MoimRepositoryTest {

    @Autowired
    private MoimRepository moimRepository;

    @Autowired
    private UserRepository userRepository;

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

        user2 = new User();
        user2.setName("강아지");
        user2.setEmail("test-user@2google.com");
        user2.setPassword("Moim@0303");

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
    @DisplayName("모임을 등록할 수 있는가?")
    void testMakeMoim() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);

        // when
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // then
        Assertions.assertThat(moimId).isGreaterThan(0);
    }

    @Test
    @DisplayName("모임 정보를 가져올 수 있는가?")
    void testGetMoimInfo() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // when
        Moim moimInfo = moimRepository.getMoimInfo(moimId);

        // then
        Assertions.assertThat(moimInfo.getDead_line_hour()).isGreaterThan(0).isLessThan(49);
        Assertions.assertThat(moimInfo.getMoim_name().length()).isGreaterThan(0);
        System.out.println(moimInfo.getCreated_time());
        Assertions.assertThat(moimInfo.getCreated_time()).isNotNull();
    }

    @Test
    @DisplayName("만들어진 모임에 참여할수 있는가?")
    void testMakeMoimMember() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        // 모임 생성
        moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // when
        long makeMoimMember = moimRepository.makeMoimMember(findUser, newMoim);

        // then
        Assertions.assertThat(makeMoimMember).isGreaterThan(0);
    }

    @Test
    @DisplayName("이미 참여한 모임인가?")
    void testAlreadyJoinedMoim() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 생성
        moimRepository.makeMoimMember(findUser, newMoim);

        // when
        Boolean alreadyJoinedMoim = moimRepository.alreadyJoinedMoim(moimId, userId);

        // then
        Assertions.assertThat(alreadyJoinedMoim).isTrue();
    }

    @Test
    @DisplayName("해당 모임의 정보를 보여줄 수 있는가?")
    void testGetMoimOveralInfo() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 생성
        moimRepository.makeMoimMember(findUser, newMoim);

        // when
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);

        // then
        Assertions.assertThat(moimOveralInfo.get(0).getId()).isEqualTo(moimId);
        Assertions.assertThat(moimOveralInfo.get(0).getDead_line_hour()).isEqualTo(5);
        Assertions.assertThat(moimOveralInfo.get(0).getMoim_name()).isEqualTo("테스트 모임");
        Assertions.assertThat(moimOveralInfo.get(0).getMorning()).isFalse();
        Assertions.assertThat(moimOveralInfo.get(0).getAfternoon()).isFalse();
        Assertions.assertThat(moimOveralInfo.get(0).getEvening()).isTrue();
        Assertions.assertThat(moimOveralInfo.get(0).getSelected_date()).isNotNull();
    }

    @Test
    @DisplayName("친구를 등록할 수 있는가?")
    void testMakeFriend() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        newMoim.setUser(findUser);
        // 모임 생성
        moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // when
        long makeFriend = moimRepository.makeFriend(findUser, newMoim);

        // then
        Assertions.assertThat(makeFriend).isGreaterThan(0);
    }

    @Test
    @DisplayName("모임의 상세 일정을 등록할 수 있는가")
    void testSaveSchedule() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long userId2 = userRepository.saveUser(user2);
        User findUser2 = userRepository.findUserById(userId2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());

        // when
        long result = moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // then
        Assertions.assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("모임 일정 확정하기")
    void testFixMoimDate() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long userId2 = userRepository.saveUser(user2);
        User findUser2 = userRepository.findUserById(userId2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());
        // 상세 모임 일정 등록
        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        LocalDateTime dateTime = LocalDateTime.now(); // 예시로 현재 시간을 사용
        String dateString = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // when
        moimRepository.fixMoimDate(newMoim, dateTime, 12);

        // then
        Moim moimInfo = moimRepository.getMoimInfo(newMoim.getId());
        Assertions.assertThat(moimInfo.getFixed_date()).isEqualTo(dateString);
    }

    @Test
    @DisplayName("확정나지 않은 모임 일정 조회")
    void testGetNotFixedScheduleMoims() throws Exception {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long userId2 = userRepository.saveUser(user2);
        User findUser2 = userRepository.findUserById(userId2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());

        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // when
        List<Moim> notFixedScheduleMoims = moimRepository.getNotFixedScheduleMoims();
        Moim myMoim = notFixedScheduleMoims.stream().filter(moim -> moim.getId() == newMoim.getId()).findFirst()
                .orElse(null);

        // then
        Assertions.assertThat(notFixedScheduleMoims).size().isGreaterThan(0);
        Assertions.assertThat(myMoim).isEqualTo(newMoim);
    }

}
