package kitae.spring.todoback.todo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDto {
    private Long id;
    private String code;
    private String name;
    private Boolean status;
    private Integer seq;
    private LocalDateTime regTime;
    private LocalDateTime updateTime;
}
