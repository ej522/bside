package com.example.beside.api.moim;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.common.config.Loggable;
import com.example.beside.common.response.*;
import com.example.beside.common.response.ResponseDetail.DeepLinkMoimResponse;
import com.example.beside.common.response.ResponseDetail.InvitedMoimResponse;
import com.example.beside.common.response.ResponseDetail.MoimAdjustScheduleResponse;
import com.example.beside.common.response.ResponseDetail.MoimDetailListResponse;
import com.example.beside.common.response.ResponseDetail.MoimListResponse;
import com.example.beside.common.response.ResponseDetail.MoimParticipateResponse;
import com.example.beside.common.response.ResponseDetail.VoteMoimDateResponse;
import com.example.beside.common.response.ResponseDetail.VoteMoimTimeResponse;
import com.example.beside.common.response.ResponseDetail.VotingMoimResponse;
import com.example.beside.domain.*;
import com.example.beside.dto.*;

import com.example.beside.service.FcmPushService;
import com.example.beside.service.UserService;
import com.example.beside.util.Common;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.beside.service.MoimService;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Loggable
@Tag(name = "Moim", description = "모임 API")
@RequiredArgsConstructor
@RequestMapping("/api/moim")
@RestController
public class MoimController {

    private final MoimService moimService;
    private final FcmPushService fcmPushService;
    private final UserService userService;

