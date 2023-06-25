package com.example.beside.dto;

import io.micrometer.common.lang.NonNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimTImeInfoDto {
    @NonNull
    @Schema(description = "선택일", example = "2023-03-10", type = "String")
    private String selectedDate;

    @Schema(description = "오전 9시", example = "true", type = "Boolean")
    private boolean amNine;

    @Schema(description = "오전 10시", example = "true", type = "Boolean")
    private boolean amTen;

    @Schema(description = "오전 11시", example = "true", type = "Boolean")
    private boolean amEleven;

    @Schema(description = "정오", example = "true", type = "Boolean")
    private boolean noon;

    @Schema(description = "오후 1시", example = "true", type = "Boolean")
    private boolean pmOne;

    @Schema(description = "오후 2시", example = "true", type = "Boolean")
    private boolean pmTwo;

    @Schema(description = "오후 3시", example = "true", type = "Boolean")
    private boolean pmThree;

    @Schema(description = "오후 4시", example = "true", type = "Boolean")
    private boolean pmFour;

    @Schema(description = "오후 5시", example = "true", type = "Boolean")
    private boolean pmFive;

    @Schema(description = "오후 6시", example = "true", type = "Boolean")
    private boolean pmSix;

    @Schema(description = "오후 7시", example = "true", type = "Boolean")
    private boolean pmSeven;

    @Schema(description = "오후 8시", example = "true", type = "Boolean")
    private boolean pmEight;

    @Schema(description = "오후 9시", example = "true", type = "Boolean")
    private boolean pmNine;
}
