package com.example.beside.api.moim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.common.response.*;
import com.example.beside.dto.*;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimDate;
import com.example.beside.domain.MoimMemberTime;
import com.example.beside.domain.User;
import com.example.beside.service.MoimService;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(tags = { "Moim" }, summary = "모임 생성하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임을 생성했습니다", content = @Content(schema = @Schema(implementation = Response.class))),
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
        for (MoimDateInfo moimDate : request.moim_date_list) {
            MoimDate temp = new MoimDate();
            LocalDateTime selectedDate = LocalDate.parse(moimDate.selectedDate, formatter).atStartOfDay();

            temp.setSelected_date(selectedDate);
            temp.setMorning(moimDate.morning);
            temp.setAfternoon(moimDate.afternoon);
            temp.setEvening(moimDate.evening);

            moimDates.add(temp);
        }

        String encryptedMoimId = moimService.makeMoim(user_, newMoim, moimDates);
        return Response.success(200, "모임 생성을 완료했습니다", encryptedMoimId);
    }

    @Operation(tags = { "Moim" }, summary = "딥링크 모임 참여하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임에 참여 됐습니다.", content = @Content(schema = @Schema(implementation = MoimParticipateResponse.class))),
            @ApiResponse(responseCode = "400", description = "모임은 최대 10명 까지 가능합니다."),
            @ApiResponse(responseCode = "400_1", description = "모임 주최자는 모임 멤버로 참여할 수 없습니다."),
            @ApiResponse(responseCode = "400_2", description = "해당 모임에 이미 참여하고 있습니다.")
    })
    @PostMapping(value = "/v1/participate")
    public MoimParticipateResponse participateMoim(HttpServletRequest token,
            @RequestBody @Validated MoimParticipateRequest request) throws Exception {
        User user_ = (User) token.getAttribute("user");
        String encrptedInfo = request.getEncrptedInfo();

        MoimParticipateInfoDto participateMoim = moimService.participateMoim(user_, encrptedInfo);

        return MoimParticipateResponse.success(200, "모임에 참여 됐습니다.", participateMoim);
    }

    @Operation(tags = { "Moim" }, summary = "내 모임 친구 초대하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임을 초대 했습니다", content = @Content(schema = @Schema(implementation = MoimParticipateResponse.class))),
    })
    @PostMapping(value = "/v1/invite-my-moim")
    public MoimParticipateResponse inviteMyMoim(HttpServletRequest token,
            @RequestBody @Validated InviteMyMoimRequest request)
            throws NumberFormatException, Exception {
        User user_ = (User) token.getAttribute("user");

        String encrptedMoimInfo = request.getEncrptedInfo();
        List<String> friend_id_list = request.getFriend_id_list();

        MoimParticipateInfoDto participateMoim = moimService.inviteMyMoim(user_, encrptedMoimInfo, friend_id_list);
        return MoimParticipateResponse.success(200, "모임에 참여 됐습니다.", participateMoim);
    }

    @Operation(tags = { "Moim" }, summary = "참여 모임 일정 정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 스케줄을 등록 했습니다.", content = @Content(schema = @Schema(implementation = MoimAdjustScheduleResponse.class))),
            @ApiResponse(responseCode = "400_1", description = "해당 모임에 참여되지 않았습니다"),
            @ApiResponse(responseCode = "400_2", description = "불가능한 일자를 선택했습니다"),
    })
    @PostMapping(value = "/v1/adjust-schedule")
    public MoimAdjustScheduleResponse adjustSchedule(HttpServletRequest token,
            @RequestBody @Validated AdjustScheduleRequest request) throws Exception {
        User user_ = (User) token.getAttribute("user");
        String encrptedInfo = request.getEncrptedInfo();

        // 선택 시간 정보
        List<MoimTimeInfo> moimTimeList = request.getMoim_time_list();
        var moimMemberTimeList = new ArrayList<MoimMemberTime>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (MoimTimeInfo moimTime : moimTimeList) {
            var temp = new MoimMemberTime();
            LocalDateTime selectedDate = LocalDate.parse(moimTime.selectedDate, formatter).atStartOfDay();
            temp.setSelected_date(selectedDate);

            temp.setAm_nine(moimTime.amNine);
            temp.setAm_ten(moimTime.amTen);
            temp.setAm_eleven(moimTime.amEleven);
            temp.setNoon(moimTime.noon);
            temp.setPm_one(moimTime.pmOne);
            temp.setPm_two(moimTime.pmTwo);
            temp.setPm_three(moimTime.pmThree);
            temp.setPm_four(moimTime.pmFour);
            temp.setPm_five(moimTime.pmFive);
            temp.setPm_six(moimTime.pmSix);
            temp.setPm_seven(moimTime.pmSeven);
            temp.setPm_eigth(moimTime.pmEight);
            temp.setPm_nine(moimTime.pmNine);

            moimMemberTimeList.add(temp);
        }

        MoimAdjustScheduleDto adjustSchedule = moimService.adjustSchedule(user_, encrptedInfo, moimMemberTimeList);

        return MoimAdjustScheduleResponse.success(200, "모임 스케줄을 등록 했습니다.", adjustSchedule);
    }

    @Operation(tags = { "Moim" }, summary = "과거 약속 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과거 모임 목록이 조회 되었습니다.", content = @Content(schema = @Schema(implementation = MoimHistoryResponse.class))),
    })
    @GetMapping(value = "/v1/moim-history")
    public MoimHistoryResponse getMoimHistoryList(HttpServletRequest token) {
        User user = (User) token.getAttribute("user");

        List<MyMoimDto> moimList = moimService.getMoimHistoryList(user.getId());

        return MoimHistoryResponse.success(200, "모임 목록이 조회 되었습니다.", moimList);
    }

    @Operation(tags = { "Moim" }, summary = "투표중인 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투퓨중인 모임 목록이 조회 되었습니다.", content = @Content(schema = @Schema(implementation = VotingMoimResponse.class))),
    })
    @GetMapping(value = "/v1/voting-moim-list")
    public VotingMoimResponse getVotingMoimList(HttpServletRequest token) {
        User user = (User) token.getAttribute("user");

        List<VotingMoimDto> votingMoimList = moimService.getVotingMoimList(user.getId());

        return VotingMoimResponse.success(200, "모임 목록이 조회 되었습니다.", votingMoimList);
    }

    @Operation(tags = { "Moim" }, summary = "모임 주최자가 등록한 모임 일정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주최자가 등록한 모임일정이 조회되었습니다.", content = @Content(schema = @Schema(implementation = MoimHistoryResponse.class))),
    })
    @PostMapping(value = "/v1/host-select-date")
    public MoimParticipateResponse getHostSelectMoimDate(HttpServletRequest token,
            @RequestBody @Validated MoimParticipateRequest request) throws Exception {
        User user = (User) token.getAttribute("user");

        MoimParticipateInfoDto moimInfo = moimService.getHostSelectMoimDate(user, request.encrptedInfo);

        return MoimParticipateResponse.success(200, "주최자가 등록한 모임일정이 조회되었습니다.", moimInfo);
    }

    @Operation(tags = { "Moim" }, summary = "날짜 투표 결과")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 날짜 투표 결과가 조회되었습니다.", content = @Content(schema = @Schema(implementation = VoteMoimDateResponse.class))),
    })
    @PostMapping(value = "/v1/date-vote")
    public VoteMoimDateResponse getVoteMoimDateList(@RequestBody @Validated MoimParticipateRequest request)
            throws Exception {
        List<VoteMoimDateDto> dateVoteInfo = moimService.getVoteDateInfo(request.encrptedInfo);

        return VoteMoimDateResponse.success(200, "모임 날짜 투표 결과가 조회되었습니다.", dateVoteInfo);
    }

    @Operation(tags = { "Moim" }, summary = "시간 투표 결과")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 시간 투표 결과가 조회되었습니다.", content = @Content(schema = @Schema(implementation = VoteMoimTimeResponse.class))),
    })
    @PostMapping(value = "/v1/time-vote")
    public VoteMoimTimeResponse getVoteTimeInfo(@RequestBody @Validated TimeVoteRequest request) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        VoteMoimTimeDto voteTimeInfo = moimService.getVoteTimeInfo(request.moim_id,
                LocalDate.parse(request.selected_date, formatter).atStartOfDay());

        return VoteMoimTimeResponse.success(200, "모임 시간 투표 결과가 조회되었습니다.", voteTimeInfo);
    }

    @Operation(tags = { "Moim" }, summary = "특정 모임 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과거 모임이 삭제되었습니다.", content = @Content(schema = @Schema(implementation = MoimHistoryResponse.class))),
    })
    @DeleteMapping(value = "/v1/delete/moim-history")
    public MoimHistoryResponse deleteMoimHistory(HttpServletRequest token,
            @RequestBody @Validated MoimHistoryRequest request) {
        User user = (User) token.getAttribute("user");

        List<MyMoimDto> result = moimService.deleteMoimHistory(request.moim_id, request.host_id, user.getId());

        return MoimHistoryResponse.success(200, "과거 모임이 삭제되었습니다.", result);

    }

    @Data
    static class MoimParticipateRequest {
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
        @Schema(description = "모임일 정보", type = "MoimDateInfo")
        private List<MoimDateInfo> moim_date_list;
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

    @Data
    static class InviteMyMoimRequest {
        @NotNull
        @Schema(description = "모임방 정보", example = "CbXrx470K6OcAZWiy94SPw==", type = "String")
        private String encrptedInfo;

        @NotNull
        @Schema(description = "friend_id 리스트", example = "[8652,8653,14452,1445]", type = "List<String>")
        private List<String> friend_id_list;
    }

    @Data
    static class AdjustScheduleRequest {
        @NotNull
        @Schema(description = "모임방 정보", example = "CbXrx470K6OcAZWiy94SPw==", type = "String")
        private String encrptedInfo;

        @NotNull
        @Schema(description = "모임시간 정보", type = "MoimTimeInfo")
        private List<MoimTimeInfo> moim_time_list;

    }

    @Data
    static class MoimTimeInfo {
        @NonNull
        @Schema(description = "선택일", example = "2023-03-10", type = "String")
        private String selectedDate;

        @Schema(description = "오전 9시", example = "true", type = "Boolean")
        private boolean amNine;

        @Schema(description = "오전 10시", example = "true", type = "Boolean")
        private boolean amTen;

        @Schema(description = "오전 11시", example = "true", type = "Boolean")
        private boolean amEleven;

        @Schema(description = "정오", example = "true", type = "Boolean")
        private boolean noon;

        @Schema(description = "오후 1시", example = "true", type = "Boolean")
        private boolean pmOne;

        @Schema(description = "오후 2시", example = "true", type = "Boolean")
        private boolean pmTwo;

        @Schema(description = "오후 3시", example = "true", type = "Boolean")
        private boolean pmThree;

        @Schema(description = "오후 4시", example = "true", type = "Boolean")
        private boolean pmFour;

        @Schema(description = "오후 5시", example = "true", type = "Boolean")
        private boolean pmFive;

        @Schema(description = "오후 6시", example = "true", type = "Boolean")
        private boolean pmSix;

        @Schema(description = "오후 7시", example = "true", type = "Boolean")
        private boolean pmSeven;

        @Schema(description = "오후 8시", example = "true", type = "Boolean")
        private boolean pmEight;

        @Schema(description = "오후 9시", example = "true", type = "Boolean")
        private boolean pmNine;
    }

    @Data
    static class TimeVoteRequest {
        @NotNull
        @Schema(description = "모임 아이디", example = "1")
        private Long moim_id;

        @NotNull
        @Schema(description = "선택된 날짜", example = "2023-03-10", type = "String")
        private String selected_date;
    }

    @Data
    static class MoimHistoryRequest {
        @NotNull
        @Schema(description = "모임 아이디", example = "1")
        private Long moim_id;

        @NotNull
        @Schema(description = "호스트 아이디", example = "1")
        private Long host_id;
    }

}