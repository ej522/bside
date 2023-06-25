package com.example.beside.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.common.response.MoimMemberDto;
import com.example.beside.dto.*;
import com.example.beside.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beside.common.Exception.ExceptionDetail.AdjustScheduleException;
import com.example.beside.common.Exception.ExceptionDetail.InviteMyMoimException;
import com.example.beside.common.Exception.ExceptionDetail.MoimParticipateException;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.repository.MoimRepository;
import com.example.beside.repository.UserRepository;
import com.example.beside.util.Encrypt;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoimService {
    private final MoimRepository moimRepository;
    private final UserRepository userRepository;

    @Autowired
    private Encrypt encrypt;

    // #region [모임 생성]
    @Transactional
    public String makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception {
        makeMoimValidate(moim_date_list);

        long moimId = moimRepository.makeMoim(user, moim, moim_date_list);

        return encrypt.encrypt(String.valueOf(moimId));
    }

    private void makeMoimValidate(List<MoimDate> moim_date_list) throws MoimParticipateException {
        Set<LocalDateTime> selectedDates = new HashSet<>();
        for (MoimDate moim_date : moim_date_list) {
            LocalDateTime selected_date = moim_date.getSelected_date();

            if ((moim_date.getMorning() && moim_date.getAfternoon() && moim_date.getEvening())
                    || (!moim_date.getMorning() && !moim_date.getAfternoon() && !moim_date.getEvening()))
                throw new MoimParticipateException("날짜별 가능한 시간대는 최소 1개 ~ 2개만 선택 가능합니다.");

            if (selectedDates.contains(selected_date))
                throw new MoimParticipateException("동일한 날짜가 포함되어 있습니다.");

            selectedDates.add(selected_date);
        }
    }
    // #endregion

    // #region [딥링크 모임 참여]
    @Transactional
    public MoimParticipateInfoDto participateDeepLink(User user, String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        Moim moim = getMoimInfoWithMoimId(moimId);
        participateMoimValidate(user, moimId, moim);

        // 주최자, 초대자
        moimRepository.makeFriend(user.getId(), moimId, moim.getUser());
        moimRepository.makeFriend(moim.getUser().getId(), moimId, user);

        // 모임 멤버 추가
        moimRepository.makeMoimMember(user, moim);

        // 모임 종합 정보 조회
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);

        // 데이터 결과 가공
        MoimParticipateInfoDto result = new MoimParticipateInfoDto(moimOveralInfo);

        return result;
    }

    // #region [초대받은 모임 참여]
    @Transactional
    public MoimParticipateInfoDto participateInvitedMoim(User user, Long moimId) throws Exception {
        Moim moim = getMoimInfoWithMoimId(moimId);
        participateMoimValidate(user, moimId, moim);

        // 모임 멤버 추가
        moimRepository.makeMoimMember(user, moim);

        // 모임 종합 정보 조회
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);

        // 데이터 결과 가공
        MoimParticipateInfoDto result = new MoimParticipateInfoDto(moimOveralInfo);

        return result;
    }

    private void participateMoimValidate(User user, Long moimId, Moim moim) throws MoimParticipateException {
        if (user.getId().equals(moim.getUser().getId()))
            throw new MoimParticipateException("모임 주최자는 모임 멤버로 참여할 수 없습니다.");

        if (moim.getCreated_time().plusHours(moim.getDead_line_hour()).isBefore(LocalDateTime.now()))
            throw new MoimParticipateException("데드라인 시간이 지난 모임입니다.");

        if (moimRepository.getMoimMembers(moimId).size() >= 10)
            throw new MoimParticipateException("모임은 최대 10명 까지 가능합니다.");

        if (moimRepository.alreadyJoinedMoim(moimId, user.getId()))
            throw new MoimParticipateException("해당 모임에 이미 참여하고 있습니다.");
    }
    // #endregion

    // #region [모임에 친구 초대]
    @Transactional
    public MoimParticipateInfoDto inviteMyMoim(User user, String encryptInfo, List<String> friendList)
            throws NumberFormatException, Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        Moim moim = getMoimInfoWithMoimId(moimId);
        inviteMoimValidate(user, friendList, moimId);

        // 친구 중복 제거
        Set<String> friendSet = new HashSet<>(friendList);
        // 모임 멤버 추가
        for (var friend : friendSet)
            moimRepository.makeMoimMemberToFriend(friend, moim);

        // 모임 종합 정보 조회
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);
        // 데이터 결과 가공
        MoimParticipateInfoDto result = new MoimParticipateInfoDto(moimOveralInfo);

        return result;

    }

    public void inviteMoimValidate(User user, List<String> friendList, Long moimId)
            throws InviteMyMoimException {
        Moim moimInfo = moimRepository.getMoimInfo(moimId);

        if (moimInfo == null)
            throw new InviteMyMoimException("해당 모임이 존재하지 않습니다");

        for (String friend_id : friendList) {
            User friend = userRepository.findUserById(Long.parseLong(friend_id));
            if (friend == null)
                throw new InviteMyMoimException("해당 유저가 존재하지 않습니다. user_id: " + String.valueOf(friend_id));
            if (Long.parseLong(friend_id) == user.getId())
                throw new InviteMyMoimException("모임장만 친구 초대가 가능합니다");
        }
    }
    // #endregion

    // #region [모임 일정 투표]
    @Transactional
    public MoimAdjustScheduleDto adjustSchedule(User user, String encryptInfo, List<MoimMemberTime> moimTimeInfos)
            throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        adjustScheduleValidate(user, moimId, moimTimeInfos);
        MoimMember moimMember = moimRepository.getMoimMemberByMemberId(moimId, user.getId());

        // 모임 일정 등록
        moimRepository.saveSchedule(moimMember, moimTimeInfos);
        // 모임 일정 현황 조회
        List<MoimOveralScheduleDto> moimScheduleInfo = moimRepository.getMoimScheduleInfo(moimId);
        // 데이터 결과 가공
        MoimAdjustScheduleDto result = new MoimAdjustScheduleDto(moimScheduleInfo);

        return result;
    }

    private void adjustScheduleValidate(User user, Long moimId, List<MoimMemberTime> moimMemberTimes)
            throws AdjustScheduleException {
        // 해당 모임에 참여되자 않았습니다.
        List<MoimMember> moimMembers = moimRepository.getMoimMembers(moimId);
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);

        validateMoimTime(moimMemberTimes, moimOveralInfo);

        if (moimMembers.stream().noneMatch(tt -> tt.getUser_id().equals(user.getId()))) {
            throw new AdjustScheduleException("해당 모임에 참여되지 않았습니다.");
        }

        if (moimRepository.isAlreadyScheduled(moimId, user)) {
            throw new AdjustScheduleException("이미 일정을 등록했습니다. 변경을 원하면 수정을 해주세요");
        }
    }

    private void validateMoimTime(List<MoimMemberTime> moimMemberTimes, List<MoimOveralDateDto> moimOveralInfo)
            throws AdjustScheduleException {
        Boolean isValidate = false;

        // 모임 참여자 선택 일자
        for (var tt : moimMemberTimes) {
            isValidate = false;
            // 모임 주최자 선택 일자
            for (var aa : moimOveralInfo) {
                LocalDateTime date = tt.getSelected_date();
                // 일자 일치
                if (aa.getSelected_date().equals(date)) {
                    isValidate = true;

                    if (!aa.getMorning())
                        if (tt.getAm_nine() || tt.getAm_ten() || tt.getAm_eleven())
                            throw new AdjustScheduleException(set_yyyy_mm_dd(date) + " 해당 일에는 오전 9~11시를 선택할 수 없습니다");

                    if (!aa.getAfternoon())
                        if (tt.getNoon() || tt.getPm_one() || tt.getPm_two()
                                || tt.getPm_three() || tt.getPm_four() || tt.getPm_five())
                            throw new AdjustScheduleException(set_yyyy_mm_dd(date) + " 해당 일에는 오후 12~17시를 선택할 수 없습니다");

                    if (!aa.getEvening())
                        if (tt.getPm_six() || tt.getPm_seven() || tt.getPm_eigth() || tt.getPm_nine())
                            throw new AdjustScheduleException(set_yyyy_mm_dd(date) + " 해당 일에는 오후 18~21시를 선택할 수 없습니다");
                }
            }
            if (isValidate == false)
                throw new AdjustScheduleException("불가능한 일자를 선택했습니다.");
        }
    }

    private String set_yyyy_mm_dd(LocalDateTime date) {
        var month = String.format("%02d", date.getMonthValue()); // 월 값에 대해 두 자리로 포맷팅
        var day = String.format("%02d", date.getDayOfMonth()); // 일 값에 대해 두 자리로 포맷팅
        return date.getYear() + "-" + month + "-" + day;
    }
    // #endregion

    // #region [과거 모임 이력 조회]
    public List<MoimDto> getMoimHistoryList(Long user_id) throws NoResultListException {
        List<MoimDto> moimList = moimRepository.findMyMoimHistoryList(user_id);

        if (moimList.isEmpty())
            throw new NoResultListException("과거 모임 목록이 없습니다.");

        for (int i = 0; i < moimList.size(); i++) {
            MoimDto moimDto = moimList.get(i);
            String fixedDate = moimDto.getFixed_date();
            int fixedHour = Integer.parseInt(moimDto.getFixed_time());

            int year = Integer.parseInt(fixedDate.split("-")[0]);
            int month = Integer.parseInt(fixedDate.split("-")[1]);
            int day = Integer.parseInt(fixedDate.split("-")[2]);

            LocalDateTime moimExpectedTime = LocalDateTime.of(year, month, day, fixedHour, 0);
            if (moimExpectedTime.isAfter(LocalDateTime.now())) {
                moimList.remove(i);
                continue;
            }

            int moimMemberCnt = moimRepository.findMemberCount(moimDto.getMoim_id());
            moimMemberCnt += 1;

            moimDto.setMemeber_cnt(moimMemberCnt);
        }

        return moimList;
    }
    // #endregion

    // #region [투표중인 모임 조회]
    public List<VotingMoimDto> getVotingMoimList(Long user_id) {
        List<VotingMoimDto> findVotingMoimHistory = moimRepository.findVotingMoimHistory(user_id);
        LocalDateTime now = LocalDateTime.now();

        List<VotingMoimDto> onGoingMoimDto = findVotingMoimHistory.stream()
                .filter(moimDto -> moimDto.getDead_line_time().isAfter(now))
                .collect(Collectors.toList());

        return onGoingMoimDto;
    }
    // #endregion

    // #region [초대받은 모임 목록 조회]
    public List<InvitedMoimListDto> getInvitedMoimList(Long user_id) throws NoResultListException {
        List<InvitedMoimListDto> invitedMoimList = moimRepository.getInvitedMoimList(user_id);
        if (invitedMoimList.size() == 0)
            throw new NoResultListException("초대 받은 모임이 없습니다");

        return invitedMoimList;
    }
    // #endregion

    public Moim getMoimInfoWithMoimId(Long moimId) {
        return moimRepository.getMoimInfo(moimId);
    }

    public Moim getMoimInfoWithEncrypedMoimId(String encryptInfo) throws NumberFormatException, Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        return moimRepository.getMoimInfo(moimId);
    }

    public MoimParticipateInfoDto getHostSelectMoimDate(User user, Long moimId) throws Exception {
        List<MoimOveralDateDto> moimInfo = moimRepository.getMoimOveralInfo(moimId);

        MoimParticipateInfoDto result = new MoimParticipateInfoDto(moimInfo);

        return result;
    }

    //날짜투표결과
    public List<VoteMoimDateDto> getVoteDateInfo(Long moimId) throws Exception {
        // 모임 날짜 정보
        List<MoimOveralDateDto> dateInfo = moimRepository.getMoimOveralInfo(moimId);

        // 투표 인원 정보
        List<MoimOveralScheduleDto> voteUserInfo = moimRepository.getMoimScheduleInfo(moimId);

        List<VoteMoimDateDto> dateVoteUserList = new ArrayList<>();

        for (int i = 0; i < dateInfo.size(); i++) {
            VoteMoimDateDto voteMoimDateDto = new VoteMoimDateDto();
            voteMoimDateDto.setMoim_id(moimId);

            LocalDateTime selected_date = dateInfo.get(i).getSelected_date();
            voteMoimDateDto.setSelected_date(selected_date);

            voteMoimDateDto.setMorning(dateInfo.get(i).getMorning());
            voteMoimDateDto.setAfternoon(dateInfo.get(i).getAfternoon());
            voteMoimDateDto.setEvening(dateInfo.get(i).getEvening());

            // 투표참여 인원
            int vote_cnt = moimRepository.getDateVoteCnt(moimId, selected_date);
            voteMoimDateDto.setVote_cnt(vote_cnt);

            List<UserDto> userInfoList = new ArrayList<>();

            for (int j = 0; j < voteUserInfo.size(); j++) {
                LocalDateTime vote_date = voteUserInfo.get(j).getSelected_date();
                if (selected_date.equals(vote_date)) {

                    UserDto userDto = new UserDto();
                    userDto.setId(voteUserInfo.get(j).getUser_id());
                    userDto.setName(voteUserInfo.get(j).getMember_name());
                    userDto.setProfile_image(voteUserInfo.get(j).getProfile_image());
                    userInfoList.add(userDto);
                }
            }
            voteMoimDateDto.setUser_info(userInfoList);
            dateVoteUserList.add(voteMoimDateDto);
        }

        return dateVoteUserList;

    }

    //시간투표결과
    public VoteMoimTimeDto getVoteTimeInfo(Long moimId, LocalDateTime selected_date) throws Exception {
        // 투표 인원 정보
        List<MoimOveralScheduleDto> voteUserInfoList = moimRepository.getMoimScheduleInfo(moimId);

        // 인원수
        VoteMoimTimeCntDto voteTimeCnt = moimRepository.getTimeVoteCnt(moimId, selected_date);

        VoteMoimTimeDto moimTimeInfo = new VoteMoimTimeDto();
        moimTimeInfo.setMoim_id(moimId);
        moimTimeInfo.setSelected_date(selected_date);

        List<VoteMoimTimeDetailDto> timeInfoList = new ArrayList<>();

        // 시간, 시간대 투표 인원
        VoteMoimTimeDetailDto am9Info = setTimeInfo(9, voteTimeCnt.getAm_nine_cnt());
        VoteMoimTimeDetailDto am10Info = setTimeInfo(10, voteTimeCnt.getAm_ten_cnt());
        VoteMoimTimeDetailDto am11Info = setTimeInfo(11, voteTimeCnt.getAm_eleven_cnt());
        VoteMoimTimeDetailDto pm12Info = setTimeInfo(12, voteTimeCnt.getNoon_cnt());
        VoteMoimTimeDetailDto pm1Info = setTimeInfo(13, voteTimeCnt.getPm_one_cnt());
        VoteMoimTimeDetailDto pm2Info = setTimeInfo(14, voteTimeCnt.getPm_two_cnt());
        VoteMoimTimeDetailDto pm3Info = setTimeInfo(15, voteTimeCnt.getPm_three_cnt());
        VoteMoimTimeDetailDto pm4Info = setTimeInfo(16, voteTimeCnt.getPm_four_cnt());
        VoteMoimTimeDetailDto pm5Info = setTimeInfo(17, voteTimeCnt.getPm_five_cnt());
        VoteMoimTimeDetailDto pm6Info = setTimeInfo(18, voteTimeCnt.getPm_six_cnt());
        VoteMoimTimeDetailDto pm7Info = setTimeInfo(19, voteTimeCnt.getPm_seven_cnt());
        VoteMoimTimeDetailDto pm8Info = setTimeInfo(20, voteTimeCnt.getPm_eight_cnt());
        VoteMoimTimeDetailDto pm9Info = setTimeInfo(21, voteTimeCnt.getPm_nine_cnt());

        // 해당시간에 투표한 유저 리스트
        List<UserDto> am9userInfoList = new ArrayList<>();
        List<UserDto> am10userInfoList = new ArrayList<>();
        List<UserDto> am11userInfoList = new ArrayList<>();
        List<UserDto> pm12userInfoList = new ArrayList<>();
        List<UserDto> pm1userInfoList = new ArrayList<>();
        List<UserDto> pm2userInfoList = new ArrayList<>();
        List<UserDto> pm3userInfoList = new ArrayList<>();
        List<UserDto> pm4userInfoList = new ArrayList<>();
        List<UserDto> pm5userInfoList = new ArrayList<>();
        List<UserDto> pm6userInfoList = new ArrayList<>();
        List<UserDto> pm7userInfoList = new ArrayList<>();
        List<UserDto> pm8userInfoList = new ArrayList<>();
        List<UserDto> pm9userInfoList = new ArrayList<>();

        for (MoimOveralScheduleDto voteUserInfo : voteUserInfoList) {
            if (selected_date.equals(voteUserInfo.getSelected_date())) {
                // 해당시간에 투표한 유저 정보 입력
                if (voteUserInfo.getAm_nine()) {
                    am9userInfoList = setTimeUserInfo(voteUserInfo, am9userInfoList);
                }

                if (voteUserInfo.getAm_ten()) {
                    am10userInfoList = setTimeUserInfo(voteUserInfo, am10userInfoList);
                }

                if (voteUserInfo.getAm_eleven()) {
                    am11userInfoList = setTimeUserInfo(voteUserInfo, am11userInfoList);
                }

                if (voteUserInfo.getNoon()) {
                    pm12userInfoList = setTimeUserInfo(voteUserInfo, pm12userInfoList);
                }

                if (voteUserInfo.getPm_one()) {
                    pm1userInfoList = setTimeUserInfo(voteUserInfo, pm1userInfoList);
                }

                if (voteUserInfo.getPm_two()) {
                    pm2userInfoList = setTimeUserInfo(voteUserInfo, pm2userInfoList);
                }

                if (voteUserInfo.getPm_three()) {
                    pm3userInfoList = setTimeUserInfo(voteUserInfo, pm3userInfoList);
                }

                if (voteUserInfo.getPm_four()) {
                    pm4userInfoList = setTimeUserInfo(voteUserInfo, pm4userInfoList);
                }

                if (voteUserInfo.getPm_five()) {
                    pm5userInfoList = setTimeUserInfo(voteUserInfo, pm5userInfoList);
                }

                if (voteUserInfo.getPm_six()) {
                    pm6userInfoList = setTimeUserInfo(voteUserInfo, pm6userInfoList);
                }

                if (voteUserInfo.getPm_seven()) {
                    pm7userInfoList = setTimeUserInfo(voteUserInfo, pm7userInfoList);
                }

                if (voteUserInfo.getPm_eight()) {
                    pm8userInfoList = setTimeUserInfo(voteUserInfo, pm8userInfoList);
                }

                if (voteUserInfo.getPm_nine()) {
                    pm9userInfoList = setTimeUserInfo(voteUserInfo, pm9userInfoList);
                }
            }
        }

        //주최자가 설정한 모임 날짜별 시간
        MoimDateDto moimDateInfo = moimRepository.findMoimDateByMoimIdAndDate(moimId, selected_date);
        if(moimDateInfo.isMorning()) {
            timeInfoList = setVoteTimeInfoList(timeInfoList, am9Info, am9userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, am10Info, am10userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, am11Info, am11userInfoList);
        }

        if(moimDateInfo.isAfternoon()) {
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm12Info, pm12userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm1Info, pm1userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm2Info, pm2userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm3Info, pm3userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm4Info, pm4userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm5Info, pm5userInfoList);
        }

        if(moimDateInfo.isEvening()) {
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm6Info, pm6userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm7Info, pm7userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm8Info, pm8userInfoList);
            timeInfoList = setVoteTimeInfoList(timeInfoList, pm9Info, pm9userInfoList);
        }

        moimTimeInfo.setTime_info(timeInfoList);

        return moimTimeInfo;
    }

    //과거모임목록삭제
    @Transactional
    public List<MoimDto> deleteMoimHistory(Long moim_id, Long host_id, Long user_id) {

        if (user_id.equals(host_id)) {
            moimRepository.deleteHostHistory(user_id, moim_id);
        } else {
            moimRepository.deleteGusetHistory(user_id, moim_id);
        }

        List<MoimDto> result = moimRepository.findMyMoimHistoryList(user_id);

        return result;
    }

    // 미래 모임 목록
    public List<MoimDto> getMoimFutureList(Long user_id) throws NoResultListException {
        List<MoimDto> moimList = moimRepository.findMyMoimFutureList(user_id);

        if (moimList.isEmpty()) {
            throw new NoResultListException("예정 모임 목록이 없습니다.");
        }

        for (MoimDto moim : moimList) {
            int cnt = moimRepository.findMemberCount(moim.getMoim_id());

            // 주최자도 더해줌
            cnt += 1;

            moim.setMemeber_cnt(cnt);
        }

        return moimList;
    }

    //모임 상세정보
    public MoimDetailDto getMoimDetailInfo(Long moim_id) throws NoResultListException {
        //모임
        MoimDto moimInfo = moimRepository.findMoimByMoimId(moim_id);
        if(moimInfo.getMoim_id()==null)
            throw new NoResultListException("해당 모임이 존재하지 않습니다.");

        //선택된 모임
        List<MoimDateDto> moimDateList = moimRepository.findMoimDateByMoimId(moim_id);

        List<MoimMemberDto> moimMembers = moimRepository.findMoimMemberByMoimId(moim_id);

        int moim_cnt = moimRepository.findMemberCount(moim_id);
        //주최자 더해줌
        moim_cnt += 1;

        MoimDetailDto moimDetailDto = new MoimDetailDto(moimInfo, moim_cnt, moimDateList, moimMembers);

        return moimDetailDto;
    }

    //모임정보
    public MoimDto getMoimNameAndDeadLine(String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));

        MoimDto moimInfo = moimRepository.findMoimByMoimId(moimId);

        if(moimInfo==null) {
            throw new NoResultListException("해당 모임이 존재하지 않습니다.");
        }

        LocalDateTime deadline = Common.calculateDeadLineTime(moimInfo.getCreated_time(), moimInfo.getDead_line_hour());

        MoimDto summInfo = new MoimDto();
        summInfo.setMoim_name(moimInfo.getMoim_name());
        summInfo.setDead_line_time(deadline);

        return summInfo;
    }

    private List<UserDto> setTimeUserInfo(MoimOveralScheduleDto voteUserInfo, List<UserDto> voteUserInfoList) {
        UserDto userDto = new UserDto();
        userDto.setId(voteUserInfo.getUser_id());
        userDto.setName(voteUserInfo.getMember_name());
        userDto.setProfile_image(voteUserInfo.getProfile_image());

        voteUserInfoList.add(userDto);

        return voteUserInfoList;
    }

    private VoteMoimTimeDetailDto setTimeInfo(int time, int cnt) {
        VoteMoimTimeDetailDto timeInfo = new VoteMoimTimeDetailDto();
        timeInfo.setTime(time);
        timeInfo.setVote_cnt(cnt);

        return timeInfo;
    }

    private List<VoteMoimTimeDetailDto> setVoteTimeInfoList(List<VoteMoimTimeDetailDto> timeInfoList,
            VoteMoimTimeDetailDto timeInfo, List<UserDto> useInfoList) {
        timeInfo.setUser_info(useInfoList);

        timeInfoList.add(timeInfo);

        return timeInfoList;

    }
}
