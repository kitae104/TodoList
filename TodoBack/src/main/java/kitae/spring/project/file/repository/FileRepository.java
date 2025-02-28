package kitae.spring.project.file.repository;

import kitae.spring.project.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

  // 부모 기준 목록
  @Query("SELECT f FROM File f WHERE f.parentTable = :parentTable AND f.parentNo = :parentNo")
  List<File> findByParent(@Param("parentTable") String parentTable, @Param("parentNo") Long parentNo);

  // 부모 기준 삭제
  @Query("DELETE FROM File f WHERE f.parentTable = :parentTable AND f.parentNo = :parentNo")
  public int deleteByParent(@Param("parentTable") String parentTable, @Param("parentNo") Long parentNo);
}
