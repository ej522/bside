package com.example.beside.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beside.common.Exception.MoimParticipateException;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.example.beside.dto.MoimDateDto;
import com.example.beside.dto.MoimOveralDto;
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
            if (selectedDates.contains(selected_date))
                throw new MoimParticipateException("동일한 날짜가 포함되어 있습니다.");
            else
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
        List<MoimOveralDto> moimOveralInfo = moimRepository.getMoimOveralInfo(moimId);
        List<MoimDateDto> moimDateDtos = moimOveralInfo.stream().map(MoimDateDto::new).collect(Collectors.toList());

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

    public Moim getMoimInfo(Long moimId) {
        return moimRepository.getMoimInfo(moimId);
    }
}
