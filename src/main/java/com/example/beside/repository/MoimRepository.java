package com.example.beside.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.dto.*;

public interface MoimRepository {
    // CREATE
    long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception;

    long makeMoimMember(User user, Moim moim);

    long makeMoimMemberToFriend(String friend, Moim moim);

    long makeFriend(Long friend_id, Long moim_id, User user);

    long saveSchedule(MoimMember moimMember, List<MoimMemberTime> moimTimeInfos);

    void fixMoimDate(Moim moim, LocalDateTime date, int time);

    // READ
    Moim getMoimInfo(Long moimId);

    List<MoimOveralDateDto> getMoimOveralInfo(Long moimId);

    Boolean alreadyJoinedMoim(Long moimId, Long userId);

    List<MoimDto> findMyMoimHistoryList(Long userId);

    List<VotingMoimDto> findVotingMoimHistory(Long user_id);

    List<MoimMember> getMoimMembers(Long moimId);

    List<InvitedMoimListDto> getInvitedMoimList(Long user_id);

    MoimMember getMoimMemberByMemberId(Long moimId, Long memberId);

    int findMemberCount(Long moim_id);

    Boolean isAlreadyScheduled(Long moimId, User user);

    List<MoimOveralScheduleDto> getMoimScheduleInfo(Long moimId);

    List<MoimDto> findMyMoimList(Long userId);

    int getDateVoteCnt(Long moimId, LocalDateTime selectedDate);

    VoteMoimTimeCntDto getTimeVoteCnt(Long moimId, LocalDateTime selectedDate);

    List<MoimDto> findMyMoimFutureList(Long userId);

    MoimDateDto findMoimDateByMoimIdAndDate(Long moimId, LocalDateTime selectedDate);

    MoimDto findMoimByMoimId(Long moimId);

    List<MoimMemberDto> findMoimMemberByMoimId(Long moimId);

    List<MoimDateDto> findMoimDateByMoimId(Long moimId);

    // UPDATE
    long deleteHostHistory(Long userId, Long moimId);

    long deleteGusetHistory(Long userId, Long moimId);

}
