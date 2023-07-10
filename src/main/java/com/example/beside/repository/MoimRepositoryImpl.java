package com.example.beside.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.domain.*;
import com.example.beside.dto.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
public class MoimRepositoryImpl implements MoimRepository {
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
                                .where(qMoim.fixed_date.isNull())
                                .fetch();

        }

        @Override
        @Transactional
        public void fixMoimDate(Moim moim, LocalDateTime date, int time) {
                String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedTime = String.valueOf(time);

                Moim findMoim = em.find(Moim.class, moim.getId());
                findMoim.setFixed_date(formattedDate);
                findMoim.setFixed_time(formattedTime);

                em.persist(findMoim);
        }

        @Override
        //@Transactional
        public void insertAlarm(Alarm alarm) {
                em.persist(alarm);
                em.flush();
        }

        /**
         * moim
         */

        @Override
        public long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception {
                // 모임 생성
                moim.setHistory_view_yn(true);
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

        @Override
        public Moim getMoimInfo(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = new QMoim("moim");
                Moim result = queryFactory.selectFrom(qMoim)
                                .from(qMoim)
                                .where(qMoim.id.eq(moimId))
                                .fetchOne();

                return result;
        }

        @Override
        public List<MoimOveralDateDto> getMoimOveralInfo(Long moinId, LocalDateTime selected_date) {
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
                                .where(qMoim.id.eq(moinId), dateEq(selected_date, qMoimDate))
                                .fetch();

                return result;
        }

        @Override
        public Boolean alreadyJoinedMoim(Long moimId, Long userId) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;

                MoimMember fetchOne = queryFactory.selectFrom(qMoimMember)
                                .from(qMoimMember)
                                .where(qMoimMember.moim.id.eq(moimId)
                                                .and(qMoimMember.user_id.eq(userId))
                                                .and(qMoimMember.is_accept.eq(true)))
                                .fetchOne();

                if (fetchOne == null)
                        return false;

                return true;
        }

        public List<MoimDto> findMyMoimHistoryList(Long user_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;
                QUser qUser = QUser.user;

                LocalDateTime today = LocalDateTime.now();

                String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<MoimDto> result = queryFactory.select(
                                Projections.fields(MoimDto.class,
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qUser.profile_image.as("host_profile_img"),
                                                qMoim.fixed_date.as("fixed_date"),
                                                qMoim.fixed_time.as("fixed_time"),
                                                qMoim.user.id.as("host_id")))
                                .from(qMoim)
                                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                                .leftJoin(qUser).on(qUser.id.eq(qMoim.user.id))
                                .where(((qMoim.user.id.eq(user_id).and(qMoim.history_view_yn.eq(true)))
                                                .or((qMoimMember.user_id.eq(user_id)
                                                                .and(qMoimMember.history_view_yn.eq(true)))))
                                                .and(qMoim.fixed_date.loe(formattedDate))
                                                .and(qMoim.fixed_date.isNotNull())
                                                .and(qMoim.fixed_time.isNotNull())
                                                .and(qMoimMember.is_accept.eq(true)))
                                .orderBy(qMoim.fixed_date.desc(), qMoim.fixed_time.desc())
                                .fetch();

                return result;
        }

        @Override
        public List<VotingMoimDto> findVotingMoimHistory(Long user_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;

                // 모임원
                JPAQuery<VotingMoimDto> query1 = queryFactory
                                .select(Projections.fields(VotingMoimDto.class,
                                                qMoim.user.id.as("user_id"),
                                                qMoim.user.name.as("user_name"),
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qMoim.created_time.as("created_time"),
                                                qMoim.dead_line_hour.as("dead_line_hour")))
                                .from(qMoim).innerJoin(qMoimMember)
                                .on(qMoim.id.eq(qMoimMember.moim.id))
                                .where(qMoim.user.id.ne(user_id)
                                                .and(qMoim.fixed_date.isNull())
                                                .and(qMoimMember.user_id.eq(user_id))
                                                .and(qMoimMember.is_accept.isTrue()));

                // 모임장
                JPAQuery<VotingMoimDto> query2 = queryFactory
                                .select(Projections.fields(VotingMoimDto.class,
                                                qMoim.user.id.as("user_id"),
                                                qMoim.user.name.as("user_name"),
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qMoim.created_time.as("created_time"),
                                                qMoim.dead_line_hour.as("dead_line_hour")))
                                .from(qMoim).innerJoin(qMoimMember)
                                .on(qMoim.id.eq(qMoimMember.moim.id))
                                .where(qMoim.user.id.eq(user_id)
                                                .and(qMoim.fixed_date.isNull()));

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

        @Override
        public List<InvitedMoimListDto> getInvitedMoimList(Long user_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;

                List<InvitedMoimListDto> result = queryFactory.select(Projections.constructor(InvitedMoimListDto.class,
                                qMoim.id.as("moim_id"),
                                qMoim.moim_name.as("moim_name"),
                                qMoim.user.name.as("moim_leader"),
                                qMoim.created_time.as("createdTime"),
                                qMoim.dead_line_hour.as("dead_line_hour")))
                                .from(qMoim).leftJoin(qMoimMember)
                                .on(qMoim.id.eq(qMoimMember.moim.id))
                                .where(qMoim.user.id.ne(user_id)
                                                .and(qMoim.fixed_date.isNull()
                                                                .and(qMoimMember.user_id.eq(user_id))
                                                                .and(qMoimMember.is_accept.isFalse())))
                                .fetch();

                return result;
        }

        /**
         * moim member
         */

        @Override
        public long makeMoimMember(User user, Moim moim) {
                MoimMember moimMember = new MoimMember();
                moimMember.setUser_id(user.getId());
                moimMember.setUser_name(user.getName());
                moimMember.setMoim(moim);
                moimMember.setJoin_time(LocalDateTime.now());
                moimMember.setHistory_view_yn(true);
                moimMember.setIs_accept(true);

                em.persist(moimMember);
                em.flush();
                return moimMember.getId();
        }

        @Override
        public long makeMoimMemberToFriend(String user_id, Moim moim) {
                User user = em.find(User.class, user_id);

                MoimMember moimMember = new MoimMember();
                moimMember.setUser_id(user.getId());
                moimMember.setUser_name(user.getName());
                moimMember.setMoim(moim);
                moimMember.setJoin_time(LocalDateTime.now());
                moimMember.setHistory_view_yn(true);
                moimMember.setIs_accept(false);

                em.persist(moimMember);
                em.flush();
                return moimMember.getId();
        }

        @Override
        public List<MoimMember> getMoimMembers(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;
                List<MoimMember> result = queryFactory
                                .selectFrom(qMoimMember)
                                .where(qMoimMember.moim.id.eq(moimId))
                                .fetch();

                return result;
        }

        @Override
        public MoimMember getMoimMemberByMemberId(Long moimId, Long memberId) {
                queryFactory = new JPAQueryFactory(em);
                QMoimMember qMoimMember = QMoimMember.moimMember;

                MoimMember result = queryFactory.selectFrom(qMoimMember)
                                .where(qMoimMember.moim.id.eq(moimId)
                                                .and(qMoimMember.user_id.eq(memberId)))
                                .fetchOne();

                return result;
        }

        @Override
        public int findMemberCount(Long moim_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;
                int cnt = (int) queryFactory.select(qMoimMember.moim.id)
                                .from(qMoimMember)
                                .where(qMoimMember.moim.id.eq(moim_id))
                                .fetchCount();

                return cnt;
        }

        @Override
        @Transactional
        public void deleteHostHistory(Long user_id, Long moim_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;

                queryFactory.update(qMoim)
                                .set(qMoim.history_view_yn, false)
                                .where(qMoim.user.id.eq(user_id)
                                                .and(qMoim.id.eq(moim_id)))
                                .execute();
        }

        @Override
        @Transactional
        public void deleteGusetHistory(Long user_id, Long moim_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;

                queryFactory.update(qMoimMember)
                                .set(qMoimMember.history_view_yn, false)
                                .where(qMoimMember.user_id.eq(user_id)
                                                .and(qMoimMember.moim.id.eq(moim_id)))
                                .execute();
        }

        @Override
        @Transactional
        public void updateMemberAccept(Long userId, Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;

                queryFactory.update(qMoimMember)
                        .set(qMoimMember.is_accept, true)
                        .where(qMoimMember.user_id.eq(userId)
                                .and(qMoimMember.moim.id.eq(moimId)))
                        .execute();
        }

        @Override
        @Transactional
        public void deleteMoim(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimDate qMoimDate = QMoimDate.moimDate;
                QMoimMember qMoimMember = QMoimMember.moimMember;
                QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

                // 모임 투표한 날짜
                queryFactory.delete(qMoimMemberTime).where(qMoimMemberTime.moim_id.eq(moimId)).execute();
                // 모임 멤버
                queryFactory.delete(qMoimMember).where(qMoimMember.moim.id.eq(moimId)).execute();
                // 주최자가 선택한 모임 날짜
                queryFactory.delete(qMoimDate).where(qMoimDate.moim.id.eq(moimId)).execute();
                // 모임
                queryFactory.delete(qMoim).where(qMoim.id.eq(moimId)).execute();

                em.flush();
                em.clear();
        }

        /**
         * Friend
         */

        @Override
        public long makeFriend(Long friend_id, Long moim_id, User user) {

                queryFactory = new JPAQueryFactory(em);
                QFriend qFriend = QFriend.friend;
                // 친구 확인
                List<Friend> result = queryFactory.selectFrom(qFriend)
                                .where(qFriend.user.id.eq(user.getId())
                                                .and(qFriend.member_id.eq(friend_id)))
                                .fetch();

                // 기존 친구 체크
                if (result.size() > 0)
                        return -1;

                // 친구 등록
                Friend friend = new Friend();
                friend.setFirst_moim_id(moim_id);
                friend.setUser(user);
                friend.setMember_id(friend_id);
                friend.setCreate_time(LocalDateTime.now());

                em.persist(friend);
                em.flush();
                return friend.getId();
        }

        /**
         * Schedule
         */

        @Override
        public long saveSchedule(MoimMember moimMember, List<MoimMemberTime> moimTimeInfos) {

                Moim moim = moimMember.getMoim();
                moim.setNobody_schedule_selected(false);
                em.persist(moim);

                for (var tt : moimTimeInfos) {
                        tt.setMoim_member(moimMember);
                        tt.setMoim_id(moim.getId());

                        em.persist(tt);
                }

                return 0;
        }

        @Override
        public Boolean isAlreadyScheduled(Long moimId, User user) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMember qMoimMember = QMoimMember.moimMember;
                QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

                // 모임 멤버 조회
                MoimMember moimMember = queryFactory.selectFrom(qMoimMember)
                                .where(qMoimMember.moim.id.eq(moimId)
                                                .and(qMoimMember.user_id.eq(user.getId())))
                                .fetchOne();

                // 기존에 모임 일정 등록 여부 확인
                List<MoimMemberTime> result = queryFactory.selectFrom(qMoimMemberTime)
                                .where(qMoimMemberTime.moim_member.eq(moimMember))
                                .fetch();

                if (result.size() > 0) {
                        return true;
                }
                return false;
        }

        @Override
        public List<MoimOveralScheduleDto> getMoimScheduleInfo(Long moimId) {
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
                                                qMoimMember.user_name,
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
                                .leftJoin(qMoimMemberTime).on(qMoimMember.id.eq(qMoimMemberTime.moim_member.id))
                                .leftJoin(qUser).on(qMoimMember.user_id.eq(qUser.id))
                                .where(qMoim.id.eq(moimId))
                                .orderBy(qMoimMemberTime.selected_date.asc())
                                .fetch();

                return result;
        }

        @Override
        public List<MoimDto> findMyMoimList(Long user_id) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;
                QUser qUser = QUser.user;

                List<MoimDto> result = queryFactory.select(
                                Projections.fields(MoimDto.class,
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qUser.profile_image.as("host_profile_img"),
                                                qMoim.fixed_date.as("fixed_date"),
                                                qMoim.fixed_time.as("fixed_time")))
                                .from(qMoim)
                                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                                .where((qMoim.user.id.eq(user_id)
                                                .or(qMoimMember.user_id.eq(user_id)))
                                                .and(qMoim.fixed_date.isNotNull())
                                                .and(qMoim.fixed_time.isNotNull()))
                                .groupBy(qMoim.id, qMoim.moim_name, qUser.profile_image, qMoim.fixed_date,
                                                qMoim.fixed_time)
                                .orderBy(qMoim.fixed_date.desc(), qMoim.fixed_time.desc())
                                .fetch();

                return result;

        }

        @Override
        public int getDateVoteCnt(Long moim_id, LocalDateTime select_date) {
                queryFactory = new JPAQueryFactory(em);

                QMoimMemberTime qMoimMemberTime = QMoimMemberTime.moimMemberTime;

                int cnt = (int) queryFactory.select(qMoimMemberTime.selected_date)
                                .from(qMoimMemberTime)
                                .where(qMoimMemberTime.moim_id.eq(moim_id)
                                                .and(qMoimMemberTime.selected_date.eq(select_date)))
                                .orderBy(qMoimMemberTime.selected_date.asc())
                                .fetchCount();

                return cnt;
        }

        @Override
        public List<MoimDto> findMyMoimFutureList(Long userId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;
                QUser qUser = QUser.user;

                LocalDateTime today = LocalDateTime.now();

                String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<MoimDto> result = queryFactory.select(
                                Projections.fields(MoimDto.class,
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qUser.profile_image.as("host_profile_img"),
                                                qMoim.fixed_date.as("fixed_date"),
                                                qMoim.fixed_time.as("fixed_time"),
                                                qMoim.user.id.as("host_id")))
                                .from(qMoim)
                                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                                .leftJoin(qUser).on(qUser.id.eq(qMoim.user.id))
                                .where(((qMoim.user.id.eq(userId).and(qMoim.history_view_yn.eq(true)))
                                                .or((qMoimMember.user_id.eq(userId)
                                                                .and(qMoimMember.history_view_yn.eq(true)))))
                                                .and(qMoim.fixed_date.goe(formattedDate))
                                                .and(qMoim.fixed_date.isNotNull())
                                                .and(qMoim.fixed_time.isNotNull())
                                                .and(qMoimMember.is_accept.eq(true)))
                                .orderBy(qMoim.fixed_date.desc(), qMoim.fixed_time.desc())
                                .fetch();

                return result;
        }

        @Override
        public MoimDto findMoimByMoimId(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QUser qUser = QUser.user;

                MoimDto result = queryFactory.select(
                                Projections.fields(MoimDto.class,
                                                qMoim.id.as("moim_id"),
                                                qMoim.moim_name.as("moim_name"),
                                                qMoim.fixed_date.as("fixed_date"),
                                                qMoim.fixed_time.as("fixed_time"),
                                                qMoim.user.id.as("host_id"),
                                                qUser.name.as("host_name"),
                                                qUser.profile_image.as("host_profile_img"),
                                                qMoim.dead_line_hour.as("dead_line_hour"),
                                                qMoim.created_time.as("created_time")))
                                .from(qMoim)
                                .leftJoin(qUser).on(qMoim.user.id.eq(qUser.id))
                                .where(qMoim.id.eq(moimId))
                                .fetchOne();

                return result;
        }

        @Override
        public List<MoimMemberDto> findMoimMemberByMoimId(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimMember qMoimMember = QMoimMember.moimMember;
                QUser qUser = QUser.user;

                List<MoimMemberDto> result = queryFactory.select(
                                Projections.constructor(MoimMemberDto.class,
                                                qMoimMember.id,
                                                qMoim.id,
                                                qMoimMember.user_id,
                                                qUser.name,
                                                qUser.profile_image,
                                                qMoimMember.is_accept))
                                .from(qMoim)
                                .leftJoin(qMoimMember).on(qMoim.id.eq(qMoimMember.moim.id))
                                .leftJoin(qUser).on(qUser.id.eq(qMoimMember.user_id))
                                .where(qMoim.id.eq(moimId))
                                .fetch();       //.and(qMoimMember.is_accept.eq(true))

                return result;
        }

        @Override
        public List<MoimDateDto> findMoimDateByMoimId(Long moimId) {
                queryFactory = new JPAQueryFactory(em);

                QMoim qMoim = QMoim.moim;
                QMoimDate qMoimDate = QMoimDate.moimDate;

                List<MoimDateDto> result = queryFactory.select(
                                Projections.constructor(MoimDateDto.class,
                                                qMoimDate.morning,
                                                qMoimDate.afternoon,
                                                qMoimDate.evening,
                                                qMoimDate.selected_date))
                                .from(qMoim)
                                .leftJoin(qMoimDate).on(qMoim.id.eq(qMoimDate.moim.id))
                                .where(qMoim.id.eq(moimId))
                                .fetch();

                return result;
        }

        @Override
        public MoimDateDto findMoimDateByMoimIdAndDate(Long moimId, LocalDateTime selectedDate) {
                queryFactory = new JPAQueryFactory(em);

                QMoimDate qMoimDate = QMoimDate.moimDate;

                MoimDateDto result = queryFactory.select(
                                Projections.constructor(MoimDateDto.class,
                                                qMoimDate.morning,
                                                qMoimDate.afternoon,
                                                qMoimDate.evening,
                                                qMoimDate.selected_date))
                                .from(qMoimDate)
                                .where(qMoimDate.moim.id.eq(moimId).and(qMoimDate.selected_date.eq(selectedDate)))
                                .fetchOne();

                return result;
        }

        private NumberExpression<Integer> getTimeCnt(Object object) {
                return Expressions
                                .numberTemplate(Integer.class, "count(case when {0} then 1 end)", object);
        }

        private BooleanExpression dateEq(LocalDateTime selected_date, QMoimDate qMoimDate) {

                return selected_date == null ? null : qMoimDate.selected_date.eq(selected_date);
        }
}