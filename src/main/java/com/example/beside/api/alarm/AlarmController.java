package com.example.beside.api.alarm;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.common.response.Response;
import com.example.beside.common.response.ResponseDetail.AlarmInfoResponse;
import com.example.beside.domain.User;
import com.example.beside.dto.AlarmDto;
import com.example.beside.service.FcmPushService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Fcm Alarm Info", description = "알림 정보")
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
@RestController
public class AlarmController {
    private final FcmPushService fcmPushService;

    @Operation(tags = { "Fcm Alarm Info" }, summary = "알람 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알람이 조회되었습니다.", content = @Content(schema = @Schema(implementation = AlarmInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "알람 목록이 없습니다."),
    })
    @GetMapping("/v1/all")
    public Response<List<AlarmDto>> getAlarmAllList(HttpServletRequest token) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<AlarmDto> alarmList = fcmPushService.getAlarmAllList(user);

        return AlarmInfoResponse.success(200, "알람이 조회되었습니다.", alarmList);
    }

    @Operation(tags = { "Fcm Alarm Info" }, summary = "알람 유형별 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알람이 조회되었습니다.", content = @Content(schema = @Schema(implementation = AlarmInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "알람 목록이 없습니다.")
    })
    @GetMapping("/v1/type")
    public Response<List<AlarmDto>> getAlarmTypeList(HttpServletRequest token,
                                                     @RequestParam(name = "type") String type) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<AlarmDto> alarmList = fcmPushService.getAlarmTypeList(user, type);

        return AlarmInfoResponse.success(200, "알람이 조회되었습니다.", alarmList);
    }

    @Operation(tags = { "Fcm Alarm Info" }, summary = "알람 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알람 상태가 수정되었습니다.", content = @Content(schema = @Schema(implementation = AlarmDto.class))),
            @ApiResponse(responseCode = "400", description = "알람 목록이 없습니다."),
    })
    @PostMapping("v1/update-status")
    public Response<List<AlarmDto>> updateAlarmStatus(HttpServletRequest token, @RequestBody AlarmRequest request) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        List<AlarmDto> alarmList = fcmPushService.updateAlarmStatus(request.alarm_id, user, request.status);

        return AlarmInfoResponse.success(200, "알람 상태가 수정되었습니다.", alarmList);
    }

    @Data
    static class AlarmRequest {
        @NotNull
        @Schema(description = "상태", example = "READ / DELETE")
        private String status;

        @NotNull
        @Schema(description = "알람 아이디", example = "1")
        private Long alarm_id;
    }
}
