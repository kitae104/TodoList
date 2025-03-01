package kitae.spring.project.file.entity;

import jakarta.persistence.*;
import kitae.spring.project.common.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "file_id", nullable = false)
  private Long id;  // 아이디

  @Column(length = 45, nullable = false)
  private String parentTable;  // 부모 테이블명

  @Column(nullable = false)
  private Long parentNo; // 부모 테이블의 PK

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "VARCHAR(10) DEFAULT 'SUB'")
  private FileType type;  // 파일 타입("MAIN", "SUB")

  @Column(nullable = false)
  private String fileName;  // 저장 파일명

  @Column(nullable = false)
  private String originName;  // 원본 파일명

  @Column(nullable = false)
  private String filePath;  // 파일 경로

  @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  private Long fileSize; // 파일 크기

  @Column(nullable = false)
  private Long seq = 0L; // 순서
}
