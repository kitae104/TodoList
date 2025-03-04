package kitae.spring.project.board.repository;

import kitae.spring.project.board.entity.Board;
import kitae.spring.project.file.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
  Page<Board> findAll(Pageable pageable);

}
