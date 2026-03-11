package dev.hyunlab.hyunlib.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AtchmnflGroupDto {
    private String atchmnflGroupId;
    private String delYn;
    private String registerId;
    private LocalDateTime registDt;
    private String updaterId;
    private LocalDateTime updateDt;
}
