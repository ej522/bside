package com.example.beside.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.common.Exception.ExceptionDetail.*;

import com.example.beside.domain.*;
import com.example.beside.dto.*;
import com.example.beside.repository.MoimRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.example.beside.util.Encrypt;

import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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

    @Autowired
    private MoimRepository moimRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MoimService moimService;

    @Autowired
    private Encrypt encrypt;

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
        user.setName("부엉이2");
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
        User saveUser = userService.saveUser(user);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when
        String encryptMoimID = moimService.makeMoim(saveUser, newMoim, normalMoimDates);

        // then
        Assertions.assertThat(encryptMoimID).isNotNull();
    }

    @Test
    @DisplayName("중복된 날짜를 가진 모임 생성")
    void testMakeMoimWithWrongDateList() throws Exception {
        // given
        User saveUser = userService.saveUser(user);
        Moim newMoim = new Moim();
        newMoim.setUser(saveUser);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // when, then
        assertThrows(MoimParticipateException.class, () -> moimService.makeMoim(saveUser, newMoim, wrongMoimDates));
    }

    @Test
    @DisplayName("딥링크 모임 참여하기")
    void testDeepLinkParticipate() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);

        // when
        MoimParticipateInfoDto participateMoim = moimService.participateDeepLink(saveUser2, encryptMoimID);

        // then
        Assertions.assertThat(participateMoim.getMoim_name()).isEqualTo("테스트 모임");
        Assertions.assertThat(participateMoim.getDead_line_hour()).isEqualTo(5);
    }

    @Test
    @DisplayName("자신이 만든 모임 딥링크로 모임 참여하기")
    void testParticipateMoimByMoimCreator() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(saveUser1, newMoim,
                normalMoimDates);

        // when, then
        assertThrows(MoimParticipateException.class, () -> moimService.participateDeepLink(saveUser1, encryptMoimID));
    }

    @Test
    @DisplayName("기존 참여한 모임 딥링크로 다시 참여하기")
    void testParticipateMoimByAlreadyJoinedPeople() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(saveUser1, newMoim,
                normalMoimDates);

        // when
        moimService.participateDeepLink(saveUser2, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateDeepLink(saveUser2, encryptMoimID));
    }

    @Test
    @DisplayName("11명 이상 모임 참여하기")
    void testParticipateMoimByMoreThanTenPeople() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);
        User saveUser3 = userService.saveUser(user3);
        User saveUser4 = userService.saveUser(user4);
        User saveUser5 = userService.saveUser(user5);
        User saveUser6 = userService.saveUser(user6);
        User saveUser7 = userService.saveUser(user7);
        User saveUser8 = userService.saveUser(user8);
        User saveUser9 = userService.saveUser(user9);
        User saveUser10 = userService.saveUser(user10);
        User saveUser11 = userService.saveUser(user11);
        User saveUser12 = userService.saveUser(user12);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // [모임 생성]
        String encryptMoimID = moimService.makeMoim(saveUser1, newMoim,
                normalMoimDates);

        // when
        moimService.participateDeepLink(saveUser2, encryptMoimID);
        moimService.participateDeepLink(saveUser3, encryptMoimID);
        moimService.participateDeepLink(saveUser4, encryptMoimID);
        moimService.participateDeepLink(saveUser5, encryptMoimID);
        moimService.participateDeepLink(saveUser6, encryptMoimID);
        moimService.participateDeepLink(saveUser7, encryptMoimID);
        moimService.participateDeepLink(saveUser8, encryptMoimID);
        moimService.participateDeepLink(saveUser9, encryptMoimID);
        moimService.participateDeepLink(saveUser10, encryptMoimID);
        moimService.participateDeepLink(saveUser11, encryptMoimID);

        // then
        assertThrows(MoimParticipateException.class, () -> moimService.participateDeepLink(saveUser12, encryptMoimID));
    }

    @Test
    @DisplayName("내가 만든 모임에 친구 초대하기")
    void testInviteMyMoim() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);
        User saveUser3 = userService.saveUser(user3);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        String encryptMoimID = moimService.makeMoim(saveUser1, newMoim,
                normalMoimDates);

        List<String> friendList = new ArrayList();
        friendList.add(String.valueOf(user2.getId()));
        friendList.add(String.valueOf(user3.getId()));

        // when
        MoimParticipateInfoDto participateMoim = moimService.inviteMyMoim(saveUser1,
                encryptMoimID, friendList);

        // then
        Assertions.assertThat(participateMoim.getMoim_name()).isEqualTo("테스트 모임");
    }

    @Test
    @DisplayName("참여한 모임 일정 투표하기")
    void testAdjustSchedule() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        var moimId = Long.parseLong(encrypt.decrypt(encryptedId));
        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // when
        MoimAdjustScheduleDto adjustSchedule = moimService.adjustSchedule(saveUser2,
                moimId, normalMoimMemberTime);

        // then
        Moim moim = moimService.getMoimInfoWithEncrypedMoimId(encryptedId);

        Assertions.assertThat(adjustSchedule.getMoim_name()).isEqualTo("테스트 모임");
        Assertions.assertThat(adjustSchedule.getMoim_leader()).isEqualTo("부엉이2");
        Assertions.assertThat(moim.getNobody_schedule_selected()).isEqualTo(false);
    }

    @Test
    @DisplayName("참여하지 않은 모임의 유저가 모임 일정 정하기")
    void testAdjustScheduleWithNotParticipate() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // 모임 생성
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        var moimId = Long.parseLong(encrypt.decrypt(encryptedId));

        // when, then
        assertThrows(AdjustScheduleException.class,
                () -> moimService.adjustSchedule(saveUser2, moimId,
                        normalMoimMemberTime));
    }

    @Test
    @DisplayName("주최자가 선택하지 않은 일자로 모임 일정 정하기")
    void testAdjustScheduleWithNotAuthorizedDate() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        var moimId = Long.parseLong(encrypt.decrypt(encryptedId));
        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // when, then
        assertThrows(AdjustScheduleException.class,
                () -> moimService.adjustSchedule(saveUser2, moimId,
                        wrongMoimMemberTime));
    }

    @Test
    @DisplayName("초대받은 모임 참여하기")
    void testInvitedMoimParticipate() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임1");
        newMoim.setDead_line_hour(5);

        Moim newMoim2 = new Moim();
        newMoim2.setUser(saveUser1);
        newMoim2.setMoim_name("테스트 모임2");
        newMoim2.setDead_line_hour(5);

        // [user1 과거 모임 생성]
        var encryptMoimID = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);

        // [user2 과거 모임 참여 (친구 등록)]
        moimService.participateDeepLink(saveUser2, encryptMoimID);

        // [user1 신규 모임 생성]
        var encryptMoimId2 = moimService.makeMoim(saveUser1, newMoim2, normalMoimDates);
        var newMoimId = Long.parseLong(encrypt.decrypt(encryptMoimId2));

        // [user1 신규 모임 초대]
        List<String> friendList = new ArrayList<>();
        friendList.add(saveUser2.getId().toString());
        moimService.inviteMyMoim(saveUser1, encryptMoimId2, friendList);

        // when
        MoimParticipateInfoDto participateMoim = moimService.participateInvitedMoim(saveUser2, newMoimId);

        // then
        Assertions.assertThat(participateMoim.getMoim_name()).isEqualTo("테스트 모임2");
        Assertions.assertThat(participateMoim.getDead_line_hour()).isEqualTo(5);
    }

    @Test
    @DisplayName("과거 확정된 모임 목록 조회")
    void testGetMoimHistoryList() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);
        User saveUser3 = userService.saveUser(user3);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        moimService.participateDeepLink(saveUser2, encryptedId);
        moimService.participateDeepLink(saveUser3, encryptedId);

        moimRepository.fixMoimDate(newMoim, LocalDateTime.now().minusDays(1), 12);

        // when
        List<MoimDto> moimList = moimService.getMoimHistoryList(saveUser1.getId());

        // then
        assertTrue(moimList.get(0).getMemeber_cnt() > 1);
    }

    @Test
    @DisplayName("초대 받은 모임 목록 조회")
    void testGetInvitedMoimList() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);

        Moim newMoim2 = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임2");
        newMoim.setDead_line_hour(5);

        // [모임 생성]
        String encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        // [모임 참여 -> 친구 생성]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // [모임 생성]
        String encryptedId2 = moimService.makeMoim(saveUser1, newMoim2, normalMoimDates);
        List<String> friendIdList = new ArrayList<>();
        friendIdList.add(String.valueOf(saveUser2.getId()));
        // [모임 초대]
        moimService.inviteMyMoim(saveUser1, encryptedId2, friendIdList);

        // when
        List<InvitedMoimListDto> invitedMoimList = moimService.getInvitedMoimList(saveUser2.getId());

        // then
        assertTrue(invitedMoimList.get(0).getMoim_name().equals("테스트 모임2"));
    }

    @Test
    @DisplayName("주최자가 선택한 모임 날짜")
    void testGetHostSelectMoimDate() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        Long moimId = Long.parseLong(encrypt.decrypt(encryptedId).toString());

        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // when
        MoimParticipateInfoDto moimInfo = moimService.getHostSelectMoimDate(saveUser2, moimId);

        // then
        assertTrue(moimInfo.getMoim_leader_id().equals(saveUser1.getId()));
        assertTrue(moimInfo.getDateList().size() > 0);
    }

    @Test
    @DisplayName("주최자가 선택한 날짜에 투표한 모임원 정보")
    void getVoteDateInfo() throws Exception {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        Long moimId = Long.parseLong(encrypt.decrypt(encryptedId));

        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // [모임 투표]
        moimService.adjustSchedule(saveUser2, moimId, normalMoimMemberTime);

        // when
        VoteMoimDateDto result = moimService.getVoteDateInfo(moimId, saveUser2.getId());

        // given
        assertTrue(result.getTotal().equals(1));
        assertTrue(result.getTotal().equals(1));
        assertTrue(result.getVoteList().get(0).getSelected_date().isEqual(LocalDate.parse("2023-03-10", formatter).atStartOfDay()));
        assertTrue(result.getVoteList().get(1).getSelected_date().isEqual(LocalDate.parse("2023-03-13", formatter).atStartOfDay()));
        assertTrue(result.getVoteList().get(0).getVote_cnt().equals(0));
        assertTrue(result.getVoteList().get(1).getVote_cnt().equals(1));
        assertTrue(result.getMyVote_yn().equals(true));
    }

    @Test
    @DisplayName("주최자가 선택한 시간에 투표한 모임원 정보")
    void testGetVoteTimeInfo() throws Exception {
        // given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        Long moimId = Long.parseLong(encrypt.decrypt(encryptedId));

        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // [모임 투표]
        moimService.adjustSchedule(saveUser2, moimId, normalMoimMemberTime);

        // when
        VoteMoimTimeDto result = moimService.getVoteTimeInfo(moimId,
                LocalDate.parse("2023-03-13", formatter).atStartOfDay());

        // then
        assertTrue(result.getTotal()==2);
        assertTrue(result.getMorning()!=null);
        assertTrue(result.getAfternoon()!=null);
        assertTrue(result.getEvening()==null);
        assertTrue(result.getMorning().get(0).getVote_cnt()==0);  //am9
        assertTrue(result.getAfternoon().get(1).getVote_cnt()>0); //pm1

    }

    @Test
    @DisplayName("과거 모임 목록 삭제")
    void testDeleteMoimHistory() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        Long moimId = Long.parseLong(encrypt.decrypt(encryptedId));

        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);

        // [모임 투표]
        moimService.adjustSchedule(saveUser2, moimId, normalMoimMemberTime);

        // [모임 확정]
        moimRepository.fixMoimDate(newMoim, LocalDateTime.now().minusDays(1), 12);

        // when
        List<MoimDto> hostResult = moimService.deleteMoimHistory(moimId,
                newMoim.getUser().getId(), saveUser1.getId());

        // then
        List<MoimDto> guestResult = moimService.getMoimHistoryList(saveUser2.getId());
        assertTrue(hostResult.size() == 0);
        assertTrue(guestResult.size() != 0);
    }

    @Test
    @DisplayName("예정된 모임 목록 조회")
    public void testGetMoimFutureList() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);
        User saveUser3 = userService.saveUser(user3);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2024-03-13");
        newMoim.setFixed_time("2");

        // [모임 생성]
        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        // [모임 참여]
        moimService.participateDeepLink(saveUser2, encryptedId);
        moimService.participateDeepLink(saveUser3, encryptedId);

        // [모임 확정]
        moimRepository.fixMoimDate(newMoim, LocalDateTime.now().plusDays(2), 12);

        // when
        List<MoimDto> moimList = moimService.getMoimFutureList(saveUser1.getId());

        // then
        assertTrue(moimList.get(0).getMemeber_cnt() > 1);
    }

    @Test
    @DisplayName("모임id로 모임 상세 조회")
    void testGetMoimDetailInfo() throws Exception {
        // given
        User saveUser1 = userService.saveUser(user);
        User saveUser2 = userService.saveUser(user2);

        Moim newMoim = new Moim();
        newMoim.setUser(saveUser1);
        newMoim.setMoim_name("테스트 모임");
        newMoim.setDead_line_hour(5);
        newMoim.setFixed_date("2023-03-13");
        newMoim.setFixed_time("2");

        var encryptedId = moimService.makeMoim(saveUser1, newMoim, normalMoimDates);
        Long moimId = Long.parseLong(encrypt.decrypt(encryptedId));

        moimService.participateDeepLink(saveUser2, encryptedId);
        moimService.adjustSchedule(saveUser2, moimId, normalMoimMemberTime);

        // when
        MoimDetailDto moimDetailDto = moimService.getMoimDetailInfo(moimId);

        // then
        assertTrue(moimDetailDto.getMoim_name().equals("테스트 모임"));
        assertTrue(moimDetailDto.getMemeber_cnt() == 2);
        assertTrue(moimDetailDto.getMoimDateList().size() > 0);
        assertTrue(moimDetailDto.getMoimMemberList().size() > 0);
    }
}
