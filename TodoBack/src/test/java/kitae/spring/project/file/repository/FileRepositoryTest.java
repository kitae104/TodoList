package kitae.spring.project.file.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileRepositoryTest {

  @Autowired
  private FileRepository fileRepository;

  @Test
  void deleteFilesById() {
    int deletedCount = fileRepository.deleteFilesById(List.of(5L, 6L));
    assertEquals(2, deletedCount);
  }
}