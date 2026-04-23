package dev.hyunlab.hyunlib.dto;

import java.nio.file.Path;
import java.util.List;

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
public class EmailV2Dto {
  private SmtpDto smtpDto;

  private Authentication authentication;

  private EmailUser from;

  private List<EmailUser> tos;

  private String title;
  private String body;

  private List<Attachment> attachments;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  /** 첨부파일 정보 */
  public static class Attachment {
    private Path path;
    private String fileName;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  /** 메일 사용자(from/to) 정보 */
  public static class EmailUser {
    private String emailAddress;

    /** 사용자 명 */
    private String userName;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  /** 이메일 서버 로그인용 정보 */
  public static class Authentication {
    /** 이메일 주소 */
    private String emailAddress;
    private String password;
  }

}
