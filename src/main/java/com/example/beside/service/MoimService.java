package com.example.beside.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.beside.dto.MoimScheduleDto;
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
        long moimId = moimRepository.makeMoim(user, moim, moim_date_list);

        moimMakeValidate(moim_date_list);

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
        if (moimRepository.getMoimMembers(moimId).size() >= 10)
            throw new MoimParticipateException("모임은 최대 10명 까지 가능합니다.");

        if (user.getId().equals(moim.getUser().getId()))
            throw new MoimParticipateException("모임 주최자는 모임 멤버로 참여할 수 없습니다.");

        if (moimRepository.alreadyJoinedMoim(moimId, user.getId()))
            throw new MoimParticipateException("해당 모임에 이미 참여하고 있습니다.");

        if (moim.getCreated_time().plusHours(moim.getDead_line_hour()).isBefore(LocalDateTime.now())) {
            throw new MoimParticipateException("데드라인 시간이 지난 모임입니다.");
        }
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

        if (moimMembers.stream().noneMatch(tt -> tt.getUser().getId().equals(user.getId()))) {
            throw new AdjustScheduleException("해당 모임에 참여되지 않았습니다.");
        }

        if (moimRepository.isAlreadyScheduled(moimId, user)) {
            throw new AdjustScheduleException("이미 일정을 등록했습니다. 변경을 원하면 수정을 해주세요");
        }

        // 불가능한 일자를 선택했습니다.
        Boolean isValidate = false;
        for (var tt : moimMemberTimes) {
            isValidate = false;
            for (var aa : moimOveralInfo) {
                if (aa.getSelected_date().equals(tt.getSelected_date()))
                    isValidate = true;
            }
            if (isValidate == false)
                throw new AdjustScheduleException("불가능한 일자를 선택했습니다.");
        }

        // 일자 별 오전, 오후 각 2개씩만 선택 가능합니다.
        for (var tt : moimMemberTimes) {
            int am_count = 0, pm_count = 0;
            if (tt.getAm_nine())
                am_count++;
            if (tt.getAm_ten())
                am_count++;
            if (tt.getAm_eleven())
                am_count++;
            if (tt.getNoon())
                am_count++;
            if (am_count > 2)
                throw new AdjustScheduleException("일자 별 오전, 오후 각 2개씩만 선택 가능합니다.");

            if (tt.getPm_four())
                pm_count++;
            if (tt.getPm_five())
                pm_count++;
            if (tt.getPm_six())
                pm_count++;
            if (tt.getPm_seven())
                pm_count++;
            if (tt.getPm_eigth())
                pm_count++;
            if (tt.getPm_nine())
                pm_count++;
            if (pm_count > 2)
                throw new AdjustScheduleException("일자 별 오전, 오후 각 2개씩만 선택 가능합니다.");
        }
    }

    public Moim getMoimInfo(Long moimId) {
        return moimRepository.getMoimInfo(moimId);
    }
}
