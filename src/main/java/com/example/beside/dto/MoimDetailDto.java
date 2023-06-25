package com.example.beside.dto;

import com.example.beside.common.response.MoimMemberDto;
import com.example.beside.domain.Moim;
import com.example.beside.domain.MoimMember;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimDetailDto {
    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "모임명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "모임 확정 날짜", example = "2023-12-20")
    private String fixed_date;

    @NotEmpty
    @Schema(description = "모임 확정 시간", example = "14")
    private String fixed_time;

    @NotEmpty
    @Schema(description = "모임 주최자 ID", example = "1")
    private Long host_id;

    @Schema(description = "모임 인원", example = "6")
    private int memeber_cnt;

    @Schema(description = "모임 정보")
    private List<MoimDateDto> moimDateList;

    @Schema(description = "참여 유저 리스트")
    private List<MoimMemberDto> moimMemberList;

    public MoimDetailDto(MoimDto moim, int member_cnt, List<MoimDateDto> moimDateList, List<MoimMemberDto> moimMemberList) {
        this.moim_id = moim.getMoim_id();
        this.moim_name = moim.getMoim_name();
        this.fixed_date = moim.getFixed_date();
        this.fixed_time = moim.getFixed_time();
        this.host_id = moim.getHost_id();
        this.memeber_cnt = member_cnt;
        this.moimDateList = moimDateList;
        this.moimMemberList = moimMemberList;
    }
}
