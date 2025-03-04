package kitae.spring.project.board.entity;

import jakarta.persistence.*;
import kitae.spring.project.common.entity.BaseEntity;
import kitae.spring.project.file.entity.FileEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id", nullable = false)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 100, nullable = false)
    private String writer;

    @Lob
    private String content;

    // Board가 삭제될 때 관련된 파일도 함께 삭제됨
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> files;
}
