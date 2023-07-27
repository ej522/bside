package com.example.beside.api.alarm;

import com.example.beside.common.Exception.ExceptionDetail.NoResultListException;
import com.example.beside.common.config.Loggable;
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


@Loggable
@Tag(name = "Fcm Alarm Info", description = "알림 정보")
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
@RestController
public class AlarmController {
    private final FcmPushService fcmPushService;

    
    @Operation(tags = { "Fcm Alarm Info" }, summary = "알림 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 조회되었습니다.", content = @Content(schema = @Schema(implementation = AlarmInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "알림 목록이 없습니다.")
    })
    @GetMapping("/v1/list")
    public Response<AlarmDto> getAlarmTypeList(HttpServletRequest token,
                                                     @RequestParam(name = "type", required = false) String type) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        AlarmDto alarmList = fcmPushService.getAlarmTypeList(user, type);

        return AlarmInfoResponse.success(200, "알림 조회되었습니다.", alarmList);
    }

    @Operation(tags = { "Fcm Alarm Info" }, summary = "알림 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 상태가 수정되었습니다.")
    })
    @PostMapping("v1/update-status")
    public Response<?> updateAlarmStatus(HttpServletRequest token, @RequestBody AlarmRequest request) throws NoResultListException {
        User user = (User) token.getAttribute("user");

        fcmPushService.updateAlarmStatus(request.alarm_id, user, request.status);

        return AlarmInfoResponse.success(200, "알림 상태가 수정되었습니다.", null);
    }

    @Data
    static class AlarmRequest {
        @NotNull
        @Schema(description = "상태", example = "READ / DELETE")
        private String status;

        @NotNull
        @Schema(description = "알림 아이디", example = "1")
        private Long alarm_id;
    }
}
