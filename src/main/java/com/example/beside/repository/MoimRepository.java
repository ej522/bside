package com.example.beside.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.beside.domain.Friend;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.QFriend;
import com.example.beside.domain.QMoim;
import com.example.beside.domain.QMoimDate;
import com.example.beside.domain.QMoimMember;
import com.example.beside.domain.QMoimMemberTime;
import com.example.beside.domain.QUser;
import com.example.beside.domain.User;
import com.example.beside.dto.MoimOveralDateDto;
import com.example.beside.dto.MoimOveralScheduleDto;
import com.example.beside.util.Encrypt;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MoimRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @Autowired
    private Encrypt encrypt = new Encrypt();

    public Moim getMoimInfo(Long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = new QMoim("moim");
        Moim result = queryFactory.selectFrom(qMoim)
                .from(qMoim)
                .where(qMoim.id.eq(moimId))
                .fetchOne();

        return result;
    }

    public List<MoimMember> getMoimMembers(Long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;
        List<MoimMember> result = queryFactory
                .selectFrom(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId))
                .fetch();

        return result;
    }

    public MoimMember getMoimMemberByMemberId(Long moimId, Long memberId) {
        queryFactory = new JPAQueryFactory(em);
        QMoimMember qMoimMember = QMoimMember.moimMember;

        MoimMember result = queryFactory.selectFrom(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId)
                        .and(qMoimMember.user.id.eq(memberId)))
                .fetchOne();

        return result;
    }

    public Boolean alreadyJoinedMoim(Long moimId, Long userId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;

        MoimMember fetchOne = queryFactory.selectFrom(qMoimMember)
                .from(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId)
                        .and(qMoimMember.user.id.eq(userId)))
                .fetchOne();

        if (fetchOne == null)
            return false;

        return true;
    }

    public List<MoimOveralDateDto> getMoimOveralInfo(Long moinId) {
        queryFactory = new JPAQueryFactory(em);

        QMoimDate qMoimDate = QMoimDate.moimDate;
        QMoim qMoim = QMoim.moim;
        QUser qUser = QUser.user;

        List<MoimOveralDateDto> result = queryFactory.select(Projections.constructor(MoimOveralDateDto.class,
                qMoim.id, qMoim.user.id, qUser.name, qMoim.moim_name, qMoim.dead_line_hour,
                qMoimDate.morning, qMoimDate.afternoon, qMoimDate.evening, qMoimDate.selected_date))
                .from(qMoim)
                .leftJoin(qMoimDate).on(qMoim.id.eq(qMoimDate.moim.id))
                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                .where(qMoim.id.eq(moinId))
                .fetch();

        return result;
    }

    public long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception {
        // 모임 생성
        moim.setCreated_time(LocalDateTime.now());
        em.persist(moim);

        em.flush();
        // 모임 일정 정보 생성
        for (var moim_date : moim_date_list) {
            em.persist(moim_date);
            moim_date.setMoim(moim);
        }
        em.flush();

        // 암호화 모임 ID 업데이트
        String encryptId = encrypt.encrypt(String.valueOf(moim.getId()));
        moim.setEncrypted_id(encryptId);
        em.flush();

        return moim.getId();
    }

    public long makeMoimMember(User user, Moim moim) {
        MoimMember moimMember = new MoimMember();
        moimMember.setUser(user);
        moimMember.setMember_name(user.getName());
        moimMember.setMoim(moim);
        moimMember.setJoin_time(LocalDateTime.now());

        em.persist(moimMember);
        em.flush();
        return moimMember.getId();
    }

    public long makeFriend(User user, Moim moim) {
        Long user_id = moim.getUser().getId();
        Long friend_id = user.getId();

        queryFactory = new JPAQueryFactory(em);
        QFriend qFriend = QFriend.friend;
        // 친구 확인
        List<Friend> result = queryFactory.selectFrom(qFriend)
                .where(qFriend.user.id.eq(user_id)
                        .and(qFriend.member_id.eq(friend_id)))
                .fetch();

        // 기존 친구 체크
        if (result.size() > 0)
            return -1;

        // 친구 등록
        Friend friend = new Friend();
        friend.setFirst_moim_id(moim.getId());
        friend.setUser(moim.getUser());
        friend.setMember_id(user.getId());
        friend.setCreate_time(LocalDateTime.now());

        em.persist(friend);
        em.flush();
        return friend.getId();
    }

    public long saveSchedule(MoimMember moimMember, List<MoimMemberTime> moimTimeInfos) {

        for (var tt : moimTimeInfos) {
            tt.setMoimMember(moimMember);
            em.persist(tt);
        }

        return 0;
    }

    public Boolean isAlreadyScheduled(Long moimId, User user) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;
        QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

        // 모임 멤버 조회
        MoimMember moimMember = queryFactory.selectFrom(qMoimMember)
                .where(qMoimMember.moim.id.eq(moimId)
                        .and(qMoimMember.user.id.eq(user.getId())))
                .fetchOne();

        // 기존에 모임 일정 등록 여부 확인
        List<MoimMemberTime> result = queryFactory.selectFrom(qMoimMemberTime)
                .where(qMoimMemberTime.moimMember.eq(moimMember))
                .fetch();

        if (result.size() > 0) {
            return true;
        }
        return false;
    }

    public List<MoimOveralScheduleDto> getMoimScheduleInfo(long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = QMoim.moim;
        QMoimMember qMoimMember = QMoimMember.moimMember;
        QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

        List<MoimOveralScheduleDto> result = queryFactory.select(
                Projections.constructor(MoimOveralScheduleDto.class,
                        qMoim.id,
                        qMoim.dead_line_hour,
                        qMoim.created_time,
                        qMoim.user.name,
                        qMoim.moim_name,
                        qMoimMember.member_name,
                        qMoimMemberTime.selected_date,
                        qMoimMemberTime.am_nine,
                        qMoimMemberTime.am_ten,
                        qMoimMemberTime.am_eleven,
                        qMoimMemberTime.noon,
                        qMoimMemberTime.pm_one,
                        qMoimMemberTime.pm_two,
                        qMoimMemberTime.pm_three,
                        qMoimMemberTime.pm_four,
                        qMoimMemberTime.pm_five,
                        qMoimMemberTime.pm_six,
                        qMoimMemberTime.pm_seven,
                        qMoimMemberTime.pm_eigth,
                        qMoimMemberTime.pm_nine))
                .from(qMoim)
                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                .leftJoin(qMoimMemberTime).on(qMoimMember.id.eq(qMoimMemberTime.moimMember.id))
                .where(qMoim.id.eq(moimId))
                .fetch();

        return result;
    }
}