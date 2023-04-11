package com.example.beside.repository;

import java.time.LocalDate;
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
import com.example.beside.dto.MoimOveralDto;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MoimRepositoryTest {

    @Autowired
    private MoimRepository moimRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Moim newMoim;
    private List<MoimDate> moimdate1 = new ArrayList<>();

    @BeforeEach
    void settingEntity() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 유저1 세팅
        user = new User();
        user.setName("부엉이");
        user.setEmail("test-user@google.com");
        user.setPassword("Moim@0303");

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
    }

    @Test
    @DisplayName("모임을 등록할 수 있는가?")
    void testMakeMoim() {
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
    void testGetMoimInfo() {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
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
    void testMakeMoimMember() {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // when
        long makeMoimMember = moimRepository.makeMoimMember(findUser, newMoim);

        // then
        Assertions.assertThat(makeMoimMember).isGreaterThan(0);
    }

    @Test
    @DisplayName("이미 참여한 모임인가?")
    void testAlreadyJoinedMoim() {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        moimRepository.makeMoimMember(findUser, newMoim);

        // when
        Boolean alreadyJoinedMoim = moimRepository.alreadyJoinedMoim(moimId, userId);

        // then
        Assertions.assertThat(alreadyJoinedMoim).isTrue();
    }

    @Test
    @DisplayName("해당 모임의 정보를 보여줄 수 있는가?")
    void testGetMoimOveralInfo() {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);
        moimRepository.makeMoimMember(findUser, newMoim);

        // when
        List<MoimOveralDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);

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
    void testMakeFriend() {
        // given
        long userId = userRepository.saveUser(user);
        User findUser = userRepository.findUserById(userId);
        long moimId = moimRepository.makeMoim(findUser, newMoim, moimdate1);

        // when
        long makeFriend = moimRepository.makeFriend(findUser, newMoim);

        // then
        Assertions.assertThat(makeFriend).isGreaterThan(0);
    }

}
