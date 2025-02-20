package kitae.spring.todoback.todo.entity;

import jakarta.persistence.*;
import kitae.spring.todoback.utils.audit.BaseEntity;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id", nullable = false)
    private Long id;

    @Column(length = 64, nullable = false, unique = true)
    private String code;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean status;

    @Column(nullable = false, updatable = false, columnDefinition = "int default 0")
    private Integer seq;
}
