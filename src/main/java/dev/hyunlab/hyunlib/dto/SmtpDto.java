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
public class SmtpDto {
  private String host;
  private int port;
  private boolean auth;
  private boolean starttlsEnable;
}
