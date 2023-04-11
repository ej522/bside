package com.example.beside.api.moim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(tags = { "Moim" }, summary = "모임 참여하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임에 참여 됐습니다."),
            @ApiResponse(responseCode = "400", description = "모임은 최대 10명 까지 가능합니다."),
            @ApiResponse(responseCode = "400_1", description = "모임 주최자는 모임 멤버로 참여할 수 없습니다."),
            @ApiResponse(responseCode = "400_2", description = "해당 모임에 이미 참여하고 있습니다.")
    })
    @PostMapping(value = "/v1/participate")
    public Response<Map<String, Object>> participateMoim(HttpServletRequest token,
            @RequestBody @Validated ParticipateMoimRequest request) throws Exception {
        User user_ = (User) token.getAttribute("user");
        String encrptedInfo = request.getEncrptedInfo();

        Map<String, Object> participateMoim = moimService.participateMoim(user_, encrptedInfo);

        return Response.success(200, "모임에 참여 됐습니다.", participateMoim);
    }

    @Operation(tags = { "Moim" }, summary = "모임 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임을 생성했습니다"),
            @ApiResponse(responseCode = "400", description = "날짜별 가능한 시간대는 최소 1개 ~ 2개만 선택 가능합니다."),
            @ApiResponse(responseCode = "400_1", description = "동일한 날짜가 포함되어 있습니다."),
    })
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
        @Schema(description = "모임방 정보", example = "CbXrx470K6OcAZWiy94SPw==", type = "String")
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