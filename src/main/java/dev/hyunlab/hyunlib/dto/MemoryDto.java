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
public class MemoryDto {
  /** 단위:바이트 */
  private long totalMemory;

  /** 단위:바이트 */
  private long freeMemory;

  /** 단위:바이트 */
  private long usedMemory;
}
