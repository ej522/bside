package com.example.beside.api.appInfo;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beside.common.response.Response;
import com.example.beside.domain.AppInfo;
import com.example.beside.service.AppInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Tag(name = "App Info", description = "앱 정보 API")
@RequiredArgsConstructor
@RequestMapping("/api/appInfo")
@RestController
public class AppInfoController {
    private final AppInfoService appInfoService;

    @Operation(tags = { "APP Info" }, summary = "약관 정보")
    @GetMapping("/v1/get-term")
    public Response<String> getAppTermInfo() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String content = appTermInfo.getDetail();

        return Response.success(200, "약관 정보를 정상 조회했습니다 ", content);
    }

    @Operation(tags = { "APP Info" }, summary = "앱 버전 정보")
    @GetMapping("/v1/get-version")
    public Response<String> getAppVersionInfo() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String version = appTermInfo.getVersion();

        return Response.success(200, "앱 버전을 정상 조회했습니다 ", version);
    }

    @Operation(tags = { "APP Info" }, summary = "앱 버전, 약관 정보 이력")
    @PostMapping("/v1/save-app-info")
    public Response<String> saveTermInformation(@RequestBody @Validated AppInfoRequest appInfoRequest) {

        String version = appInfoRequest.getVersion();
        String content = appInfoRequest.getContent();
        appInfoService.saveAppTermInfo(version, content);

        return Response.success(200, "서비스 약관 정보를 저장했습니다, ", null);
    }

    @Data
    static class AppInfoRequest {
        @NotNull
        @Schema(description = "version", example = "1.0", type = "String")
        private String version;

        @NotNull
        @Schema(description = "content", example = "내용", type = "String")
        private String content;
    }
}
