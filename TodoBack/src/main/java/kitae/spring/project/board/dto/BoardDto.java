package kitae.spring.project.board.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.LocalDateTime;

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
}