    @Operation(tags = { "Moim" }, summary = "시간 투표 결과 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 시간 투표 결과가 조회되었습니다.", content = @Content(schema = @Schema(implementation = VoteMoimTimeResponse.class))),
    })
    @GetMapping(value = "/v1/result-time-vote")
    public Response<VoteMoimTimeDto> getVoteTimeInfo(
            @RequestParam(name = "moim_id") @NotNull Long moim_id,
            @RequestParam(name = "selected_date") @NotNull String selected_date) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        VoteMoimTimeDto voteTimeInfo = moimService.getVoteTimeInfo(moim_id,
                LocalDate.parse(selected_date, formatter).atStartOfDay());

        return VoteMoimTimeResponse.success(200, "모임 시간 투표 결과가 조회되었습니다.", voteTimeInfo);
    }

    @Operation(tags = { "Moim" }, summary = "모임 주최자가 등록한 모임 일정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주최자가 등록한 모임일정이 조회되었습니다.", content = @Content(schema = @Schema(implementation = MoimListResponse.class))),
    })
    @GetMapping(value = "/v1/host-moim-info")
    public Response<MoimParticipateInfoDto> getHostSelectMoimDate(HttpServletRequest token,
            @RequestParam(name = "moim_id") @NotNull Long moim_id) throws Exception {
        User user = (User) token.getAttribute("user");

        MoimParticipateInfoDto moimInfo = moimService.getHostSelectMoimDate(user, moim_id);

        return MoimParticipateResponse.success(200, "주최자가 등록한 모임일정이 조회되었습니다.", moimInfo);
    }

    @Operation(tags = { "Moim" }, summary = "날짜 투표 결과 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 날짜 투표 결과가 조회되었습니다.", content = @Content(schema = @Schema(implementation = VoteMoimDateResponse.class))),
    })
    @GetMapping(value = "/v1/result-date-vote")
    public Response<VoteMoimDateDto> getVoteMoimDateList(HttpServletRequest token,
                                                         @RequestParam(name = "moim_id") @NotNull Long moim_id)
            throws Exception {
        User user = (User) token.getAttribute("user");
        VoteMoimDateDto dateVoteInfo = moimService.getVoteDateInfo(moim_id, user.getId());

        return VoteMoimDateResponse.success(200, "모임 날짜 투표 결과가 조회되었습니다.", dateVoteInfo);
    }

    @Operation(tags = { "Moim" }, summary = "초대받은 모임 참여하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임에 참여 됐습니다.", content = @Content(schema = @Schema(implementation = MoimParticipateResponse.class))),
            @ApiResponse(responseCode = "400", description = "모임은 최대 10명 까지 가능합니다."),
            @ApiResponse(responseCode = "400_1", description = "모임 주최자는 모임 멤버로 참여할 수 없습니다."),
            @ApiResponse(responseCode = "400_2", description = "해당 모임에 이미 참여하고 있습니다.")
    })
    @PostMapping(value = "/v1/participate-invited")
    public Response<MoimParticipateInfoDto> InvitedMoimParticipate(HttpServletRequest token,
            @RequestBody @Validated InvitedLinkParticipate request) throws Exception {
        User user_ = (User) token.getAttribute("user");

        MoimParticipateInfoDto participateMoim = moimService.participateInvitedMoim(user_, request.getMoimId());

        Moim moim = moimService.getMoimInfoWithMoimId(request.getMoimId());

        User hostInfo = userService.chkPushAgree(participateMoim.getMoim_leader_id());

        if(hostInfo!=null) {
            if(hostInfo.getFcm()!=null) {
                String type = AlarmInfo.ACCEPT.name();

                String result = fcmPushService.sendFcmPushNotification(hostInfo.getFcm(), Common.getPushTitle(type),
                        Common.getPushContent(hostInfo.getName(), user_.getName(), moim.getMoim_name(), type),
                        moim.getEncrypted_id(), type);

                if(result.equals(AlarmInfo.SUCCESS.name())) {
                    fcmPushService.saveAlarmData(user_, hostInfo, moim, type, AlarmInfo.SUCCESS.name(), null);
                } else {
                    fcmPushService.saveAlarmData(user_, hostInfo, moim, type, AlarmInfo.ERROR.name(), result);
                }
            }
        }

        return MoimParticipateResponse.success(200, "모임에 참여 됐습니다.", participateMoim);
    }

    @Operation(tags = { "Moim" }, summary = "딥링크 모임 참여하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임에 참여 됐습니다.", content = @Content(schema = @Schema(implementation = MoimParticipateResponse.class))),
            @ApiResponse(responseCode = "400", description = "모임은 최대 10명 까지 가능합니다."),
            @ApiResponse(responseCode = "400_1", description = "모임 주최자는 모임 멤버로 참여할 수 없습니다."),
            @ApiResponse(responseCode = "400_2", description = "해당 모임에 이미 참여하고 있습니다.")
    })
    @PostMapping(value = "/v1/participate-deep-link")
    public Response<MoimParticipateInfoDto> deepLinkParticipate(HttpServletRequest token,
            @RequestBody @Validated deepLinkParticipate request) throws Exception {
        User user_ = (User) token.getAttribute("user");
        String encrptedInfo = request.getEncrptedInfo();

        MoimParticipateInfoDto participateMoim = moimService.participateDeepLink(user_, encrptedInfo);

        User hostInfo = userService.chkPushAgree(participateMoim.getMoim_leader_id());

        if(hostInfo!=null) {
            if(hostInfo.getFcm()!=null) {
                Moim moim = new Moim();
                moim.setId(participateMoim.getMoim_id());
                moim.setMoim_name(participateMoim.getMoim_name());

                String type = AlarmInfo.ACCEPT.name();

                String result = fcmPushService.sendFcmPushNotification(hostInfo.getFcm(), Common.getPushTitle(type),
                        Common.getPushContent(hostInfo.getName(), user_.getName(), moim.getMoim_name(), type),
                        encrptedInfo, type);

                if(result.equals(AlarmInfo.SUCCESS.name())) {
                    fcmPushService.saveAlarmData(user_, hostInfo, moim, type, AlarmInfo.SUCCESS.name(), null);
                } else {
                    fcmPushService.saveAlarmData(user_, hostInfo, moim, type, AlarmInfo.ERROR.name(), result);
                }
            }
        }

        return MoimParticipateResponse.success(200, "모임에 참여 됐습니다.", participateMoim);
    }

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

    @Operation(tags = { "Moim" }, summary = "내 모임 친구 초대하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임을 초대 했습니다", content = @Content(schema = @Schema(implementation = MoimParticipateResponse.class))),
    })
    @PostMapping(value = "/v1/invite-my-moim")
    public Response<MoimParticipateInfoDto> inviteMyMoim(HttpServletRequest token,
            @RequestBody @Validated InviteMyMoimRequest request)
            throws NumberFormatException, Exception {
        User user_ = (User) token.getAttribute("user");

        String encrptedMoimInfo = request.getEncrptedInfo();
        List<String> friend_id_list = request.getFriend_id_list();

        MoimParticipateInfoDto participateMoim = moimService.inviteMyMoim(user_, encrptedMoimInfo, friend_id_list);

        try {
            Moim moim = new Moim();
            moim.setId(participateMoim.getMoim_id());
            moim.setMoim_name(participateMoim.getMoim_name());

            String type = AlarmInfo.INVITE.name();

            for (String friend_id : friend_id_list) {
                User msgUserInfo = userService.chkPushAgree(Long.valueOf(friend_id));

                if (msgUserInfo != null) {
                    if (msgUserInfo.getFcm() != null) {
                        String result = fcmPushService.sendFcmPushNotification(msgUserInfo.getFcm(), Common.getPushTitle(type),
                                Common.getPushContent(msgUserInfo.getName(), user_.getName(), null, type),
                                encrptedMoimInfo, type);

                        System.out.println("result="+result);

                        if(result.equals(AlarmInfo.SUCCESS.name())) {
                            //성공시
                            fcmPushService.saveAlarmData(user_, msgUserInfo, moim, type, AlarmInfo.SUCCESS.name(), null);
                        } else {
                            //실패시
                            fcmPushService.saveAlarmData(user_, msgUserInfo, moim, type, AlarmInfo.ERROR.name(), result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            return MoimParticipateResponse.success(200, "모임에 참여 됐습니다.", participateMoim);
        }
    }

    @Operation(tags = { "Moim" }, summary = "참여 모임 일정 정하기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 스케줄을 등록 했습니다.", content = @Content(schema = @Schema(implementation = MoimAdjustScheduleResponse.class))),
            @ApiResponse(responseCode = "400_1", description = "해당 모임에 참여되지 않았습니다"),
            @ApiResponse(responseCode = "400_2", description = "불가능한 일자를 선택했습니다"),
    })
    @PostMapping(value = "/v1/adjust-schedule")
    public Response<MoimAdjustScheduleDto> adjustSchedule(HttpServletRequest token,
            @RequestBody @Validated AdjustScheduleRequest request) throws Exception {
        User user_ = (User) token.getAttribute("user");

        var moimTimeInfos = new ArrayList<MoimMemberTime>();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Long moimId = request.getMoim_id();
        List<MoimTImeInfoDto> moimTimeList = request.getMoim_time_list();

        for (MoimTImeInfoDto moimTime : moimTimeList) {
            var scheduleInfo = new MoimMemberTime();
            var selectedDate = LocalDate.parse(moimTime.getSelectedDate(), formatter).atStartOfDay();

            scheduleInfo.setSchedule(selectedDate, moimTime);
            moimTimeInfos.add(scheduleInfo);
        }

        MoimAdjustScheduleDto adjustSchedule = moimService.adjustSchedule(user_, moimId, moimTimeInfos);

        return MoimAdjustScheduleResponse.success(200, "모임 스케줄을 등록 했습니다.", adjustSchedule);
    }

    @Operation(tags = { "Moim" }, summary = "투표중인 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "투표중인 모임 목록이 조회 되었습니다.", content = @Content(schema = @Schema(implementation = VotingMoimResponse.class))),
    })
    @GetMapping(value = "/v1/list-voting")
    public Response<List<VotingMoimDto>> getVotingMoimList(HttpServletRequest token) {
        User user = (User) token.getAttribute("user");

        List<VotingMoimDto> votingMoimList = moimService.getVotingMoimList(user.getId());

        return VotingMoimResponse.success(200, "모임 목록이 조회 되었습니다.", votingMoimList);
    }

    @Operation(tags = { "Moim" }, summary = "과거 약속 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과거 모임 목록이 조회 되었습니다.", content = @Content(schema = @Schema(implementation = MoimListResponse.class))),
            @ApiResponse(responseCode = "404", description = "과거 모임 목록이 없습니다.")
    })
    @GetMapping(value = "/v1/list-past")
    public Response<List<MoimDto>> getMoimHistoryList(HttpServletRequest token) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<MoimDto> moimList = moimService.getMoimHistoryList(user.getId());

        return MoimListResponse.success(200, "모임 목록이 조회 되었습니다.", moimList);
    }

    @Operation(tags = { "Moim" }, summary = "예정된 약속 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예정 모임 목록이 조회 되었습니다.", content = @Content(schema = @Schema(implementation = MoimListResponse.class))),
            @ApiResponse(responseCode = "404", description = "예정 모임 목록이 없습니다.")
    })
    @GetMapping(value = "/v1/list-scheduled")
    public Response<List<MoimDto>> getMoimFutureList(HttpServletRequest token) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<MoimDto> moimList = moimService.getMoimFutureList(user.getId());

        return MoimListResponse.success(200, "예정 모임 목록이 조회 되었습니다.", moimList);
    }

    @Operation(tags = { "Moim" }, summary = "초대된 모임 목록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "초대 모임이 조회되었습니다", content = @Content(schema = @Schema(implementation = InvitedMoimResponse.class))),
            @ApiResponse(responseCode = "404", description = "초대 모임 목록이 없습니다.")
    })
    @GetMapping(value = "/v1/list-invited")
    public Response<List<InvitedMoimListDto>> getInvitedMoimList(HttpServletRequest token)
            throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<InvitedMoimListDto> invitedMoimList = moimService.getInvitedMoimList(user.getId());

        return InvitedMoimResponse.success(200, "초대 모임이 조회되었습니다.", invitedMoimList);

    }

    @Operation(tags = { "Moim" }, summary = "특정 모임 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "과거 모임이 삭제되었습니다.", content = @Content(schema = @Schema(implementation = MoimListResponse.class))),
    })
    @DeleteMapping(value = "/v1/delete/moim-history")
    public Response<List<MoimDto>> deleteMoimHistory(HttpServletRequest token,
            @RequestBody @Validated MoimHistoryRequest request) {
        User user = (User) token.getAttribute("user");

        List<MoimDto> result = moimService.deleteMoimHistory(request.moim_id, request.host_id, user.getId());

        return MoimListResponse.success(200, "과거 모임이 삭제되었습니다.", result);

    }

    @Operation(tags = { "Moim" }, summary = "모임 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 정보가 조회되었습니다.", content = @Content(schema = @Schema(implementation = MoimDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 모임이 존재하지 않습니다.")
    })
    @GetMapping(value = "/v1/detail")
    public Response<MoimDetailDto> getMoimDetailInfo(HttpServletRequest token,
            @RequestParam(name = "moim_id") @NotNull Long moim_id) throws NoResultListException {
        MoimDetailDto moimDetailInfo = moimService.getMoimDetailInfo(moim_id);

        return MoimDetailListResponse.success(200, "모임 정보가 조회되었습니다.", moimDetailInfo);

    }

    @Operation(tags = { "Moim" }, summary = "딥링크를 통한 모임 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모임 정보가 조회되었습니다.", content = @Content(schema = @Schema(implementation = DeepLinkMoimResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당 모임이 존재하지 않습니다.")
    })
    @GetMapping(value = "/v1/deeplink-info")
    public Response<MoimDto> getMoimInfoByDeepLink(@RequestParam(name = "encryptInfo") @NotNull String encryptInfo)
            throws Exception {
        MoimDto moimInfo = moimService.getMoimNameAndDeadLine(encryptInfo);

        return DeepLinkMoimResponse.success(200, "모임 정보가 조회되었습니다.", moimInfo);

    }

    @Data
    static class deepLinkParticipate {
        @NotNull
        @Schema(description = "모임방 정보", example = "CbXrx470K6OcAZWiy94SPw==", type = "String")
        private String encrptedInfo;
    }

    @Data
    static class InvitedLinkParticipate {
        @NotNull
        @Schema(description = "모임방 정보", example = "2752", type = "String")
        private Long moimId;
    }

    @Data
    static class CreateMoimRequest {
        @NotNull
        @Schema(description = "모임명", example = "비사이드_6팀", type = "String")
        private String moimName;

        @NotNull
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
        @Schema(description = "모임 아이디", example = "1")
        private Long moim_id;

        @NotNull
        @Schema(description = "모임시간 정보", type = "MoimTimeInfo")
        private List<MoimTImeInfoDto> moim_time_list;

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