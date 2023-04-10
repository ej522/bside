package com.example.beside.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.example.beside.repository.MoimRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MoimService {
    private final MoimRepository moimRepository;

    @Transactional
    public Long makeMoim(User user, Moim moim, List<MoimDate> moim_date_list) {
        long result = moimRepository.makeMoim(user, moim, moim_date_list);

        return result;
    }

}
