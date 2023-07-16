package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimDetailDto {

    private MoimDto moimInfo;

    @Schema(description = "모임 날짜 정보")
    private List<MoimDateDto> moimDateList;

    @Schema(description = "참여 유저 리스트")
    private List<SimpleUserDto> moimMemberList;

    @Schema(description = "무응답 리스트")
    private List<SimpleUserDto> nonResponseList;
}
