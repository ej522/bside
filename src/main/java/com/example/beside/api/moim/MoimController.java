package com.example.beside.api.moim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beside.common.response.Response;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.User;
import com.example.beside.service.MoimService;

import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Tag(name = "Moim", description = "모임 API")
@RequiredArgsConstructor
@RequestMapping("/api/moim")
@RestController
public class MoimController {

    private final MoimService moimService;

    @PostMapping(value = "/v1/make")
    public Response<String> createMoim(HttpServletRequest token, @RequestBody @Validated CreateMoimRequest request)
            throws Exception {
        User user_ = (User) token.getAttribute("user");

        // 모임 정보
        Moim newMoim = new Moim();
        newMoim.setUser(user_);
        newMoim.setMoim_name(request.getMoimName());
        newMoim.setDead_line_hour(request.deadLineHour);

        // 모임 일정 정보
        List<MoimDate> moimDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (MoimDateInfo moimDate : request.moim_time_list) {
            MoimDate temp = new MoimDate();
            LocalDateTime selectedDate = LocalDate.parse(moimDate.selectedDate, formatter).atStartOfDay();

            if (!checkSelectedTime(moimDate.morning, moimDate.afternoon, moimDate.evening))
                return Response.fail(400, "날짜별 가능한 시간대는 최소 1개 ~ 2개만 선택 가능합니다.");

            temp.setSelected_date(selectedDate);
            temp.setMorning(moimDate.morning);
            temp.setAfternoon(moimDate.afternoon);
            temp.setEvening(moimDate.evening);

            moimDates.add(temp);
        }

        String moimId = moimService.makeMoim(user_, newMoim, moimDates);
        return Response.success(200, "모임 생성을 완료했습니다", moimId);
    }

    private Boolean checkSelectedTime(boolean morning, boolean afternoon, boolean evening) {
        if ((morning && afternoon && evening) || (!morning && !afternoon && !evening))
            return false;
        return true;
    }

    @Data
    static class ParticipateMoimRequest {
        @NotNull
        @Schema(description = "모임방 정보", example = "4mwSYbnvxv01MUQyQGEsOA=2", type = "String")
        private String encrptedInfo;
    }

    @Data
    static class CreateMoimRequest {
        @NotNull
        @Schema(description = "모임명", example = "비사이드_6팀", type = "String")
        private String moimName;

        @NotNull
        @Max(48)
        @Schema(description = "데드라인 시간", example = "5", type = "int")
        private int deadLineHour;

        @NotNull
        @Schema(description = "모임일 정보", type = "MoimTime")
        private List<MoimDateInfo> moim_time_list;
    }

    @Data
    static class MoimDateInfo {
        @NonNull
        @Schema(description = "선택일", example = "2023-03-10", type = "String")
        private String selectedDate;

        @Schema(description = "오전", example = "true", type = "Boolean")
        private boolean morning;

        @Schema(description = "오후", example = "true", type = "Boolean")
        private boolean afternoon;

        @Schema(description = "저녁", example = "true", type = "Boolean")
        private boolean evening;
    }
}