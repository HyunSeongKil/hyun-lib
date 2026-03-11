package dev.hyunlab.hyunlib.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AtchmnflDto {
    private String atchmnflId;
    private String atchmnflGroupId;
    private String originFileName;
    private String savedFileName;
    private String contentType;
    private String filePath;
    private Long fileSize;
    private String delYn;
    private String registerId;
    private LocalDateTime registDt;
    private String updaterId;
    private LocalDateTime updateDt;
}
