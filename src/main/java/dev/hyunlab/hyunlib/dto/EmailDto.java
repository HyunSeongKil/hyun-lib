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
public class EmailDto {
  private SmtpDto smtpDto;

  private String fromEmailAddress;
  private String fromEmailPassword;
  private List<String> toEmailAddresses;
  private String title;
  private String body;

  /** filenames.count와 개수 같아야 함 */
  private List<Path> paths;
  /** paths.count와 개수 같아야 함 */
  private List<String> filenames;

}
