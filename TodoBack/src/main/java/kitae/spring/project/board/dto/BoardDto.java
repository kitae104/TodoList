package kitae.spring.project.board.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import kitae.spring.project.file.dto.FileDto;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
  private Long id;
  private String title;
  private String writer;
  private String content;
  private LocalDateTime regTime;
  private LocalDateTime updateTime;

  private MultipartFile mainFile;
  private List<MultipartFile> fileList;

  private FileDto file;
}
