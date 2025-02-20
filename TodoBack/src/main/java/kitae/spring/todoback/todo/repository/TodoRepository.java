package kitae.spring.todoback.todo.repository;

import kitae.spring.todoback.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Optional<Todo> findByCode(String code);

    Page<Todo> findAllByOrderByStatusAscSeqAsc(Pageable pageable);
}
