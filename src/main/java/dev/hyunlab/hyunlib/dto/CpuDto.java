package dev.hyunlab.hyunlib.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CpuDto {

  private String name;

  private String arch;

  private String version;

  /** 코어(논리) 수 */
  private int availableProcessors;

  /** 시스템 평균 부하(사용량) */
  private double systemLoadAverage;

}
