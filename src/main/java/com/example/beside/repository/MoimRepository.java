package com.example.beside.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.dto.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.example.beside.dto.MyMoimDto;
import com.example.beside.dto.VotingMoimDto;

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
import com.example.beside.util.Encrypt;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MoimRepository {
    private final EntityManager em;
    private JPAQueryFactory queryFactory;

    @Autowired
    private Encrypt encrypt = new Encrypt();

    /**
     * batch
     */

    public List<Moim> getNotFixedMoims() {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = new QMoim("moim");
        return queryFactory.selectFrom(qMoim)
                .where(qMoim.nobody_schedule_selected.eq(false))
                .fetch();

    }

    @Transactional
    public void fixMoimDate(Moim moim, LocalDateTime date, int time) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String formattedTime = String.valueOf(time);

        Moim findMoim = em.find(Moim.class, moim.getId());
        findMoim.setFixed_date(formattedDate);
        findMoim.setFixed_time(formattedTime);

        em.persist(findMoim);
    }

    /**
     * moim
     */

    public long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception {
        // 모임 생성
        moim.setCreated_time(LocalDateTime.now());
        moim.setNobody_schedule_selected(true);
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

    public Moim getMoimInfo(Long moimId) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = new QMoim("moim");
        Moim result = queryFactory.selectFrom(qMoim)
                .from(qMoim)
                .where(qMoim.id.eq(moimId))
                .fetchOne();

        return result;
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

    public List<MyMoimDto> findMyMoimHistoryList(Long user_id) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = QMoim.moim;
        QMoimMember qMoimMember = QMoimMember.moimMember;
        QUser qUser = QUser.user;

        List<MyMoimDto> result = queryFactory.select(
                Projections.fields(MyMoimDto.class,
                        qMoim.id.as("moim_id"),
                        qMoim.moim_name.as("moim_name"),
                        qUser.profile_image.as("host_profile_img"),
                        qMoim.fixed_date.as("fixed_date"),
                        qMoim.fixed_time.as("fixed_time")))
                .from(qMoim)
                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                .where(qMoim.user.id.eq(user_id)
                        .or(qMoimMember.user.id.eq(user_id))
                        .and(qMoim.fixed_date.isNotNull())
                        .and(qMoim.fixed_time.isNotNull()))
                .groupBy(qMoim.id, qMoim.moim_name, qUser.profile_image, qMoim.fixed_date, qMoim.fixed_time)
                .orderBy(qMoim.fixed_date.desc(), qMoim.fixed_time.desc())
                .fetch();

        return result;
    }

    public List<VotingMoimDto> findVotingMoimHistory(Long user_id) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = QMoim.moim;
        QUser qUser = QUser.user;
        QMoimMember qMoimMember = QMoimMember.moimMember;

        // 내가 참여한 모임
        JPAQuery<VotingMoimDto> query1 = queryFactory
                .select(Projections.fields(VotingMoimDto.class, qUser.id.as("user_id"),
                        qUser.name.as("user_name"),
                        qMoim.id.as("moim_id"),
                        qMoim.moim_name.as("moim_name"),
                        qMoim.created_time.as("created_time"),
                        qMoim.dead_line_hour.as("dead_line_hour")))
                .from(qMoim)
                .innerJoin(qUser).on(qUser.id.eq(qMoim.user.id))
                .where(qMoim.id.in(
                        JPAExpressions.select(qMoimMember.moim.id)
                                .from(qMoimMember)
                                .where(qMoimMember.user.id.eq(user_id)))
                        .and(qMoim.fixed_date.isNull()));

        // 내가 모임장인 모임
        JPAQuery<VotingMoimDto> query2 = queryFactory
                .select(Projections.fields(VotingMoimDto.class, qUser.id.as("user_id"),
                        qUser.name.as("user_name"),
                        qMoim.id.as("moim_id"),
                        qMoim.moim_name.as("moim_name"),
                        qMoim.created_time.as("created_time"),
                        qMoim.dead_line_hour.as("dead_line_hour")))
                .from(qMoim)
                .innerJoin(qUser).on(qUser.id.eq(qMoim.user.id))
                .where(qMoim.user.id.eq(user_id).and(qMoim.fixed_date.isNull()));

        // UINON ALL
        List<VotingMoimDto> result = query1.fetch();
        result.addAll(query2.fetch());

        // 데드라인 멤버변수 새로 할당
        List<VotingMoimDto> newResult = new ArrayList<>();
        for (VotingMoimDto data : result) {
            VotingMoimDto votingMoimDto = new VotingMoimDto(data);
            newResult.add(votingMoimDto);
        }
        return newResult;
    }

    /**
     * moim member
     */

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

    public Long findMemberCount(Long moim_id) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMember qMoimMember = QMoimMember.moimMember;
        Long cnt = queryFactory.select(qMoimMember.moim.id)
                .from(qMoimMember)
                .where(qMoimMember.moim.id.eq(moim_id))
                .fetchCount();

        return cnt;
    }

    /**
     * Friend
     */

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

    /**
     * Schedule
     */

    public long saveSchedule(MoimMember moimMember, List<MoimMemberTime> moimTimeInfos) {

        Moim moim = moimMember.getMoim();
        moim.setNobody_schedule_selected(false);
        em.persist(moim);

        for (var tt : moimTimeInfos) {
            tt.setMoimMember(moimMember);
            tt.setMoim(moim);

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
        QUser qUser = QUser.user;

        List<MoimOveralScheduleDto> result = queryFactory.select(
                Projections.constructor(MoimOveralScheduleDto.class,
                        qMoim.id,
                        qMoim.dead_line_hour,
                        qMoim.created_time,
                        qUser.id,
                        qMoim.user.name,
                        qMoim.moim_name,
                        qMoimMember.member_name,
                        qUser.profile_image,
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
                .leftJoin(qUser).on(qMoimMember.user.id.eq(qUser.id))
                .where(qMoim.id.eq(moimId))
                .orderBy(qMoimMemberTime.selected_date.asc())
                .fetch();

        return result;
    }

    public List<MyMoimDto> findMyMoimList(Long user_id) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = QMoim.moim;
        QMoimMember qMoimMember = QMoimMember.moimMember;
        QUser qUser = QUser.user;

        List<MyMoimDto> result = queryFactory.select(
                Projections.fields(MyMoimDto.class,
                        qMoim.id.as("moim_id"),
                        qMoim.moim_name.as("moim_name"),
                        qUser.profile_image.as("host_profile_img"),
                        qMoim.fixed_date.as("fixed_date"),
                        qMoim.fixed_time.as("fixed_time")))
                .from(qMoim)
                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                    .where(qMoim.user.id.eq(user_id)
                            .or(qMoimMember.user.id.eq(user_id))
                            .and(qMoim.fixed_date.isNotNull())
                        .and(qMoim.fixed_time.isNotNull()))
                .groupBy(qMoim.id, qMoim.moim_name, qUser.profile_image, qMoim.fixed_date, qMoim.fixed_time)
                .orderBy(qMoim.fixed_date.desc(), qMoim.fixed_time.desc())
                .fetch();

        return result;

    }

    public Long getDateVoteCnt(Long moim_id, LocalDateTime select_date) {
        queryFactory = new JPAQueryFactory(em);

        QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

        Long cnt = queryFactory.select(qMoimMemberTime.selected_date)
                .from(qMoimMemberTime)
                .where(qMoimMemberTime.moim.id.eq(moim_id).and(qMoimMemberTime.selected_date.eq(select_date)))
                .orderBy(qMoimMemberTime.selected_date.asc())
                .fetchCount();

        return cnt;
    }

    public VoteMoimTimeCntDto getTimeVoteCnt(Long moim_id, LocalDateTime select_date) {
        queryFactory = new JPAQueryFactory(em);

        QMoim qMoim = QMoim.moim;
        QMoimDate qMoimDate = QMoimDate.moimDate;
        QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

        VoteMoimTimeCntDto result = queryFactory.select(
                        Projections.constructor(VoteMoimTimeCntDto.class,
                                qMoimMemberTime.selected_date,
                                getTimeCnt(qMoimMemberTime.am_nine.eq(true)),
                                getTimeCnt(qMoimMemberTime.am_ten.eq(true)),
                                getTimeCnt(qMoimMemberTime.am_eleven.eq(true)),
                                getTimeCnt(qMoimMemberTime.noon.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_one.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_two.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_three.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_four.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_five.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_six.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_seven.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_eigth.eq(true)),
                                getTimeCnt(qMoimMemberTime.pm_nine.eq(true))
                        ))
                .from(qMoim)
                .leftJoin(qMoimDate).on(qMoim.id.eq(qMoimDate.moim.id))
                .leftJoin(qMoimMemberTime).on(qMoim.id.eq(qMoimMemberTime.moim.id).and(qMoimDate.selected_date.eq(qMoimMemberTime.selected_date)))
                .where(qMoim.id.eq(moim_id).and(qMoimMemberTime.selected_date.eq(select_date)))
                .groupBy(qMoimMemberTime.selected_date)
                .fetchOne();

        if(result==null) {
            result = new VoteMoimTimeCntDto();
            result.setSelected_date(select_date);
            result.setAm_nine_cnt(0L);
            result.setAm_ten_cnt(0L);
            result.setAm_eleven_cnt(0L);
            result.setNoon_cnt(0L);
            result.setPm_one_cnt(0L);
            result.setPm_two_cnt(0L);
            result.setPm_three_cnt(0L);
            result.setPm_four_cnt(0L);
            result.setPm_five_cnt(0L);
            result.setPm_six_cnt(0L);
            result.setPm_seven_cnt(0L);
            result.setPm_eight_cnt(0L);
            result.setPm_nine_cnt(0L);
        }

        return result;
    }

    private NumberExpression<Long> getTimeCnt(Object object) {
        return Expressions
                .numberTemplate(Long.class, "count(case when {0} then 1 end)", object);
    }
}