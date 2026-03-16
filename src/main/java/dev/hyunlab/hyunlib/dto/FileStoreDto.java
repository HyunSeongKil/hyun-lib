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
public class FileStoreDto {

  private String name;

  private String type;

  /** 단위:바이트 */
  private long totalSpace;

  /** 단위:바이트 */
  private long usableSpace;

  /** 단위:바이트 */
  private long usedSpace;

}
