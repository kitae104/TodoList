package kitae.spring.project.file.repository;

import kitae.spring.project.file.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  // 부모 기준 목록
  @Query("SELECT f FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentNo = :parentNo")
  List<FileEntity> findByParent(@Param("parentTable") String parentTable, @Param("parentNo") Long parentNo);

  // 부모 기준 삭제
  @Query("DELETE FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentNo = :parentNo")
  public int deleteByParent(@Param("parentTable") String parentTable, @Param("parentNo") Long parentNo);

  // 아이디 목록 삭제
  @Modifying
  @Transactional
  @Query("DELETE FROM FileEntity f WHERE f.id IN :idList")
  public int deleteFilesById(@Param("idList") List<Long> idList);
}
