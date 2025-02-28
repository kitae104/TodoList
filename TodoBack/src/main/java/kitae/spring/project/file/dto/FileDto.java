package kitae.spring.project.file.dto;

import jakarta.persistence.Column;
import kitae.spring.project.file.entity.FileType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {
  private Long id;  // 아이디
  private String parentTable;  // 부모 테이블명
  private Long parentNo; // 부모 테이블의 PK
  private String type;  // 파일 타입("MAIN", "SUB")
  private String fileName;  // 저장 파일명
  private String originName;  // 원본 파일명
  private String filePath;  // 파일 경로
  private Long fileSize; // 파일 크기
  private Long seq; // 순서
  private LocalDateTime regTime;  // 등록일
  private LocalDateTime updateTime; // 수정일
}
