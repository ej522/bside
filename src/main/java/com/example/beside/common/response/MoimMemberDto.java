package com.example.beside.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimMemberDto {
    @NotEmpty
    @Schema(description = "모임 멤버 id", example = "1")
    private Long memeber_id;

    @NotEmpty
    @Schema(description = "모임id", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "유저id", example = "1")
    private Long user_id;

    @NotEmpty
    @Schema(description = "유저 이름", example = "닉네임")
    private String user_name;
}
