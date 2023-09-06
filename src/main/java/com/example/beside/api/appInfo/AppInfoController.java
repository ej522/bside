package com.example.beside.api.appInfo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.beside.common.config.Loggable;
import com.example.beside.common.response.Response;
import com.example.beside.domain.AppInfo;
import com.example.beside.service.AppInfoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Loggable
@Tag(name = "App Info", description = "앱 정보 API")
@RequiredArgsConstructor
@RequestMapping("/api/appInfo")
@RestController
public class AppInfoController {
    private final AppInfoService appInfoService;

    @Operation(tags = { "App Info" }, summary = "앱 버전 정보")
    @GetMapping("/v1/get-version/{os_type}")
    public Response<String> getAppVersionInfo(@PathVariable(name = "os_type") String os_type) {
        AppInfo appTermInfo = appInfoService.getAppTermInfo();
        String version ="";

        if (os_type.equalsIgnoreCase("android"))
            version = appTermInfo.getAndriod_version();

        else if(os_type.equalsIgnoreCase("ios"))
            version = appTermInfo.getIos_version();
        
        else 
            version ="os_type not exist";

        return Response.success(200, "앱 버전을 정상 조회했습니다 ", version);
    }

    @Operation(tags = { "App Info" }, summary = "앱 버전 정보")
    @PutMapping("/v1/get-version/{os_type}")
    public Response<String> updateAppVersionInfo(
        @PathVariable(name = "os_type") String os_type,
        @RequestParam(name ="version" ,required = true) String version
    ) {

        appInfoService.updateAppVersionInfo(os_type, version);
        return Response.success(200, "앱 버전을 변경했습니다 ", version);
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
