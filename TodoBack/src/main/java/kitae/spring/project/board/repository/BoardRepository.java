package kitae.spring.project.board.repository;

import kitae.spring.project.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Page<Board> findAll(Pageable pageable);

  @Query("SELECT b FROM Board b LEFT JOIN FETCH b.file WHERE b.id = :id")
  Optional<Board> findByIdWithFile(@Param("id") Long id);
}
