package com.example.beside.api.appInfo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.beside.common.response.Response;
import com.example.beside.domain.AppInfo;
import com.example.beside.service.AppInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "App Info", description = "앱 정보 API")
@RequiredArgsConstructor
@RequestMapping("/api/appInfo")
@RestController
public class AppInfoController {
    private final AppInfoService appInfoService;

    @Operation(tags = { "App Info" }, summary = "앱 버전 정보")
    @GetMapping("/v1/get-version")
    public Response<String> getAppVersionInfo() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String version = appTermInfo.getVersion();

        return Response.success(200, "앱 버전을 정상 조회했습니다 ", version);
    }

    @Operation(tags = { "App Info" }, summary = "약관 정보")
    @GetMapping("/v1/get-terms")
    public Response<String> getAppTermInfo() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String content = appTermInfo.getTerms();

        return Response.success(200, "약관 정보를 정상 조회했습니다 ", content);
    }

    @Operation(tags = { "App Info" }, summary = "개인정보 처리방침")
    @GetMapping("/v1/get-privacy_policy")
    public Response<String> getPrivacyPolicy() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String content = appTermInfo.getPrivacy_policy();

        return Response.success(200, "개인정보 처리방침을 정상 조회했습니다 ", content);
    }

    @Operation(tags = { "App Info" }, summary = "마케팅 정보 수신 동의")
    @GetMapping("/v1/get-marketing_info")
    public Response<String> getMarketingInfo() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String content = appTermInfo.getPrivacy_policy();

        return Response.success(200, "마케팅 정보 수신 동의를 정상 조회했습니다 ", content);
    }

    @Operation(tags = { "App Info" }, summary = "탈퇴 약관")
    @GetMapping("/v1/get-withdraw_terms")
    public Response<String> getWithDrawTerms() {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String content = appTermInfo.getWithdraw_terms();

        return Response.success(200, "탈퇴약관을 정상 조회했습니다 ", content);
    }
}