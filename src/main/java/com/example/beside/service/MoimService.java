package com.example.beside.service;

import java.time.LocalDateTime;
import java.util.*;

import com.example.beside.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beside.common.Exception.AdjustScheduleException;
import com.example.beside.common.Exception.MoimParticipateException;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMember;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.repository.MoimRepositoryImpl;
import com.example.beside.util.Encrypt;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoimService {
    private final MoimRepositoryImpl moimRepository;

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

    // #region [모임 참여]
    @Transactional
    public MoimParticipateInfoDto participateMoim(User user, String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        Moim moim = getMoimInfoWithMoimId(moimId);
        participateMoimValidate(user, moimId, moim);

        // 친구 추가
        moimRepository.makeFriend(user, moim);

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

        if (moimMembers.stream().noneMatch(tt -> tt.getUser().getId().equals(user.getId()))) {
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
    public List<MyMoimDto> getMoimHistoryList(Long user_id) {
        List<MyMoimDto> moimList = moimRepository.findMyMoimHistoryList(user_id);

        for (int i = 0; i < moimList.size(); i++) {
            MyMoimDto moim = moimList.get(i);

            String date = moim.getFixed_date();
            String[] dateList = date.split("-");
            String time = moim.getFixed_time();

            // 쿼리에서 날짜와 시간을 같이 비교할 수 없어서 서비스 단에서 비교후 오늘 이후의 날짜는 제거
            LocalDateTime dateTime = LocalDateTime.of(Integer.parseInt(dateList[0]), Integer.parseInt(dateList[1]),
                    Integer.parseInt(dateList[2]), Integer.parseInt(time), 0);

            if (dateTime.isAfter(LocalDateTime.now())) {
                moimList.remove(i);
                continue;
            }

            int cnt = moimRepository.findMemberCount(moim.getMoim_id());

            // 주최자도 더해줌
            cnt += 1;

            moim.setMemeber_cnt(cnt);
        }

        return moimList;
    }
    // #endregion

    // #region [투표중인 모임 조회]
    public List<VotingMoimDto> getVotingMoimList(Long user_id) {
        List<VotingMoimDto> findVotingMoimHistory = moimRepository.findVotingMoimHistory(user_id);
        return findVotingMoimHistory;
    }
    // #endregion

    public Moim getMoimInfoWithMoimId(Long moimId) {
        return moimRepository.getMoimInfo(moimId);
    }

    public Moim getMoimInfoWithEncrypedMoimId(String encryptInfo) throws NumberFormatException, Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        return moimRepository.getMoimInfo(moimId);
    }

    public MoimParticipateInfoDto getHostSelectMoimDate(User user, String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));

        List<MoimOveralDateDto> moimInfo = moimRepository.getMoimOveralInfo(moimId);

        MoimParticipateInfoDto result = new MoimParticipateInfoDto(moimInfo);

        return result;
    }

    public List<VoteMoimDateDto> getVoteDateInfo(String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));

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

        timeInfoList = setVoteTimeInfoList(timeInfoList, am9Info, am9userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, am10Info, am10userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, am11Info, am11userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm12Info, pm12userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm1Info, pm1userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm2Info, pm2userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm3Info, pm3userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm4Info, pm4userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm5Info, pm5userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm6Info, pm6userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm7Info, pm7userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm8Info, pm8userInfoList);
        timeInfoList = setVoteTimeInfoList(timeInfoList, pm9Info, pm9userInfoList);

        moimTimeInfo.setTime_info(timeInfoList);

        return moimTimeInfo;
    }

    @Transactional
    public List<MyMoimDto> deleteMoimHistory(Long moim_id, Long host_id, Long user_id) {

        if (user_id.equals(host_id)) {
            moimRepository.deleteHostHistory(user_id, moim_id);
        } else {
            moimRepository.deleteGusetHistory(user_id, moim_id);
        }

        List<MyMoimDto> result = moimRepository.findMyMoimHistoryList(user_id);

        return result;
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
