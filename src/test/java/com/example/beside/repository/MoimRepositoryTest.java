package com.example.beside.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.dto.*;
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

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MoimRepositoryTest {

    @Autowired
    private MoimRepositoryImpl moimRepository;

    @Autowired
    private UserRepositoryImpl userRepository;

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
    @DisplayName("유저는 모임을 등록할 수 있는가?")
    void testMakeMoim() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);

        // when
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);

        // then
        Moim moim = moimRepository.getMoimInfo(moimId);
        Assertions.assertThat(moimId).isGreaterThan(0);
        Assertions.assertThat(moim.getNobody_schedule_selected()).isEqualTo(true);
    }

    @Test
    @DisplayName("특정 moim_id 로 모임 정보를 가져올 수 있는가?")
    void testGetMoimInfo() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);

        // when
        Moim moimInfo = moimRepository.getMoimInfo(moimId);

        // then
        Assertions.assertThat(moimInfo.getDead_line_hour()).isGreaterThan(0).isLessThan(49);
        Assertions.assertThat(moimInfo.getMoim_name().length()).isGreaterThan(0);
        System.out.println(moimInfo.getCreated_time());
        Assertions.assertThat(moimInfo.getCreated_time()).isNotNull();
    }

    @Test
    @DisplayName("유저는 만들어진 모임에 참여할 수 있는가?")
    void testMakeMoimMember() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        // 모임 생성
        moimRepository.makeMoim(savedUser, newMoim, moimdate1);

        // when
        long makeMoimMember = moimRepository.makeMoimMember(savedUser, newMoim);

        // then
        Assertions.assertThat(makeMoimMember).isGreaterThan(0);
    }

    @Test
    @DisplayName("이미 참여한 모임인지 확인할 수 있는가?")
    void testAlreadyJoinedMoim() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 생성
        moimRepository.makeMoimMember(savedUser, newMoim);

        // when
        Boolean alreadyJoinedMoim = moimRepository.alreadyJoinedMoim(moimId, savedUser.getId());

        // then
        Assertions.assertThat(alreadyJoinedMoim).isTrue();
    }

    @Test
    @DisplayName("특정 모임의 정보를 보여줄 수 있는가?")
    void testGetMoimOveralInfo() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 생성
        moimRepository.makeMoimMember(savedUser, newMoim);

        // when
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId, null);
        System.out.println("moimOveralInfo="+moimOveralInfo);

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
        User savedUser = userRepository.saveUser(user);
        newMoim.setUser(savedUser);
        // 모임 생성
        moimRepository.makeMoim(savedUser, newMoim, moimdate1);

        // when
        long makeFriend = moimRepository.makeFriend(savedUser.getId(), newMoim.getId(), newMoim.getUser());

        // then
        Assertions.assertThat(makeFriend).isGreaterThan(0);
    }

    @Test
    @DisplayName("참여한 모임의 상세 일정을 투표할 수 있는가")
    void testSaveSchedule() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        User savedUser2 = userRepository.saveUser(user2);

        newMoim.setUser(savedUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(savedUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, savedUser2.getId());

        // when
        long result = moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // then
        Assertions.assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("모임 일정을 확정할 수 있는가")
    void testFixMoimDate() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        User savedUser2 = userRepository.saveUser(user2);

        newMoim.setUser(savedUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(savedUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, savedUser2.getId());
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
    @DisplayName("확정나지 않은 모임 일정을 조회할 수 있는가?")
    void testGetNotFixedMoims() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        User savedUser2 = userRepository.saveUser(user2);

        newMoim.setUser(savedUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(savedUser2, newMoim);
        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, savedUser2.getId());

        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // when
        List<Moim> notFixedScheduleMoims = moimRepository.getNotFixedMoims();
        Moim myMoim = notFixedScheduleMoims.stream().filter(moim -> moim.getId() == newMoim.getId()).findFirst()
                .orElse(null);

        // then
        Assertions.assertThat(notFixedScheduleMoims).size().isGreaterThan(0);
        Assertions.assertThat(myMoim).isEqualTo(newMoim);
    }

    @Test
    @DisplayName("투표중인 모임 목록을 조회할 수 있는가?")
    void testFindVotingMoimHistory() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        User savedUser2 = userRepository.saveUser(user2);

        newMoim.setUser(savedUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        // 모임 참여
        moimRepository.makeMoimMember(savedUser2, newMoim);
        // 모임 일정 등록
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, savedUser2.getId());
        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // when
        List<VotingMoimDto> findVotingMoimHistory = moimRepository.findVotingMoimHistory(savedUser2.getId());

        // then
        Assertions.assertThat(findVotingMoimHistory).size().isGreaterThan(0);
        Assertions.assertThat(findVotingMoimHistory.get(0).getMoim_name()).isEqualTo("테스트 모임");
        Assertions.assertThat(findVotingMoimHistory.get(0).getUser_name()).isEqualTo("부엉이");
    }

    @Test
    @DisplayName("유저의 친구 목록을 조회할 수 있는가?")
    void testFindFriendByUserId() throws Exception {
        // given
        User savedUser = userRepository.saveUser(user);
        User savedUser2 = userRepository.saveUser(user2);

        newMoim.setUser(savedUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(savedUser, newMoim, moimdate1);
        newMoim.setId(moimId);

        // 모임 참여
        moimRepository.makeMoimMember(savedUser2, newMoim);

        // 친구등록
        moimRepository.makeFriend(savedUser2.getId(), newMoim.getId(), newMoim.getUser());
        userRepository.deleteUser(savedUser);

        // when
        List<FriendDto.FriendInfo> friendDtoList = userRepository.findFriendByUserId(savedUser.getId());

        // then
        Assertions.assertThat(friendDtoList.size()).isEqualTo(0);

    }

    @Test
    @DisplayName("모임날짜에 투표한 인원이 몇 명인지 알 수 있는가?")
    void testGetDateVoteCnt() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);
        User findUser2 = userRepository.saveUser(user2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        newMoim.setId(moimId);

        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);

        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());

        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        // when
        int cnt = moimRepository.getDateVoteCnt(moimId, normalMoimMemberTime.get(0).getSelected_date());

        // then
        Assertions.assertThat(cnt).isGreaterThan(0);
    }

    @Test
    @DisplayName("시간을 투표한 인원이 몇 명인지 알 수 있는가?")
    void testGetTimeVoteCnt() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);
        User findUser2 = userRepository.saveUser(user2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        newMoim.setId(moimId);

        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);

        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());

        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // when
        VoteMoimTimeCntDto test = moimRepository.getTimeVoteCnt(moimId,
                LocalDate.parse("2023-03-10", formatter).atStartOfDay());

        // then
        Assertions.assertThat(test.getAm_nine_cnt()).isEqualTo(0);
        Assertions.assertThat(test.getAm_ten_cnt()).isEqualTo(0);
        Assertions.assertThat(test.getPm_eight_cnt()).isEqualTo(1);
        Assertions.assertThat(test.getPm_nine_cnt()).isEqualTo(1);
    }

    @Test
    @DisplayName("과거 모임 목록을 조회할 수 있는가?")
    void testGetFindMyMoimHistoryList() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);
        User findUser2 = userRepository.saveUser(user2);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        newMoim.setId(moimId);

        // 모임 참여
        moimRepository.makeMoimMember(findUser2, newMoim);

        // 모임 멤버 조회
        var moimMember = moimRepository.getMoimMemberByMemberId(moimId, findUser2.getId());

        moimRepository.saveSchedule(moimMember, normalMoimMemberTime);

        LocalDateTime dateTime = LocalDateTime.now().minusDays(1);
        moimRepository.fixMoimDate(newMoim, dateTime, 12);

        // when
        List<MoimDto> result = moimRepository.findMyMoimHistoryList(findUser2.getId());

        // then
        Assertions.assertThat(result.get(0).getFixed_date())
                .isLessThanOrEqualTo(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Assertions.assertThat(result.get(0).getFixed_time()).isLessThanOrEqualTo("12");
    }

    @Test
    @DisplayName("주최자가 설정한 날짜 목록을 모임아이디와 선택된 날짜로 조회할 수 있는가?")
    void testFindMoimDateByMoimIdAndDate() throws Exception {
        // given
        User findUser = userRepository.saveUser(user);

        newMoim.setUser(findUser);
        // 모임 생성
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        newMoim.setId(moimId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //when
        MoimDateDto result = moimRepository.findMoimDateByMoimIdAndDate(moimId, LocalDate.parse("2023-03-10", formatter).atStartOfDay());

        //then
        Assertions.assertThat(result.isMorning()).isFalse();
        Assertions.assertThat(result.isAfternoon()).isFalse();
        Assertions.assertThat(result.isEvening()).isTrue();
    }

}
