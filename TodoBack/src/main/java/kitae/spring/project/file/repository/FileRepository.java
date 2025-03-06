package kitae.spring.project.file.repository;

import kitae.spring.project.file.entity.FileEntity;
import kitae.spring.project.file.entity.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

  // 부모 기준 목록
  @Query("SELECT f FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentId = :parentId")
  List<FileEntity> findByParent(@Param("parentTable") String parentTable, @Param("parentId") Long parentId);

  // 부모 기준 삭제
  @Query("DELETE FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentId = :parentId")
  public int deleteByParent(@Param("parentTable") String parentTable, @Param("parentId") Long parentId);

  // 아이디 목록 삭제
  @Modifying
  @Transactional
  @Query("DELETE FROM FileEntity f WHERE f.id IN :idList")
  public int deleteFilesById(@Param("idList") List<Long> idList);

  @Query("SELECT f FROM FileEntity f WHERE f.parentId=:id AND f.parentTable = 'board' AND f.type = 'MAIN'")
  public FileEntity selectMainFile(@Param("id") Long id);

  @Query("SELECT f FROM FileEntity f WHERE f.board.id = :id")
  public List<FileEntity> findByIdWithFile(@Param("id") Long id);

  // Main 타입 조회
  @Query("SELECT f FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentId = :parentId AND f.type = :type")
  public FileEntity selectByType(@Param("parentTable") String parentTable, @Param("parentId") Long parentId, @Param("type") FileType type);

  // Sub 타입 목록 조회
  @Query("SELECT f FROM FileEntity f WHERE f.parentTable = :parentTable AND f.parentId = :parentId AND f.type = :type")
  List<FileEntity> listByType(@Param("parentTable") String parentTable, @Param("parentId") Long parentId, @Param("type") FileType type);
}
