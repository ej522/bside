package com.example.beside.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
import com.example.beside.dto.MoimDateDto;
import com.example.beside.dto.MoimOveralDateDto;
import com.example.beside.dto.MoimOveralScheduleDto;
import com.example.beside.repository.MoimRepository;
import com.example.beside.util.Encrypt;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoimService {
    private final MoimRepository moimRepository;

    @Autowired
    private Encrypt encrypt;

    @Transactional
    public String makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) throws Exception {
        moimMakeValidate(moim_date_list);

        long moimId = moimRepository.makeMoim(user, moim, moim_date_list);

        return encrypt.encrypt(String.valueOf(moimId));
    }

    private void moimMakeValidate(List<MoimDate> moim_date_list) throws MoimParticipateException {
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

    @Transactional
    public Map<String, Object> participateMoim(User user, String encryptInfo) throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        Moim moim = getMoimInfo(moimId);
        moimValidate(user, moimId, moim);

        // 친구 추가
        moimRepository.makeFriend(user, moim);

        // 모임 멤버 추가
        moimRepository.makeMoimMember(user, moim);

        // 모임 종합 정보 조회
        List<MoimOveralDateDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);
        List<MoimDateDto> moimDateDtos = moimOveralInfo.stream().map(MoimDateDto::new).collect(Collectors.toList());

        // 데이터 결과 가공
        Map<String, Object> result = Map.of("moim_leader", moimOveralInfo.get(0).getUser_name(),
                "moim_name", moimOveralInfo.get(0).getMoim_name(),
                "dead_line_hour", moimOveralInfo.get(0).getDead_line_hour(),
                "dateList", moimDateDtos);

        return result;
    }

    private void moimValidate(User user, Long moimId, Moim moim) throws MoimParticipateException {
        if (user.getId().equals(moim.getUser().getId()))
            throw new MoimParticipateException("모임 주최자는 모임 멤버로 참여할 수 없습니다.");

        if (moim.getCreated_time().plusHours(moim.getDead_line_hour()).isBefore(LocalDateTime.now()))
            throw new MoimParticipateException("데드라인 시간이 지난 모임입니다.");

        if (moimRepository.getMoimMembers(moimId).size() >= 10)
            throw new MoimParticipateException("모임은 최대 10명 까지 가능합니다.");

        if (moimRepository.alreadyJoinedMoim(moimId, user.getId()))
            throw new MoimParticipateException("해당 모임에 이미 참여하고 있습니다.");
    }

    @Transactional
    public Map<String, Object> adjustSchedule(User user, String encryptInfo, List<MoimMemberTime> moimTimeInfos)
            throws Exception {
        Long moimId = Long.parseLong(encrypt.decrypt(encryptInfo));
        adjustScheduleValidate(user, moimId, moimTimeInfos);
        MoimMember moimMember = moimRepository.getMoimMemberByMemberId(moimId, user.getId());

        // 모임 일정 등록
        moimRepository.saveSchedule(moimMember, moimTimeInfos);
        // 모임 일정 현황 조회
        List<MoimOveralScheduleDto> moimScheduleInfo = moimRepository.getMoimScheduleInfo(moimId);
        List<MoimScheduleDto> moimDateDtos = moimScheduleInfo.stream().map(MoimScheduleDto::new)
                .collect(Collectors.toList());

        // 데이터 결과 가공
        Map<String, Object> result = Map.of("moim_id", moimScheduleInfo.get(0).getMoim_id(),
                "moim_maker", moimScheduleInfo.get(0).getUser_name(),
                "moim_name", moimScheduleInfo.get(0).getMoim_name(),
                "dead_line_hour", moimScheduleInfo.get(0).getDead_line_hour(),
                "dateList", moimDateDtos);
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

    public Moim getMoimInfo(Long moimId) {
        return moimRepository.getMoimInfo(moimId);
    }

    public List<MyMoimDto> getMyMoimList(Long user_id) {
        List<MyMoimDto> moimList =  moimRepository.findMyMoimList(user_id);

        for(MyMoimDto moim : moimList) {
            Long cnt = moimRepository.findMemberCount(moim.getMoim_id());

            //주최자도 더해줌
            cnt += 1;

            moim.setMemeber_cnt(cnt);
        }

        return moimList;
    }
}
