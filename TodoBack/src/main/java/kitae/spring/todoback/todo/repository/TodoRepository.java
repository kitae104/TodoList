package kitae.spring.todoback.todo.repository;

import kitae.spring.todoback.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Optional<Todo> findByCode(String code);

    Page<Todo> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Todo t set t.status = true where t.status = false")
    int completeAll();


}
