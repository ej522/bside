package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMoimTimeDetailDto {
    @NotEmpty
    @Schema(description = "선택된 시간", example = "9")
    private Integer time;

    @NotEmpty
    @Schema(description = "선택한 인원", example = "1")
    private Long vote_cnt;

    @Schema(description = "유저정보")
    private List<UserDto> user_info;
}
